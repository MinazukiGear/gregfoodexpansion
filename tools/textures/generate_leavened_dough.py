# -*- coding: utf-8 -*-
"""
发酵面团 16x16 贴图(2026-09-08,酵母接入烘焙)。
暖白面团上拱 + 发酵气孔,与 GT 面团(死面)区分。

用法:python generate_leavened_dough.py [--out DIR]
"""
import argparse
import os
import random

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
DEFAULT_OUT = os.path.normpath(os.path.join(
    HERE, "..", "..", "src", "main", "resources", "assets",
    "gregfoodexpansion", "textures", "item"))

BASE = (243, 232, 205)   # 暖白面团
SHADE = (219, 202, 165)  # 下缘阴影
RIM = (198, 178, 138)    # 轮廓阴影
BUBBLE = (226, 210, 175) # 气孔


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=DEFAULT_OUT)
    args = ap.parse_args()
    os.makedirs(args.out, exist_ok=True)

    rng = random.Random(7)
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))

    # 上拱面团轮廓:上窄下宽,底部平
    rows = {
        4: range(6, 10),
        5: range(5, 11),
        6: range(4, 12),
        7: range(4, 12),
        8: range(3, 13),
        9: range(3, 13),
        10: range(3, 13),
        11: range(3, 13),
        12: range(3, 13),
    }
    for y, xs in rows.items():
        for x in xs:
            im.putpixel((x, y), (*BASE, 255))

    # 轮廓阴影:最外圈
    for y, xs in rows.items():
        x0, x1 = min(xs), max(xs)
        im.putpixel((x0, y), (*RIM, 255))
        im.putpixel((x1, y), (*RIM, 255))
    for x in range(3, 13):
        im.putpixel((x, 12), (*SHADE, 255))

    # 顶部高光带
    for x in range(6, 10):
        im.putpixel((x, 5), (250, 243, 224, 255))

    # 发酵气孔(暗点,中下部密集)
    for _ in range(9):
        y = rng.randint(8, 11)
        x = rng.randint(4, 11)
        im.putpixel((x, y), (*BUBBLE, 255))
    # 固定两枚大孔增强"发起来"的读感
    im.putpixel((6, 9), (*BUBBLE, 255))
    im.putpixel((9, 10), (*BUBBLE, 255))

    out = os.path.join(args.out, "leavened_dough.png")
    im.save(out)
    print("saved:", out)


if __name__ == "__main__":
    main()
