from tool import *
from view import *
from my_notebook import *


class App:

    def __init__(self, size="800x600", yolo_model: str = None, config_path: str = None, camera_id: int = 0):
        self.__label = None
        self.__cam = Camera(camera_id)
        self.__yolo = YoloDetect(yolo_model)
        self.__server = Server(port=60026, detector=self.__yolo)
        self.__yolo.socket(self.__server)
        self.__server.run()
        self.__windows = Windows('Detector', values=self.__yolo.getName,
                                 config_path=config_path, size=size, confirm_on_quit=True)
        self.run()
        self.__windows.mainloop()
        self.__cam.release()

    def run(self):
        if self.__cam.success:
            img = self.__cam.image()
            img_rs = self.__yolo.detect(img, self.__windows.getConfig)
            if img_rs is not None:
                self.__windows.showimg(img_rs)
        self.__windows.after(250, func=self.run)


if __name__ == '__main__':
    App(size="640x480", yolo_model="yolo/best.pt", config_path="C:/CCD-config/items.json", camera_id=0)
