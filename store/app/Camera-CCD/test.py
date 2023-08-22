import tkinter as tk
from tkinter import ttk

root = tk.Tk()
root.geometry("400x300")

notebook = ttk.Notebook(root)

class FrameSetup:
    def __init__(self, parent, labels, config):
        self.name = config["name"]
        self.color = config["color"]
        self.frame = tk.Frame(parent)
        self.labels = labels

        # Đặt màu sắc cho widget Frame của tab
        self.frame.configure(background=self.color)

        notebook.add(self.frame, text=self.name)

frames = [
    {"name": "Tab 1", "color": "#FF0000"},  # Màu đỏ
    {"name": "Tab 2", "color": "#00FF00"},  # Màu xanh lá cây
    {"name": "Tab 3", "color": "#0000FF"},  # Màu xanh dương
]

# Tạo các frame và thêm vào notebook
for frame_config in frames:
    frame = FrameSetup(root, [], frame_config)

notebook.pack(expand=True, fill="both")
root.mainloop()
