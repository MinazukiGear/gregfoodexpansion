#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成切配机批次贴图(food-processor.md §9 资产预算,追加 ~15 张的落地):

  block/machines/food_processor/overlay_{front,side,top}[_active].png
      切配机 overlay(workableTieredHullModel 读取;tier 底壳由 GTCEu hull 提供);
  item/{cleaver,peeler,mortar_pestle,rolling_pin}.png
      手工切配工具 ×4;
  item/<form>.png
      食材形态 ×38(dishes-and-gains.md §4 首批名录,按"原食材简单变形"色块级绘制);
  tools/textures/preview_prep.png
      8 倍放大审阅图。

形态贴图由 12 个形态绘制模板 + 每食材配色参数化生成。用法:
  python tools/textures/generate_prep_textures.py
"""

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
TEX = ROOT / "src/main/resources/assets/gregfoodexpansion/textures"

# 每食材四色:K 描边 / M 主体 / L 亮部 / D 暗部
PALETTES = {
    "beef": {"K": "#4A1512", "M": "#B03030", "L": "#D06050", "D": "#8A2020"},
    "pork": {"K": "#6E3030", "M": "#E8908A", "L": "#F5B5AE", "D": "#C46862"},
    "mutton": {"K": "#4A1E1E", "M": "#C04848", "L": "#E07870", "D": "#983430"},
    "chicken": {"K": "#7A5030", "M": "#F0D0B0", "L": "#FAE8D0", "D": "#D0A880"},
    "fish": {"K": "#5A6070", "M": "#D8DCE0", "L": "#F0F2F5", "D": "#B0B8C0"},
    "potato": {"K": "#6E5A20", "M": "#E8D598", "L": "#F5E8C0", "D": "#C9B070"},
    "carrot": {"K": "#7A3A0E", "M": "#F28C1B", "L": "#FFB55A", "D": "#C96E10"},
    "cabbage": {"K": "#3E5C26", "M": "#A8C97A", "L": "#C9E09A", "D": "#7FA84F"},
    "chili": {"K": "#7A1810", "M": "#D93A2B", "L": "#F06A55", "D": "#B02417"},
    "tomato": {"K": "#6E1F16", "M": "#E04B3A", "L": "#F2836F", "D": "#C23325"},
    "apple": {"K": "#6E5A20", "M": "#F2E3A8", "L": "#FAF2CE", "D": "#D9C070"},
    "onion": {"K": "#5C5260", "M": "#E8E0EC", "L": "#F7F3F8", "D": "#C9BECF"},
    "dough": {"K": "#8A6E3E", "M": "#E8D9A8", "L": "#F5ECCB", "D": "#C9B478"},
    "garlic": {"K": "#6E6A5A", "M": "#F2EFE2", "L": "#FAF8EE", "D": "#D9D4BE"},
}

# (id, 模板, 配色源)
FORMS = [
    ("beef_slice", "slices", "beef"), ("beef_strip", "strips", "beef"),
    ("beef_cube", "diced", "beef"), ("beef_ribs", "ribs", "beef"),
    ("beef_minced", "minced", "beef"),
    ("pork_slice", "slices", "pork"), ("pork_strip", "strips", "pork"),
    ("pork_cube", "diced", "pork"), ("pork_ribs", "ribs", "pork"),
    ("pork_minced", "minced", "pork"),
    ("mutton_slice", "slices", "mutton"), ("mutton_cube", "diced", "mutton"),
    ("mutton_minced", "minced", "mutton"),
    ("chicken_slice", "slices", "chicken"), ("chicken_shred", "strips", "chicken"),
    ("chicken_diced", "diced", "chicken"), ("chicken_cuts", "nugget", "chicken"),
    ("chicken_minced", "minced", "chicken"),
    ("fish_slice", "slices", "fish"), ("fish_cube", "diced", "fish"),
    ("fish_surimi", "surimi", "fish"),
    ("chili_ring", "ring", "chili"),
    ("potato_slice", "slices", "potato"), ("apple_slice", "slices", "apple"),
    ("tomato_slice", "slices", "tomato"),
    ("potato_strip", "strips", "potato"), ("carrot_strip", "strips", "carrot"),
    ("cabbage_strip", "strips", "cabbage"), ("chili_strip", "strips", "chili"),
    ("tomato_diced", "diced", "tomato"), ("onion_diced", "diced", "onion"),
    ("fries_blank", "fries", "potato"), ("chili_diced", "diced", "chili"),
    ("potato_diced", "diced", "potato"),
    ("onion_minced", "minced", "onion"), ("garlic_minced", "minced", "garlic"),
    ("chili_powder", "powder", "chili"),
    ("apple_flesh", "flesh", "apple"),
    ("noodle", "noodle", "dough"), ("dough_sheet", "sheet", "dough"),
]

MINCED_DOTS = [(3, 4), (7, 3), (11, 5), (4, 8), (9, 8), (12, 10), (3, 11), (7, 11), (10, 12)]


def base() -> Image.Image:
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def outline_rect(d: ImageDraw.ImageDraw, box, pal, radius=1):
    d.rounded_rectangle(box, radius=radius, fill=pal["K"])
    d.rounded_rectangle([box[0] + 1, box[1] + 1, box[2] - 1, box[3] - 1], radius=radius,
                        fill=pal["M"])
    d.line([box[0] + 1, box[1] + 1, box[2] - 2, box[1] + 1], fill=pal["L"])


def p_slices(d, pal):
    for ox, oy in ((1, 1), (4, 4), (7, 7)):
        outline_rect(d, (ox, oy, ox + 8, oy + 8), pal)
        d.ellipse([ox + 3, oy + 3, ox + 6, oy + 6], fill=pal["L"])


def p_strips(d, pal):
    for x in (2, 6, 10):
        outline_rect(d, (x, 1, x + 3, 14), pal)
        d.line([x + 1, 2, x + 1, 13], fill=pal["L"])


def p_diced(d, pal):
    for ox, oy in ((1, 2), (7, 5), (3, 9)):
        d.rectangle([ox, oy, ox + 5, oy + 5], fill=pal["K"])
        d.rectangle([ox + 1, oy + 1, ox + 4, oy + 4], fill=pal["M"])
        d.rectangle([ox + 1, oy + 1, ox + 4, oy + 2], fill=pal["L"])
        d.rectangle([ox + 3, oy + 3, ox + 4, oy + 4], fill=pal["D"])


def p_minced(d, pal):
    for x, y in MINCED_DOTS:
        d.rectangle([x, y, x + 1, y + 1], fill=pal["M"])
        d.point((x, y), fill=pal["L"])
    for x, y in MINCED_DOTS[::3]:
        d.point((x + 1, y + 1), fill=pal["D"])


def p_powder(d, pal):
    d.polygon([(8, 4), (2, 12), (14, 12)], fill=pal["K"])
    d.polygon([(8, 6), (4, 11), (12, 11)], fill=pal["M"])
    d.line([6, 10, 10, 10], fill=pal["L"])
    for x, y in ((5, 3), (11, 4), (8, 2), (12, 7)):
        d.point((x, y), fill=pal["M"])


def p_ring(d, pal):
    for ox in (1, 6):
        d.ellipse([ox, 4, ox + 9, 13], fill=pal["K"])
        d.ellipse([ox + 2, 6, ox + 7, 11], fill=(0, 0, 0, 0))
        d.arc([ox + 2, 6, ox + 7, 11], 180, 320, fill=pal["L"])


def p_flesh(d, pal):
    d.ellipse([2, 3, 14, 14], fill=pal["K"])
    d.ellipse([3, 4, 13, 13], fill=pal["M"])
    d.ellipse([5, 5, 11, 9], fill=pal["L"])
    d.arc([5, 8, 11, 13], 20, 160, fill=pal["D"])


def p_fries(d, pal):
    for x in (2, 5, 8, 11):
        d.rounded_rectangle([x, 2, x + 2, 13], radius=1, fill=pal["K"])
        d.rectangle([x, 4, x + 2, 12], fill=pal["M"])
        d.line([x + 1, 4, x + 1, 12], fill=pal["L"])


def p_noodle(d, pal):
    pts = [[(2, 3), (6, 5), (10, 3), (14, 5)], [(2, 7), (6, 9), (10, 7), (14, 9)],
           [(2, 11), (6, 13), (10, 11), (14, 13)]]
    for line in pts:
        d.line([tuple(p) for p in line], fill=pal["K"], width=3)
        d.line([tuple(p) for p in line], fill=pal["M"], width=2)
    d.line([(2, 3), (6, 5), (10, 3)], fill=pal["L"], width=1)


def p_sheet(d, pal):
    d.ellipse([1, 4, 15, 13], fill=pal["K"])
    d.ellipse([2, 5, 14, 12], fill=pal["M"])
    d.ellipse([4, 6, 12, 10], fill=pal["L"])


def p_ribs(d, pal):
    d.rounded_rectangle([1, 7, 15, 10], radius=2, fill="#E8E4DA")
    d.rounded_rectangle([1, 8, 15, 9], fill="#F8F6F0")
    for ox in (2, 6, 10):
        d.ellipse([ox, 3 + (ox % 3), ox + 5, 8 + (ox % 3)], outline=pal["K"], fill=pal["M"])
        d.arc([ox + 1, 4 + (ox % 3), ox + 4, 7 + (ox % 3)], 160, 340, fill=pal["L"])


def p_nugget(d, pal):
    d.polygon([(3, 4), (12, 2), (14, 8), (10, 14), (4, 12)], fill=pal["K"])
    d.polygon([(4, 5), (11, 4), (13, 8), (10, 12), (5, 11)], fill=pal["M"])
    for x, y in ((6, 6), (9, 6), (7, 9), (11, 9)):
        d.point((x, y), fill=pal["L"])


def p_surimi(d, pal):
    for ox, oy in ((3, 3), (8, 5), (4, 9), (9, 10)):
        d.ellipse([ox, oy, ox + 5, oy + 4], fill=pal["K"])
        d.ellipse([ox + 1, oy + 1, ox + 4, oy + 3], fill=pal["M"])
        d.point((ox + 2, oy + 1), fill=pal["L"])


PATTERNS = {
    "slices": p_slices, "strips": p_strips, "diced": p_diced, "minced": p_minced,
    "powder": p_powder, "ring": p_ring, "flesh": p_flesh, "fries": p_fries,
    "noodle": p_noodle, "sheet": p_sheet, "ribs": p_ribs, "nugget": p_nugget,
    "surimi": p_surimi,
}

# ---- 机器 overlay(灰钢面板 + 刀具图形) ----

PANEL = "#3B4046"
PANEL_DARK = "#2A2E33"
PANEL_LIGHT = "#5A6169"
STEEL = "#C9CDD1"
STEEL_DARK = "#8A929B"
ACCENT = "#F2C14E"
ACTIVE_GLOW = "#8FD0F0"


def panel(d: ImageDraw.ImageDraw):
    d.rectangle([0, 0, 15, 15], fill=PANEL_DARK)
    d.rectangle([1, 1, 14, 14], fill=PANEL)
    d.line([1, 1, 14, 1], fill=PANEL_LIGHT)


def overlay_front(active: bool) -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    panel(d)
    # 菜刀图形:宽刃 + 短柄
    d.polygon([(3, 4), (9, 3), (9, 10), (4, 11)], fill=STEEL)
    d.polygon([(4, 5), (8, 4), (8, 9), (5, 9)], fill=STEEL_DARK)
    d.line([(3, 10), (9, 9)], fill="#FFFFFF")
    d.line([(9, 6), (13, 10)], fill="#6E4A26", width=3)
    if active:
        for y in (3, 7, 11):
            d.line([(11, y), (14, y - 1)], fill=ACTIVE_GLOW)
        d.point((5, 6), fill=ACTIVE_GLOW)
    return img


def overlay_side(active: bool) -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    panel(d)
    d.rectangle([3, 4, 12, 5], fill=PANEL_DARK)
    d.rectangle([3, 8, 12, 9], fill=PANEL_DARK)
    d.rectangle([3, 12, 12, 13], fill=PANEL_DARK)
    if active:
        d.rectangle([3, 4, 12, 5], fill=ACCENT)
    return img


def overlay_top(active: bool) -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    panel(d)
    d.ellipse([3, 3, 13, 13], fill=PANEL_DARK)
    d.ellipse([4, 4, 12, 12], fill=STEEL_DARK)
    d.ellipse([6, 6, 10, 10], fill=PANEL)
    d.line([(8, 5), (8, 11)], fill=STEEL)
    d.line([(5, 8), (11, 8)], fill=STEEL)
    if active:
        d.arc([4, 4, 12, 12], 300, 80, fill=ACCENT, width=2)
    return img


# ---- 手工工具 ----


def tool_cleaver() -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    d.rectangle([2, 2, 10, 10], fill=STEEL)
    d.rectangle([2, 2, 10, 3], fill="#E8EBEE")
    d.line([(2, 10), (10, 10)], fill="#FFFFFF")
    d.rectangle([5, 5, 6, 6], fill=STEEL_DARK)
    d.line([(10, 10), (13, 13)], fill="#6E4A26", width=3)
    return img


def tool_peeler() -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    d.rectangle([4, 2, 7, 8], fill=STEEL)
    d.line([(4, 2), (4, 8)], fill="#FFFFFF")
    d.line([(5, 5), (5, 7)], fill=STEEL_DARK)
    d.line([(7, 8), (12, 12)], fill="#6E4A26", width=3)
    return img


def tool_mortar_pestle() -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    d.polygon([(2, 8), (13, 8), (12, 14), (3, 14)], fill="#7A8288")
    d.line([(2, 8), (13, 8)], fill="#5A6169")
    d.line([(3, 14), (12, 14)], fill="#5A6169")
    d.line([(10, 2), (12, 7)], fill="#9BA3AA", width=3)
    d.ellipse([9, 1, 12, 4], fill="#B8BEC4")
    return img


def tool_rolling_pin() -> Image.Image:
    img = base()
    d = ImageDraw.Draw(img)
    d.rectangle([1, 6, 3, 10], fill="#6E4A26")
    d.rectangle([12, 6, 14, 10], fill="#6E4A26")
    d.rounded_rectangle([3, 5, 12, 11], radius=2, fill="#C8A05A")
    d.line([(4, 6), (11, 6)], fill="#E8C888")
    d.line([(4, 10), (11, 10)], fill="#9A7838")
    return img


def main() -> None:
    machine_dir = TEX / "block" / "machines" / "food_processor"
    item_dir = TEX / "item"
    machine_dir.mkdir(parents=True, exist_ok=True)

    images = {}
    for name, img in {
        "overlay_front": overlay_front(False), "overlay_front_active": overlay_front(True),
        "overlay_side": overlay_side(False), "overlay_side_active": overlay_side(True),
        "overlay_top": overlay_top(False), "overlay_top_active": overlay_top(True),
    }.items():
        img.save(machine_dir / f"{name}.png")
        images[name] = img

    for name, draw in {
        "cleaver": tool_cleaver, "peeler": tool_peeler,
        "mortar_pestle": tool_mortar_pestle, "rolling_pin": tool_rolling_pin,
    }.items():
        img = draw()
        img.save(item_dir / f"{name}.png")
        images[name] = img

    for item_id, pattern, pal_name in FORMS:
        img = base()
        PATTERNS[pattern](ImageDraw.Draw(img), PALETTES[pal_name])
        img.save(item_dir / f"{item_id}.png")
        images[item_id] = img

    # ---- 审阅预览 ----
    scale, cell, gutter, label_h = 8, 128, 8, 14
    keys = ["overlay_front", "overlay_front_active", "overlay_side", "overlay_side_active",
            "overlay_top", "overlay_top_active",
            "cleaver", "peeler", "mortar_pestle", "rolling_pin"] + [f[0] for f in FORMS]
    cols = 10
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

    preview = ROOT / "tools/textures/preview_prep.png"
    sheet.save(preview)
    print(f"written {6 + 4 + len(FORMS)} textures; preview: {preview}")


if __name__ == "__main__":
    main()
