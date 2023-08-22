from frame_setup import *
import os
import json

TAB_SELECTED = 'selected'

TEST_MODE = "test"

SETUP_MODE = 'setup'

MODE = 'mode'

CONFIG = 'config'

STATUS = 'status'

ACTION = 'action'

MESSAGE = 'message'

DATA = 'data'


class MyNoteBook(ttk.Notebook, ImgShow):
    def __init__(self, master, labels: str = [None], config_path: str = "items.json", **kw):
        super().__init__(master, **kw)
        self.init_dummy_theme()
        self.__img_home = ImageWidget(self)
        self.add(self.__img_home, text='Home', sticky=NSEW)
        self.__labels = labels
        self.bind("<<NotebookTabChanged>>", self.__tab_pressed)
        popup_menu = Menu(self, tearoff=0)
        popup_menu.add_command(label="add location", command=self.__add_location)
        popup_menu.add_command(label="remove location", command=self.__remove_location)
        self.bind("<Button-3>", lambda event: popup_menu.post(event.x_root, event.y_root))
        if os.path.exists(config_path):
            with open(config_path, "r") as file:
                try:
                    config = json.load(file)
                    for item in config:
                        self.add_location(config=item)
                except:
                    print("load config failed")
        self.__config_path = config_path

    @staticmethod
    def init_dummy_theme():
        Mysky = "#DCF0F2"
        Myyellow = "#F2C84B"
        style = ttk.Style()
        style.theme_create("dummy", parent="alt", settings={
            "TNotebook": {"configure": {"tabmargins": [2, 5, 2, 0]}},
            "TNotebook.Tab": {
                "configure": {"padding": [5, 1], "background": Mysky},
                "map": {"background": [("selected", Myyellow)],
                        "expand": [("selected", [1, 1, 1, 0])]}}})
        style.theme_use("dummy")

    def __add_location(self):
        self.add_location()

    def __remove_location(self):
        index = self.select_index()
        if index != 0:
            self.forget(index)

    def add_location(self, config={}):
        frame = FrameSetup(self, self.__labels, config)
        self.add(frame, text=frame.name)

    @property
    def configs(self):
        config = {}
        item_configs = []
        config[CONFIG] = item_configs
        config[MODE] = SETUP_MODE
        config[TAB_SELECTED] = self.select_index()
        tabs = self.tabs()
        if config[TAB_SELECTED] == 0:
            config[MODE] = TEST_MODE
            for tab_name in tabs:
                tab = self.get_tab_instance(tab_name)
                if isinstance(tab, FrameSetup):
                    item_configs.append(tab.getConfig)
        else:
            tab = self.get_tab_instance()
            if isinstance(tab, FrameSetup):
                item_configs.append(tab.getConfig)
        return config

    def select_index(self):
        return self.index(self.select())

    def get_tab_instance(self, index: str = None):
        if index is None:
            index = self.select()
        return self.nametowidget(index)

    def __tab_pressed(self, event):
        if not os.path.exists(self.__config_path):
            os.makedirs(os.path.dirname(self.__config_path))
        index = self.select_index()
        if index == 0:
            with open(self.__config_path, "w+") as file:
                config = self.configs[CONFIG]
                json.dump(config, file)

    def showimg(self, img):
        frame = self.get_tab_instance()
        frame.showimg(img)
