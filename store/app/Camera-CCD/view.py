import tkinter.constants
from tkinter import messagebox
from my_notebook import *
import re
from interface import *
from Event import *


class Windows(Tk, ImgShow):

    def __init__(self, title: str = '', values: str = [], config_path: str = ".", icon: str = None,
                 confirm_on_quit: bool = False,
                 size: str = None,
                 center: bool = True):
        """
            size="widthxheight"
        """
        super().__init__()
        self.__initialize(title, icon, size, center)
        self.__initUI(values, config_path)
        self.wm_protocol("WM_DELETE_WINDOW", lambda: self.__on_quit(confirm_on_quit))

    def __screen_location(self, size: str, center: bool):
        if size is None or not re.match("^(\\d+x\\d+)(\\+\\d+\\+\\d)?$", size):
            self.wm_state('zoomed')
            return
        if center:
            xy = re.findall("\\d+", size, 2)
            width = int(xy[0])
            height = int(xy[1])
            x = int(self.winfo_screenwidth() / 2 - width / 2)
            y = int(self.winfo_screenheight() / 2 - height / 2)
            size = '{}x{}+{}+{}'.format(width, height, x, y)
        self.geometry(size)

    def __initUI(self, labels, config_path):
        frame = Frame(self, bg='black', padx=2, pady=2)
        frame.grid_columnconfigure(0, weight=1)
        frame.grid_rowconfigure(0, weight=1)
        frame.grid(row=0, column=0, padx=2, pady=4, sticky=tkinter.NSEW)

        self.__notebook = MyNoteBook(master=frame, labels=labels, config_path=config_path)
        self.__notebook.grid(row=0, column=0, sticky=tkinter.NSEW)

    def __initialize(self, title: str, icon: str, size: str, center: bool):
        self.wm_title(title)
        self.__screen_location(size, center)
        self.configure(bg="green")

        try:
            self.wm_iconbitmap(icon)
        except Exception:
            print("Icon file not found: ", icon)

        self.grid_columnconfigure(0, weight=1, minsize=320)
        self.grid_rowconfigure(0, weight=1, minsize=320)

    def __on_quit(self, confirm_on_quit: bool):
        if not confirm_on_quit or messagebox.askokcancel("Warning", "Do you want to quit?"):
            self.destroy()

    def showimg(self, frame):
        self.__notebook.showimg(frame)

    @property
    def getConfig(self):
        return self.__notebook.configs

    @property
    def pcname(self):
        return self.__pc_name
