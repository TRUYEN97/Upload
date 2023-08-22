from tkinter import *
from interface import *
import cv2
from PIL import ImageTk
import PIL


class ImageWidget(Canvas, ImgShow):
    def __init__(self, master=None, cnf={}, **kw):
        super().__init__(master, cnf, **kw)
        self.__frame_w = 640
        self.__frame_h = 480
        self.__img = None
        self.__w_ratio = 0
        self.__h_ratio = 0

    def showimg(self, frame):
        size = self.__canvas_size()
        if size[0] == 1 or size[1] == 1:
            return
        self.__frame_h, self.__frame_w, _ = frame.shape
        self.__w_ratio = size[0] / self.__frame_w
        self.__h_ratio = size[1] / self.__frame_h
        frame = cv2.resize(frame, size, cv2.INTER_LANCZOS4)
        image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        image = PIL.Image.fromarray(image)
        image = ImageTk.PhotoImage(image=image)
        self.__img = image
        self.create_image(0, 0, anchor=NW, image=image)

    def __canvas_size(self):
        return tuple((self.winfo_width(), self.winfo_height()))

    @property
    def frame_h(self):
        return int(self.__frame_h)

    @property
    def frame_w(self):
        return int(self.__frame_w)

    @property
    def width(self):
        return int(self.winfo_width())

    @property
    def height(self):
        return int(self.winfo_height())

    @property
    def w_ratio(self):
        return self.__w_ratio

    @property
    def h_ratio(self):
        return self.__h_ratio
