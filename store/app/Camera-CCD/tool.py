import time
import torch
from threading import Thread
import os
from Socket import Server
from my_notebook import *
from utils.torch_utils import select_device, TracedModel
from models.experimental import attempt_load
from utils.general import non_max_suppression, scale_coords
from utils.plots import plot_one_box
import numpy as np
from frame_setup import *
import cv2
from pyzbar import pyzbar
from pylibdmtx.pylibdmtx import decode
import json


class Camera:
    def __init__(self, cam_id: int = 0):
        self.__success: bool = False
        cap = cv2.VideoCapture(cam_id)
        assert cap.isOpened(), f'Failed to open cam {cam_id}'
        w = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
        h = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
        self.__fps = cap.get(cv2.CAP_PROP_FPS) % 100
        self.__img = np.zeros((h, w, 3), dtype=int)
        thread = Thread(target=self.update, args=([cap]), daemon=True)
        print(f' success ({w}x{h} at {self.__fps:.2f} FPS).')
        thread.start()
        self.__cap = cap

    def update(self, capture):
        while capture.isOpened():
            self.__success, im = capture.read()
            self.__img = im if self.__success else self.__img * 0
            time.sleep(1 / self.__fps)

    @property
    def success(self):
        return self.__success

    def image(self):
        img0 = self.__img.copy()
        img = letterbox(img0, 640, stride=32)[0]
        img = img[:, :, ::-1].transpose(2, 0, 1)  # BGR to RGB, to 3x416x416
        img = np.ascontiguousarray(img)
        return img, img0

    @property
    def fps(self):
        return self.__fps

    def release(self):
        self.__cap.release()


