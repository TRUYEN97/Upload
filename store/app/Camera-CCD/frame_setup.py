import random
from builtins import int

from Event import *
from image_widget import *
from tkinter import ttk
from interface import *

THRESHOLD = "threshold"

UPPER_AREA = "upperArea"

LOWER_AREA = "lowerArea"

PERCENT = "percent"

AREA = "area"

Y_END = "y_end"

X_END = "x_end"

Y_START = "y_start"

X_START = "x_start"

COLOR = "color"

LABEL = "label"


class FrameSetup(Frame, ImgShow):
    def __init__(self, master=None, values: list = [None], config: dict = {}, cnf={},
                 **kw):
        super().__init__(master, cnf, **kw)
        self.notebook = master
        self.grid_rowconfigure(1, weight=1)
        self.grid_columnconfigure(0, weight=1)
        self.__init_setup(values, config.get(LABEL, None))
        self.__init_img_frame()
        self.__init_value(config)
        self.__event = RectMouseEvent(self.__img_widget, mouse_move=self.__mouse_move)
        self.__img_widget.bind("<ButtonPress-1>", self.__event.on_button_press)
        self.__img_widget.bind("<ButtonRelease-1>", self.__event.on_button_release)
        self.__img_widget.bind("<B1-Motion>", self.__event.on_mouse_move)

    def __init_setup(self, values: list, item: str = None):
        setup_frame = Frame(self, bg='gray')
        setup_frame.grid_rowconfigure(0, weight=1)
        setup_frame.grid_rowconfigure(1, weight=1)
        setup_frame.grid_columnconfigure(0, weight=1)
        setup_frame.grid_columnconfigure(1, weight=1)
        setup_frame.grid(row=0, sticky=NSEW)

        select_frame = LabelFrame(setup_frame, text='select name', bg='green')
        select_frame.grid(row=0, column=0, sticky=NSEW, pady=2, padx=2)
        self.__cbb_name = ttk.Combobox(select_frame, values=values, textvariable=StringVar, state='readonly')
        self.__cbb_name.bind("<<ComboboxSelected>>", self.__on_select)
        self.__cbb_name.current(values.index(item) if item in values else 0)
        self.__cbb_name.pack(expand=True, fill=X, pady=5, padx=5)


        #
        scale_frame = Frame(setup_frame, bg='green')
        scale_frame.grid(row=0, column=1, sticky=NSEW, pady=2, padx=2)
        scale_frame.grid_columnconfigure(0, weight=1)
        scale_frame.grid_columnconfigure(1, weight=1)

        threshold_frame = LabelFrame(scale_frame, text='Threshold', bg='green')
        threshold_frame.grid(row=0, column=0, sticky=NSEW)
        self.__threshold = Scale(threshold_frame, variable=IntVar, from_=0, to=255, orient=HORIZONTAL)
        self.__threshold.pack(expand=True, fill=X, pady=5, padx=5)

        percent_frame = LabelFrame(scale_frame, text='Percent', bg='green')
        percent_frame.grid(row=0, column=1, sticky=NSEW)
        self.__scale = Scale(percent_frame, variable=IntVar, from_=1, to=100, orient=HORIZONTAL)
        self.__scale.pack(expand=True, fill=X, pady=5, padx=5)

        #
        location_frame = LabelFrame(setup_frame, text='location', bg='green')
        location_frame.grid_columnconfigure(0, weight=1)
        location_frame.grid_columnconfigure(1, weight=1)
        location_frame.grid_columnconfigure(2, weight=1)
        location_frame.grid_columnconfigure(3, weight=1)
        location_frame.grid(row=1, column=0, sticky=NSEW, pady=2, padx=2)

        Label(location_frame, justify=CENTER, text='x_start').grid(row=0, column=0, sticky=NSEW)
        self.__x_start = Entry(location_frame, justify=CENTER, width=10, textvariable=IntVar)
        self.__x_start.config(validate='key', validatecommand=(self.register(self.__validata), '%P'))
        self.__x_start.grid(row=1, column=0, sticky=NSEW)

        Label(location_frame, justify=CENTER, text='y_start').grid(row=0, column=1, sticky=NSEW)
        self.__y_start = Entry(location_frame, justify=CENTER, width=10, textvariable=IntVar)
        self.__y_start.config(validate='key', validatecommand=(self.register(self.__validata), '%P'))
        self.__y_start.grid(row=1, column=1, sticky=NSEW)

        Label(location_frame, justify=CENTER, text='x_end').grid(row=0, column=2, sticky=NSEW)
        self.__x_end = Entry(location_frame, justify=CENTER, width=10, textvariable=IntVar)
        self.__x_end.config(validate='key', validatecommand=(self.register(self.__validata), '%P'))
        self.__x_end.grid(row=1, column=2, sticky=NSEW)

        Label(location_frame, justify=CENTER, text='y_end').grid(row=0, column=3, sticky=NSEW)
        self.__y_end = Entry(location_frame, justify=CENTER, width=10, textvariable=IntVar)
        self.__y_end.config(validate='key', validatecommand=(self.register(self.__validata), '%P'))
        self.__y_end.grid(row=1, column=3, sticky=NSEW)

        #
        area_frame = LabelFrame(setup_frame, text='Area limit', bg='green')
        area_frame.grid_columnconfigure(0, weight=1)
        area_frame.grid_columnconfigure(1, weight=1)
        area_frame.grid(row=1, column=1, sticky=NSEW, pady=2, padx=2)

        Label(area_frame, justify=CENTER, text='lower area').grid(row=0, column=0, sticky=NSEW)
        self.__lower_limit = Entry(area_frame, textvariable=IntVar, justify=CENTER)
        self.__lower_limit.config(validate='key', validatecommand=(self.register(self.__lower_validate), '%P'))
        self.__lower_limit.grid(row=1, column=0, sticky=NSEW)

        Label(area_frame, justify=CENTER, text='upper area').grid(row=0, column=1, sticky=NSEW)
        self.__upper_limit = Entry(area_frame, textvariable=IntVar, justify=CENTER)
        self.__upper_limit.config(validate='key', validatecommand=(self.register(self.__upper_validate), '%P'))
        self.__upper_limit.grid(row=1, column=1, sticky=NSEW)

    def __init_img_frame(self):
        img_frame = Frame(self)
        img_frame.grid(row=1, sticky=NSEW)
        self.__img_widget = ImageWidget(img_frame, bg='green')
        self.__img_widget.pack(expand=True, fill=BOTH)

    def __init_value(self, config: dict):
        FrameSetup.__set_val(self.__x_start, config.get(X_START, 0))
        FrameSetup.__set_val(self.__y_start, config.get(Y_START, 0))
        FrameSetup.__set_val(self.__x_end, config.get(X_END, 0))
        FrameSetup.__set_val(self.__y_end, config.get(Y_END, 0))
        FrameSetup.__set_val(self.__lower_limit, config.get(LOWER_AREA, 0))
        FrameSetup.__set_val(self.__upper_limit, config.get(UPPER_AREA, 2))
        self.__scale.set(config.get(PERCENT, 50))
        self.__threshold.set(config.get(THRESHOLD, 90))
        self.__color = config.get(COLOR, [random.randint(0, 255) for _ in range(3)])

    def showimg(self, frame):
        self.__img_widget.showimg(frame)

    @property
    def name(self):
        return self.__cbb_name.get()

    def __on_select(self, event):
        selected_item = event.widget.get()
        index = self.notebook.index(self.notebook.select())
        self.notebook.tab(index, text=selected_item)

    def __mouse_move(self, start: tuple, end: tuple):
        xs, ys = start
        xe, ye = end
        w_ratio = self.__img_widget.w_ratio
        h_ratio = self.__img_widget.h_ratio
        FrameSetup.__set_val(self.__x_start, int(xs / w_ratio))
        FrameSetup.__set_val(self.__y_start, int(ys / h_ratio))
        FrameSetup.__set_val(self.__x_end, int(xe / w_ratio))
        FrameSetup.__set_val(self.__y_end, int(ye / h_ratio))

    @property
    def getConfig(self):
        return {
            LABEL: self.__cbb_name.get(),
            COLOR: list(self.__color),
            X_START: int(self.x_start),
            Y_START: int(self.y_start),
            X_END: int(self.x_end),
            Y_END: int(self.y_end),
            PERCENT: int(self.percent),
            LOWER_AREA: int(self.lowerLimit),
            UPPER_AREA: int(self.upperLimit),
            THRESHOLD: int(self.threshold),
        }

    @property
    def xy_start(self):
        return tuple((self.x_start, self.y_start))

    @property
    def xy_end(self):
        return tuple((self.x_end, self.y_end))

    @property
    def x_start(self):
        return self.__gset_location_val(self.__x_start, self.__img_widget.frame_w)

    @property
    def y_start(self):
        return self.__gset_location_val(self.__y_start, self.__img_widget.frame_h)

    @property
    def x_end(self):
        return self.__gset_location_val(self.__x_end, self.__img_widget.frame_w)

    @property
    def y_end(self):
        return self.__gset_location_val(self.__y_end, self.__img_widget.frame_h)

    @property
    def area_limit(self):
        return tuple((self.lowerLimit, self.upperLimit))

    @property
    def lowerLimit(self):
        return self.getIntValue(self.__lower_limit, 0)

    @property
    def upperLimit(self):
        return self.getIntValue(self.__upper_limit, int(self.lowerLimit + 1))

    @property
    def percent(self):
        return self.__scale.get()

    @property
    def threshold(self):
        return self.__threshold.get()

    @property
    def thresholdTo(self):
        return self.__thresholdTo.get()

    @property
    def color(self):
        return self.__color

    def __gset_location_val(self, entry: Entry, max_val: int):
        value = self.getIntValue(entry, 0)
        true_value = min(value, max_val)
        self.__set_val(entry, true_value)
        return true_value

    @staticmethod
    def __set_val(entry: Entry, value: int):
        entry.delete(0, END)
        entry.insert(END, value)

    @staticmethod
    def getIntValue(entry: Entry, default: int = 0):
        value = entry.get()
        if value is None or value == "" or not value.isdecimal():
            if default is not None:
                return int(default)
            else:
                return 0
        else:
            return int(value)

    @staticmethod
    def __validata(value):
        return value.isdecimal() or value == ""

    def __lower_validate(self, value):
        if value == "":
            return True
        if value.isdecimal():
            if self.upperLimit < int(value):
                self.__set_val(self.__upper_limit, int(value) + 1)
            return True
        else:
            return False

    def __upper_validate(self, value):
        if value == "":
            return True
        if value.isdecimal():
            if self.lowerLimit > int(value):
                self.__set_val(self.__lower_limit, int(value) - 1)
            return True
        else:
            return False
