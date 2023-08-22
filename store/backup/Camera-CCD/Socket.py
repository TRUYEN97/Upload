import json
import socket
from threading import Thread

from my_notebook import ACTION


class Server:
    def __init__(self, port, detector=None):
        self.__host = 'localhost'
        self.__port = port
        self.__detector = detector
        self.__thread: Thread = None
        self.__clients = {}

    def run(self) -> None:
        if not self.isRunning():
            self.__thread = Thread(target=self.__run_server, args=(), daemon=True)
            self.__thread.start()

    def isRunning(self):
        return self.__thread is not None and self.__thread.is_alive()

    def __run_server(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind((self.__host, self.__port))
            s.listen()
            while True:
                conn, addr = s.accept()
                self.__clients[conn] = ClientHandel(conn=conn, addr=addr,
                                                    listClient=self.__clients, detector=self.__detector)

    def disconnectAll(self):
        for client in self.__clients:
            client.disconnect()

    def send(self, data):
        if self.isRunning():
            for client in self.__clients.values():
                client.send(data)


class ClientHandel:

    def __init__(self, conn: socket, addr, detector, listClient: dict):
        self.__conn = conn
        self.__addr = addr
        self.__detector = detector
        self.__listClient = listClient
        print(f"Connected by {self.__addr}")
        print(len(self.__listClient))
        self.__thread = Thread(target=self.__run, daemon=True)
        self.__thread.start()
        self.__run = True

    def __run(self):
        with self.__conn as conn:
            while self.__run:
                try:
                    data_bytes = conn.recv(1024)
                    if not data_bytes:
                        break
                    str_ck = data_bytes.decode('utf-8').strip()
                except Exception as e:
                    print(e)
                    self.send(e)
                    continue
                print(str_ck)
                data = self.__cvt2json(str_ck)
                print(data)
                if data is None:
                    continue
                action = data[ACTION]
                match action:
                    case "test":
                        self.send(self.__detector.data(data['code']))
                    case "save":
                        self.send(self.__detector.save_img(data))
        print(f"Disconnected by {self.__addr}")
        self.__listClient.pop(self.__conn)

    @staticmethod
    def __cvt2json(str_json: str):
        try:
            return json.loads(str_json)
        except Exception as e:
            print(e)
            return None

    def send(self, data):
        try:
            if self.__conn and data is not None:
                data = f'{data}\r\n'
                print(data)
                self.__conn.sendall(data.encode())
        except Exception as e:
            self.disconnect(self)
            print(e)

    def disconnect(self):
        self.__run = False
