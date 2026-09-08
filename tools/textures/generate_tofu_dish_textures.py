#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""豆腐系菜肴贴图生成(soybean-chain.md 菜肴联动,2026-09-08)。

输出 item 贴图到 src/main/resources/assets/gregfoodexpansion/textures/item/:
- 碗装(soup): tofu_soup, braised_dried_tofu
- 盘装(stir-fry/fried): home_style_tofu, pan_fried_tofu, dried_tofu_pork, tofu_sheet_pork

风格对齐既有菜肴贴图:白瓷碗/白盘 + 食材色块,16x16 像素。
用法: python generate_tofu_dish_textures.py
同时输出 8 倍审阅图 preview_tofu_dishes.png。
"""
import os

from PIL import Image, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.normpath(os.path.join(
    HERE, "..", "..", "src", "main", "resources", "assets",
    "gregfoodexpansion", "textures", "item"))

BOWL_RIM = "#D8D8DC"   # 碗沿冷灰
BOWL_BODY = "#F4F4F6"  # 碗身白
PLATE = "#F0F0F2"      # 盘白
PLATE_K = "#C8C8CC"    # 盘沿

# (文件名, 类型, 汤色/盘底, 主食材色, 副食材色)
ITEMS = [
    ("tofu_soup", "bowl", "#F5F0E1", "#FDFCF6", "#E8D06A"),
    ("braised_dried_tofu", "bowl", "#6A4A20", "#8A5A28", "#5A3A18"),
    ("home_style_tofu", "plate", "#E8E2D0", "#C8A850", "#C04028"),
    ("pan_fried_tofu", "plate", "#EAE4D0", "#D8A848", "#A87828"),
    ("dried_tofu_pork", "plate", "#E8E2D0", "#C8A850", "#E0A090"),
    ("tofu_sheet_pork", "plate", "#E8E2D0", "#F2EEDF", "#E0A090"),
]


def base() -> Image.Image:
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def draw_bowl(d: ImageDraw.ImageDraw, broth: str, main: str, accent: str):
    # 碗:椭圆身 6-13 行
    d.ellipse([(2, 6), (13, 13)], fill=BOWL_BODY, outline=BOWL_RIM)
    # 汤面
    d.ellipse([(3, 5), (12, 9)], fill=broth, outline=BOWL_RIM)
    # 主食材块 + 副食材点缀(固定坐标,确定性)
    for x, y in ((5, 6), (8, 5), (9, 8)):
        d.point((x, y), fill=main)
    d.point((6, 8), fill=accent)
    d.point((10, 6), fill=accent)


def draw_plate(d: ImageDraw.ImageDraw, base_col: str, main: str, accent: str):
    # 盘:扁椭圆 8-13 行
    d.ellipse([(2, 8), (13, 13)], fill=PLATE, outline=PLATE_K)
    # 食材堆:主色块 + 副色丝/点
    for x, y in ((5, 7), (7, 6), (9, 7), (6, 9), (8, 9), (10, 8)):
        d.point((x, y), fill=main)
    for x, y in ((6, 6), (9, 5), (7, 8), (11, 9)):
        d.point((x, y), fill=accent)
    d.point((8, 7), fill=base_col)


def main() -> None:
    os.makedirs(OUT, exist_ok=True)
    previews = []
    for name, kind, base_col, main, accent in ITEMS:
        img = base()
        d = ImageDraw.Draw(img)
        if kind == "bowl":
            draw_bowl(d, base_col, main, accent)
        else:
            draw_plate(d, base_col, main, accent)
        img.save(os.path.join(OUT, f"{name}.png"))
        previews.append(img)
    # 8x 审阅图
    preview = Image.new("RGBA", (len(previews) * 136 + 8, 144), (40, 40, 40, 255))
    for i, img in enumerate(previews):
        big = img.resize((128, 128), Image.NEAREST)
        preview.alpha_composite(big, (8 + i * 136, 8))
    preview.save(os.path.join(HERE, "preview_tofu_dishes.png"))
    print(f"written {len(ITEMS)} dish textures; preview: {os.path.join(HERE, 'preview_tofu_dishes.png')}")


if __name__ == "__main__":
    main()
