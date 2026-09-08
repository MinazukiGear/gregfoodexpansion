# -*- coding: utf-8 -*-
"""
酒线 16x16 贴图(alcohol-line.md,2026-09-08)。
用户定调:贴图暂用 16x16,64x64 程序化批次挂起。

产出(输出到 resources/assets/gregfoodexpansion/textures/item/):
- yeast.png  酵母(粉堆)
- malt.png   麦芽(谷粒)
- bottled_*.png ×9 瓶装酒(玻璃瓶 + 酒液色,酒液色与 GTFEMaterials 材料色一致)

用法:python generate_alcohol_textures.py [--out DIR]
"""
import argparse
import os
import random

from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
DEFAULT_OUT = os.path.normpath(os.path.join(
    HERE, "..", "..", "src", "main", "resources", "assets",
    "gregfoodexpansion", "textures", "item"))

# 酒液色:基色/液面亮色(与 GTFEMaterials 流体材料色对齐)
WINES = {
    "bottled_beer":      ((224, 164, 55), (240, 190, 110)),
    "bottled_cider":     ((233, 199, 102), (245, 220, 150)),
    "bottled_mead":      ((226, 163, 63), (240, 195, 115)),
    "bottled_rice_wine": ((247, 243, 227), (255, 255, 255)),
    "bottled_wine":      ((107, 33, 64), (150, 60, 95)),
    "bottled_huangjiu":  ((201, 138, 46), (225, 175, 100)),
    "bottled_brandy":    ((163, 75, 35), (200, 120, 70)),
    "bottled_whisky":    ((130, 67, 28), (180, 110, 55)),
    "bottled_baijiu":    ((250, 250, 242), (255, 255, 255)),
}

OUTLINE = (43, 43, 43)
GLASS = (215, 233, 236)
GLASS_HI = (240, 250, 250)
CORK = (139, 90, 43)
CORK_DK = (108, 66, 28)


def new16():
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def px(im, x, y, c):
    im.putpixel((x, y), (*c, 255))


def bottle_base():
    """酒瓶骨架:软木塞 + 瓶颈 + 瓶肩 + 瓶身,返回 (im, 内区几何)。

    内区:瓶身 x5-10(y7-13);液面从 y8 起。
    """
    im = new16()
    # 软木塞
    for y in (1, 2):
        for x in (7, 8):
            px(im, x, y, CORK if y == 1 else CORK_DK)
    # 瓶颈 y3-5:轮廓 x6/x9,玻璃 x7-8
    for y in (3, 4, 5):
        px(im, 6, y, OUTLINE)
        px(im, 9, y, OUTLINE)
        for x in (7, 8):
            px(im, x, y, GLASS)
    # 瓶肩 y6:轮廓 x5/x10
    px(im, 5, 6, OUTLINE)
    px(im, 10, 6, OUTLINE)
    for x in range(6, 10):
        px(im, x, 6, GLASS)
    # 瓶身 y7-13:轮廓 x4/x11
    for y in range(7, 14):
        px(im, 4, y, OUTLINE)
        px(im, 11, y, OUTLINE)
        for x in range(5, 11):
            px(im, x, y, GLASS)
    # 瓶底 y14
    for x in range(5, 11):
        px(im, x, 14, OUTLINE)
    return im


def make_bottle(name, liquid, surface):
    im = bottle_base()
    # 液面 y8 亮色,液身 y9-13 基色
    for x in range(5, 11):
        px(im, x, 8, surface)
    for y in range(9, 14):
        for x in range(5, 11):
            px(im, x, y, liquid)
    # 玻璃高光:x6 列 y7(玻璃区)+ 液面点
    px(im, 6, 7, GLASS_HI)
    px(im, 6, 8, GLASS_HI)
    return im


def make_yeast():
    rng = random.Random(42)
    im = new16()
    base, shade, speck = (240, 228, 184), (214, 196, 138), (188, 170, 110)
    # 粉堆轮廓:上窄下宽
    rows = {
        9: range(6, 10),
        10: range(5, 11),
        11: range(4, 12),
        12: range(4, 12),
        13: range(3, 13),
        14: range(3, 13),
    }
    for y, xs in rows.items():
        for x in xs:
            px(im, x, y, base)
    # 下缘阴影
    for x in range(3, 13):
        px(im, x, 14, shade)
    for y in (12, 13):
        px(im, 3, y, shade)
        px(im, 12, y, shade)
    # 酵母颗粒噪点
    for _ in range(14):
        y = rng.choice(list(rows))
        x = rng.choice(list(rows[y]))
        px(im, x, y, speck)
    return im


def make_malt():
    im = new16()
    base, dark, tip = (216, 168, 75), (184, 132, 48), (239, 201, 107)
    # 谷粒:3x2 椭圆体,固定散布(麦芽堆)
    grains = [(4, 10), (8, 11), (11, 9), (5, 7), (9, 6), (12, 12), (3, 12)]
    for gx, gy in grains:
        for dx in range(3):
            for dy in range(2):
                x, y = gx + dx, gy + dy
                if 0 <= x < 16 and 0 <= y < 16:
                    px(im, x, y, base)
        px(im, gx, gy, tip)          # 胚端亮
        px(im, gx + 2, gy + 1, dark)  # 腹沟阴影
    return im


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=DEFAULT_OUT)
    args = ap.parse_args()
    os.makedirs(args.out, exist_ok=True)
    outs = []
    im = make_yeast()
    im.save(os.path.join(args.out, "yeast.png"))
    outs.append("yeast.png")
    im = make_malt()
    im.save(os.path.join(args.out, "malt.png"))
    outs.append("malt.png")
    for name, (liquid, surface) in WINES.items():
        make_bottle(name, liquid, surface).save(os.path.join(args.out, name + ".png"))
        outs.append(name + ".png")
    print("generated %d textures -> %s" % (len(outs), args.out))
    for n in outs:
        print(" ", n)


if __name__ == "__main__":
    main()
