from tkinter import *


class RectMouseEvent:
    def __init__(self, canvas: Canvas, mouse_move=None, button_release=None):
        self.__canvas = canvas
        self.__y_stt = 0
        self.__x_stt = 0
        self.__x_start = 0
        self.__y_start = 0
        self.__x_end = 0
        self.__y_end = 0
        self.__mouse_move = mouse_move
        self.__button_release = button_release
        self.__img = None

    def on_button_press(self, event):
        self.__x_stt, self.__y_stt = self.__get_location(event)

    def on_button_release(self, event):
        x, y = self.__get_location(event)
        if self.__x_stt == x or self.__y_stt == y:
            return
        else:
            self.__x_start = self.__x_stt
            self.__y_start = self.__y_stt
            self.__x_end = x
            self.__y_end = y
            if self.__button_release is not None:
                self.__button_release((self.__x_start, self.__y_start), (x, y))

    def on_mouse_move(self, event):
        x, y = self.__get_location(event)
        if self.__mouse_move is not None:
            self.__mouse_move((self.__x_stt, self.__y_stt), (x, y))

    def __get_location(self, event):
        w = int(self.__canvas.winfo_width())
        h = int(self.__canvas.winfo_height())
        x = int(self.__canvas.canvasx(event.x))
        y = int(self.__canvas.canvasx(event.y))
        x = x if x >= 0 else 0
        y = y if y >= 0 else 0
        x = x if x < w else w
        y = y if y < h else h
        return x, y

    @property
    def x_start(self):
        return self.__x_start

    @property
    def y_start(self):
        return self.__y_start

    @property
    def x_end(self):
        return self.__x_end

    @property
    def y_end(self):
        return self.__y_end
