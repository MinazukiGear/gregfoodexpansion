#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成大豆链物品贴图(soybean-chain.md §8 资产预算,~11 张):

  item/{bran,cooked_soybean,koji,koji_starter,koji_batch,okara,
        soybean_meal,soybean_pomace,firm_tofu,soft_tofu,tofu_pudding}.png
  tools/textures/preview_soybean.png
      8 倍放大审阅图。

沿用 generate_prep_textures.py 的"模板 + 配色参数化"方案,新增三个模板:
  beans   = 豆粒堆(煮大豆);
  tofu    = 豆块(北/南豆腐,南豆腐圆角更软);
  kojiblk = 曲块(曲/曲料,带接种斑点)。
用法:
  python tools/textures/generate_soybean_textures.py
"""

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
TEX = ROOT / "src/main/resources/assets/gregfoodexpansion/textures"

# 每物料四色:K 描边 / M 主体 / L 亮部 / D 暗部
PALETTES = {
    "bran": {"K": "#8A6A3A", "M": "#D9B478", "L": "#EBD0A0", "D": "#B08A48"},
    "cooked_soybean": {"K": "#8A6A28", "M": "#E8CE88", "L": "#F5E5B4", "D": "#C9A855"},
    "koji": {"K": "#9A8A5A", "M": "#E8DFC0", "L": "#F5F0DC", "D": "#C9BC8A"},
    "koji_starter": {"K": "#7A6A3A", "M": "#D8CC9A", "L": "#EAE0B8", "D": "#A89A62"},
    "koji_batch": {"K": "#8A7A46", "M": "#E0D6A8", "L": "#F0E8C8", "D": "#B8A870"},
    "okara": {"K": "#9A9488", "M": "#F0EDE2", "L": "#FAF8F0", "D": "#D4CFC0"},
    "soybean_meal": {"K": "#8A7A4A", "M": "#DCC98E", "L": "#EDDFB0", "D": "#B8A468"},
    "soybean_pomace": {"K": "#5A4630", "M": "#A89060", "L": "#C8B488", "D": "#7A6440"},
    "firm_tofu": {"K": "#C9C2B0", "M": "#F7F4E8", "L": "#FDFCF6", "D": "#E0DACA"},
    "soft_tofu": {"K": "#D4CEBE", "M": "#FAF8EF", "L": "#FEFDF9", "D": "#E8E2D4"},
    "pudding_white": {"K": "#C9C2B0", "M": "#F7F4EA", "L": "#FDFCF8", "D": "#E0DAD0"},
    "qianye_blank": {"K": "#C4BCAA", "M": "#F2EEE0", "L": "#FBFAF2", "D": "#DAD4C4"},
    "qianye_tofu": {"K": "#B8B4AC", "M": "#EDECE4", "L": "#FAF9F4", "D": "#CFCCC2"},
    "dried_tofu": {"K": "#8A6A30", "M": "#C8A850", "L": "#E8D590", "D": "#A88840"},
    "sheet_blank": {"K": "#C4BCAA", "M": "#F0ECDD", "L": "#FBF9F0", "D": "#DAD4C4"},
    "tofu_sheet": {"K": "#C8C0AC", "M": "#F2EEDF", "L": "#FCFAF2", "D": "#DCD6C6"},
}

ITEMS = [
    ("bran", "powder", "bran"),
    ("cooked_soybean", "beans", "cooked_soybean"),
    ("koji", "kojiblk", "koji"),
    ("koji_starter", "powder", "koji_starter"),
    ("koji_batch", "kojiblk", "koji_batch"),
    ("okara", "minced", "okara"),
    ("soybean_meal", "powder", "soybean_meal"),
    ("soybean_pomace", "minced", "soybean_pomace"),
    ("firm_tofu", "tofu", "firm_tofu"),
    ("soft_tofu", "tofu", "soft_tofu"),
    ("tofu_pudding", "soup", "pudding_white"),
    ("qianye_tofu_blank", "tofu", "qianye_blank"),
    ("qianye_tofu", "tofu", "qianye_tofu"),
    ("dried_tofu", "tofu", "dried_tofu"),
    ("tofu_sheet_blank", "minced", "sheet_blank"),
    ("tofu_sheet", "sheets", "tofu_sheet"),
]


def base() -> Image.Image:
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def p_powder(d: ImageDraw.ImageDraw, pal):
    d.polygon([(8, 4), (2, 12), (14, 12)], fill=pal["K"])
    d.polygon([(8, 6), (4, 11), (12, 11)], fill=pal["M"])
    d.line([6, 10, 10, 10], fill=pal["L"])
    for x, y in ((5, 3), (11, 4), (8, 2), (12, 7)):
        d.point((x, y), fill=pal["M"])


def p_minced(d: ImageDraw.ImageDraw, pal):
    dots = [(3, 4), (7, 3), (11, 5), (4, 8), (9, 8), (12, 10), (3, 11), (7, 11), (10, 12)]
    for x, y in dots:
        d.rectangle([x, y, x + 1, y + 1], fill=pal["M"])
        d.point((x, y), fill=pal["L"])
    for x, y in dots[::3]:
        d.point((x + 1, y + 1), fill=pal["D"])


def p_soup(d: ImageDraw.ImageDraw, pal):
    d.ellipse([1, 5, 15, 14], fill="#C9CDD1")
    d.ellipse([2, 5, 14, 11], fill="#A8AEB5")
    d.ellipse([3, 6, 13, 11], fill=pal["M"])
    d.ellipse([5, 7, 11, 10], fill=pal["L"])
    for x, y in ((5, 7), (9, 6), (11, 9)):
        d.point((x, y), fill=pal["D"])


def p_beans(d: ImageDraw.ImageDraw, pal):
    """豆粒堆:三粒椭圆豆 + 高光(煮大豆,比生豆色深润)。"""
    for ox, oy in ((1, 2), (7, 5), (3, 8)):
        d.ellipse([ox, oy, ox + 7, oy + 6], fill=pal["K"])
        d.ellipse([ox + 1, oy + 1, ox + 6, oy + 5], fill=pal["M"])
        d.arc([ox + 2, oy + 2, ox + 5, oy + 4], 180, 320, fill=pal["L"])
        d.point((ox + 5, oy + 4), fill=pal["D"])
    d.ellipse([8, 10, 15, 14], fill=pal["K"])
    d.ellipse([9, 11, 14, 13], fill=pal["M"])
    d.arc([10, 11, 13, 12], 180, 320, fill=pal["L"])


def p_tofu(d: ImageDraw.ImageDraw, pal):
    """豆块:立方体三面,顶部亮、侧面主体、右下暗。"""
    # 顶面(平行四边形)
    d.polygon([(3, 3), (11, 3), (14, 6), (6, 6)], fill=pal["L"])
    # 正面
    d.rectangle([3, 4, 10, 12], fill=pal["M"])
    # 侧面
    d.polygon([(11, 4), (14, 6), (14, 13), (11, 12)], fill=pal["D"])
    d.line([(3, 4), (11, 4)], fill=pal["L"])
    d.line([(3, 4), (3, 12)], fill=pal["L"])
    d.line([(11, 4), (11, 12)], fill=pal["D"])
    d.line([(3, 12), (11, 12)], fill=pal["D"])
    d.point((5, 6), fill=pal["L"])


def p_kojiblk(d: ImageDraw.ImageDraw, pal):
    """曲块:压块 + 接种白斑(菌丝)。"""
    d.polygon([(3, 4), (12, 3), (14, 7), (13, 12), (4, 13), (2, 8)], fill=pal["K"])
    d.polygon([(4, 5), (11, 4), (13, 7), (12, 11), (5, 12), (3, 8)], fill=pal["M"])
    for x, y in ((5, 6), (8, 5), (10, 7), (6, 9), (9, 9), (11, 10), (4, 10)):
        d.point((x, y), fill=pal["L"])
        d.point((x + 1, y), fill=pal["L"])
    for x, y in ((7, 7), (10, 9)):
        d.point((x, y), fill=pal["D"])


def p_sheets(d: ImageDraw.ImageDraw, pal):
    # 千张:三层叠放的薄 sheets,边缘 K、层面 M/L、折痕 D
    for i, y in enumerate((4, 7, 10)):
        d.rectangle([(3, y), (12, y + 2)], fill=pal["M"], outline=pal["K"])
        d.line([(4, y + 2), (11, y + 2)], fill=pal["D"])
        d.line([(4, y), (11, y)], fill=pal["L"])


PATTERNS = {
    "powder": p_powder, "minced": p_minced, "soup": p_soup,
    "beans": p_beans, "tofu": p_tofu, "kojiblk": p_kojiblk, "sheets": p_sheets,
}


def main() -> None:
    item_dir = TEX / "item"
    images = {}
    for item_id, pattern, pal_name in ITEMS:
        img = base()
        PATTERNS[pattern](ImageDraw.Draw(img), PALETTES[pal_name])
        img.save(item_dir / f"{item_id}.png")
        images[item_id] = img

    # ---- 8 倍审阅图 ----
    scale, cell, gutter, label_h = 8, 128, 8, 14
    keys = [f[0] for f in ITEMS]
    cols = 6
    rows = (len(keys) + cols - 1) // cols
    width = cols * (cell + gutter) + gutter
    height = rows * (cell + label_h + gutter) + gutter
    sheet = Image.new("RGBA", (width, height), (30, 30, 30, 255))
    draw = ImageDraw.Draw(sheet)
    for y in range(0, height, 8):
        for x in range(0, width, 8):
            if (x // 8 + y // 8) % 2 == 0:
                draw.rectangle([x, y, x + 7, y + 7], fill=(58, 58, 58, 255))
    for i, key in enumerate(keys):
        col, row = i % cols, i // cols
        x = gutter + col * (cell + gutter)
        y = gutter + row * (cell + label_h + gutter)
        sheet.paste(images[key].resize((cell, cell), Image.NEAREST), (x, y))
        draw.text((x + 2, y + cell + 1), key[:22], fill=(210, 210, 210, 255))

    preview = ROOT / "tools/textures/preview_soybean.png"
    sheet.save(preview)
    print(f"written {len(ITEMS)} textures; preview: {preview}")


if __name__ == "__main__":
    main()