class YoloDetect:
    def __init__(self, weights: str):
        self.__code = None
        self.__img = None
        self.__mode = None
        self.__socket = None
        self.__qrcode = None
        self.__qrcode_curr = None
        self.__thread_rLabel = None
        self.__thread_run_model = None
        self.__pred = None
        self.__old_tab_selected = 0
        model = attempt_load(weights, map_location='cpu')
        device = select_device('cpu')
        model = TracedModel(model, device, 640)
        names = model.module.names if hasattr(model, 'module') else model.names
        self.__model = model
        self.__device = device
        self.__names = names
        self.__data = {}

    @property
    def getName(self):
        return self.__names

    def detect(self, img, objs):
        self.__data = data = {ACTION: 'test'}
        img, img0 = self.__pretreatment(img)
        self.__img = img0.copy()
        if self.__thread_run_model is None or not self.__thread_run_model.is_alive():
            self.__thread_run_model = Thread(target=self.__detect_model, args=(img,))
            self.__thread_run_model.start()
        item_configs = objs[CONFIG]
        self.__mode = objs[MODE]
        data[MODE] = objs[MODE]
        data[DATA] = data_items = []
        if self.__pred is not None:
            tab_selected = objs[TAB_SELECTED]
            if tab_selected != self.__old_tab_selected:
                cv2.destroyAllWindows()
                self.__old_tab_selected = tab_selected
                self.__qrcode = None
            for item in item_configs:
                target_name = item[LABEL]
                target_percent = item[PERCENT]
                xyxy = item[X_START], item[Y_START], item[X_END], item[Y_END]
                st, percent, area, xyxy1 = self.__objectContainItem(img, img0, item)
                if target_name == LABEL:
                    img_crop = img0[xyxy1[1]:xyxy1[3], xyxy1[0]:xyxy1[2]]
                    if self.__thread_rLabel is None or not self.__thread_rLabel.is_alive():
                        self.__thread_rLabel = Thread(target=self.__qrDetection, args=(img_crop.copy(),))
                        self.__thread_rLabel.start()
                    st = self.__qrcode_curr is not None
                    label = f'{target_name}: {self.__qrcode_curr}'
                else:
                    data_item = {}
                    data_items.append(data_item)
                    data_item[STATUS] = st
                    data_item[LABEL] = target_name
                    data_item[PERCENT] = f'{percent}/{target_percent}%'
                    data_item[AREA] = f'{item[LOWER_AREA]}/{area}/{item[UPPER_AREA]}'
                    label = f'{target_name}: {percent: 0.2f}/{target_percent}%\r\n{area}'
                plot_one_box(xyxy, img0, label=label, color=item[COLOR], line_thickness=2, status=st)
        data[STATUS], data[MESSAGE] = self.checkRs(data_items)
        self.resetCode(data_items)
        self.__sendQRCode()
        return img0

    def resetCode(self, data_items):
        if len(data_items) == 0:
            self.__code = None
        for item in data_items:
            st = item[STATUS]
            if st is not None and st == TRUE:
                return
        self.__code = None

    @staticmethod
    def checkRs(data_items):
        if len(data_items) == 0:
            return False, 'Nothing to detect'
        for item in data_items:
            st = item[STATUS]
            if st is None or st != TRUE:
                return False, item[LABEL]
        return True, 'OK'

    def __pretreatment(self, img):
        img, img0 = img[0], img[1]
        img = torch.from_numpy(img).to(self.__device)
        img = img.float()  # uint8 to fpt32
        img /= 255.0  # 0 - 255 to 0.0 - 1.0
        if img.ndimension() == 3:
            img = img.unsqueeze(0)
        return img, img0

    def data(self, code: str):
        if self.__code is not None and code != self.__code:
            return json.dumps({STATUS: False, ACTION: 'replace'
                                  , MESSAGE: "Please replace the product with another one."})
        self.__code = code
        return json.dumps(self.__data)

    def __sendQRCode(self):
        if self.__socket and self.__mode == TEST_MODE and self.__qrcode_curr and self.__qrcode_curr != self.__qrcode:
            self.__socket.send(f'[{self.__qrcode_curr}]')
            self.__qrcode = self.__qrcode_curr

    def __detect_model(self, img):
        with torch.no_grad():
            self.__pred = self.__model(img, augment=False)[0]
        self.__pred = non_max_suppression(self.__pred, 0.25, 0.45, None, False)

    def socket(self, socket: Server):
        if socket:
            self.__socket = socket

    def __qrDetection(self, img):
        try:
            barcode_data = None
            barcodes = pyzbar.decode(img)
            for barcode in barcodes:
                barcode_data = barcode.data.decode('utf-8')
            if barcode_data is not None:
                self.__qrcode_curr = barcode_data
            else:
                img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
                barcode_data = decode(img)
                if barcode_data is not None or len(barcode_data) > 0:
                    self.__qrcode_curr = barcode_data[0].data.decode('utf-8')
                else:
                    self.__qrcode_curr = None
        except:
            self.__qrcode_curr = None

    def __objectContainItem(self, img, img0, item):
        xyxy = item[X_START], item[Y_START], item[X_END], item[Y_END]
        target_name = item[LABEL]
        target_percent = item[PERCENT]
        stt = False
        percent = 0
        xyxy1 = xyxy
        area = 0
        thresh_img = None
        for i, det in enumerate(self.__pred):
            if len(det):
                det[:, :4] = scale_coords(img.shape[2:], det[:, :4], img0.shape).round()
                for *xyxy1, conf, cls in reversed(det):
                    xyxy1 = [int(x) for x in xyxy1]
                    name = self.__names[int(cls)]
                    if target_name == name and self.is_square_inside(xyxy1, xyxy):
                        percent = int(conf * 100)
                        st, area, thresh_img = self.__checkContourArea(img0[xyxy1[1]:xyxy1[3], xyxy1[0]:xyxy1[2]], item)
                        if (st and percent >= target_percent) or target_name == LABEL:
                            stt = True
                            break
        if self.__mode == SETUP_MODE and thresh_img is not None:
            cv2.imshow('Threshold', thresh_img)
        return stt, percent, area, xyxy1

    @staticmethod
    def __checkContourArea(image, item):
        area = 0
        st = False
        thresh = None
        if image is not None:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            gray = cv2.GaussianBlur(gray, (5, 5), 0)
            _, thresh = cv2.threshold(gray, item[THRESHOLD], 255, cv2.THRESH_BINARY)
            thresh = cv2.dilate(thresh, None, iterations=1)
            thresh = cv2.erode(thresh, None, iterations=1)
            contours, _ = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            if contours and contours != ():
                largest_contour = max(contours, key=cv2.contourArea)
                area = int(cv2.contourArea(largest_contour))
                st = item[LOWER_AREA] <= area <= item[UPPER_AREA]
        return st, area, thresh

    @staticmethod
    def is_square_inside(xyxy, xyxy1):
        if len(xyxy) != 4 or len(xyxy1) != 4:
            return False
        x1, y1 = xyxy[0], xyxy[1]
        x2, y2 = xyxy[2], xyxy[3]
        x3, y3 = xyxy1[0], xyxy1[1]
        x4, y4 = xyxy1[2], xyxy1[3]
        if x1 >= x3 and y1 >= y3 and x2 <= x4 and y2 <= y4:
            return True
        else:
            return False

    def save_img(self, dataSk):
        data = {ACTION: 'save'}
        try:
            dir = dataSk['dir']
            name = dataSk['name']
            if not os.path.exists(dir):
                os.makedirs(dir)
            path = f'{dir}/{name}'
            cv2.imwrite(path, self.__img)
            data[STATUS] = True
            data[MESSAGE] = path
        except Exception as e:
            data[STATUS] = False
            data[MESSAGE] = str(e)
        return json.dumps(data)


def letterbox(img, new_shape=(640, 640), color=(114, 114, 114), scaleup=True, stride=32):
    # Resize and pad image while meeting stride-multiple constraints
    shape = img.shape[:2]  # current shape [height, width]
    if isinstance(new_shape, int):
        new_shape = (new_shape, new_shape)

    # Scale ratio (new / old)
    r = min(new_shape[0] / shape[0], new_shape[1] / shape[1])

    # Compute padding
    ratio = r, r  # width, height ratios
    new_unpad = int(round(shape[1] * r)), int(round(shape[0] * r))
    dw, dh = new_shape[1] - new_unpad[0], new_shape[0] - new_unpad[1]  # wh padding
    dw, dh = np.mod(dw, stride), np.mod(dh, stride)  # wh padding
    dw /= 2  # divide padding into 2 sides
    dh /= 2

    if shape[::-1] != new_unpad:  # resize
        img = cv2.resize(img, new_unpad, interpolation=cv2.INTER_LINEAR)
    top, bottom = int(round(dh - 0.1)), int(round(dh + 0.1))
    left, right = int(round(dw - 0.1)), int(round(dw + 0.1))
    img = cv2.copyMakeBorder(img, top, bottom, left, right, cv2.BORDER_CONSTANT, value=color)  # add border
    return img, ratio, (dw, dh)
