#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""死面菜肴 16x16 贴图(2026-09-08):葱油饼 / 煎饺 / 牛肉馅饼。
程序化绘制,与 generate_alcohol_textures.py 同风格(确定性 seed)。"""
import math
import os
import random

from PIL import Image, ImageDraw

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                   '..', '..', 'src', 'main', 'resources', 'assets',
                   'gregfoodexpansion', 'textures', 'item')


def canvas():
    return Image.new('RGBA', (16, 16), (0, 0, 0, 0))


def speckle(im, rng, colors, cx, cy, rx, ry, count):
    d = ImageDraw.Draw(im)
    for _ in range(count):
        a = rng.uniform(0, 2 * math.pi)
        r = rng.uniform(0, 1)
        x = int(cx + math.cos(a) * rx * r)
        y = int(cy + math.sin(a) * ry * r)
        c = rng.choice(colors)
        d.point((x, y), fill=c)


def scallion_pancake():
    """葱油饼:烙制的圆饼,表面葱绿碎点 + 焦斑。"""
    rng = random.Random(41)
    im = canvas()
    d = ImageDraw.Draw(im)
    # 圆饼主体(暖面色,微椭圆透视)
    d.ellipse((1, 4, 15, 13), fill=(224, 178, 118, 255), outline=(150, 104, 52, 255))
    d.ellipse((2, 5, 14, 12), fill=(236, 194, 136, 255))
    # 油煎焦斑
    speckle(im, rng, [(196, 142, 78, 255), (172, 116, 56, 255)], 8, 8.5, 5, 3, 14)
    # 葱绿碎点
    speckle(im, rng, [(96, 158, 66, 255), (128, 186, 88, 255)], 8, 8.5, 5, 3, 10)
    return im


def fried_dumplings():
    """煎饺:三只并排,底面金黄焦壳、顶面奶白。"""
    rng = random.Random(42)
    im = canvas()
    d = ImageDraw.Draw(im)
    for i, (cx, cy) in enumerate([(4, 6), (8, 8), (12, 10)]):
        # 饺身(斜置,半月形)
        d.polygon([(cx - 3, cy + 2), (cx - 2, cy - 2), (cx, cy - 3),
                   (cx + 2, cy - 2), (cx + 3, cy + 2)], fill=(238, 210, 160, 255),
                  outline=(168, 124, 62, 255))
        # 底面焦壳(下缘亮金)
        d.line([(cx - 3, cy + 2), (cx + 3, cy + 2)], fill=(206, 150, 74, 255))
        d.point((cx, cy + 2), fill=(180, 122, 52, 255))
        # 捏褶(顶部两刻点)
        d.point((cx - 1, cy - 3), fill=(190, 150, 92, 255))
        d.point((cx + 1, cy - 3), fill=(190, 150, 92, 255))
    speckle(im, rng, [(190, 134, 62, 255)], 8, 8, 6, 5, 6)
    return im


def beef_pie():
    """牛肉馅饼:圆烤饼,表面划口露出褐色肉馅。"""
    rng = random.Random(43)
    im = canvas()
    d = ImageDraw.Draw(im)
    # 饼体
    d.ellipse((1, 3, 15, 14), fill=(222, 172, 110, 255), outline=(146, 98, 48, 255))
    d.ellipse((2, 4, 14, 13), fill=(234, 190, 132, 255))
    # 中部划口露馅(两道裂口,深褐肉馅)
    d.line([(5, 8), (7, 6)], fill=(122, 74, 38, 255), width=2)
    d.line([(9, 6), (11, 8)], fill=(122, 74, 38, 255), width=2)
    speckle(im, rng, [(96, 56, 30, 255), (140, 88, 46, 255)], 8, 7.5, 3.5, 2, 8)
    # 边缘烤色
    speckle(im, rng, [(196, 142, 78, 255)], 8, 8.5, 6, 4, 8)
    return im


def main():
    os.makedirs(OUT, exist_ok=True)
    for name, im in [('scallion_pancake', scallion_pancake()),
                     ('fried_dumplings', fried_dumplings()),
                     ('beef_pie', beef_pie())]:
        im.save(os.path.join(OUT, name + '.png'))
        print('wrote', name)


if __name__ == '__main__':
    main()
