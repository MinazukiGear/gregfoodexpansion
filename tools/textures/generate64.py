#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""64x64 程序化贴图生成器(作物/食材/菜肴全量,2026-09-08 定案)。

**本脚本当前未执行——用户要求先只交付脚本,运行见文末用法。**

路线依据(当日实验结论):
- 64x64 程序化"有机画法"优于 AI 降档:metaball 肉块(半径角向正弦扰动)+ 斜向光照
  + 边缘压暗 + 逐像素噪点;AI 1024 原图是"假像素画",降到 64 反而暴露马赛克网格。
- 游戏内物品显示 ~16-26px,128 无意义;64 为写实与成本平衡点。
- 关键教训:所有形状判定必须全程使用归一化坐标(d 与 r 同单位,否则全画布判内)。

覆盖范围:
- 食材+菜肴:generate_prep_textures.py 的 FORMS 全部 172 项(16 档模板重制);
- 大豆链:generate_soybean_textures.py 的 ITEMS 全部 16 项;
- 作物:generate_crop_textures.py 的 13 产物 + 13 种子 + 4 张灰度生长模板
  (block/crop/stage_*.png 保持灰度,运行时 BlockColors 染色机制不变)。

用法:
  python tools/textures/generate64.py                 # 全量生成 + 审查图
  python tools/textures/generate64.py --only skewer   # 只生成含关键字的物品
  python tools/textures/generate64.py --list          # 只列清单不落盘
  python tools/textures/generate64.py --out <dir>     # 输出到指定目录(默认资源目录,直接覆盖 16x16!)
"""

import argparse
import math
import os
import random

from PIL import Image, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
ITEM_DIR = os.path.join(ROOT, "src/main/resources/assets/gregfoodexpansion/textures/item")
BLOCK_DIR = os.path.join(ROOT, "src/main/resources/assets/gregfoodexpansion/textures/block/crop")
PREVIEW_DIR = HERE

SIZE = 64

# ============================================================================
# 调色板:每物料 K 描边 / M 主体 / L 亮部 / D 暗部(移植自旧生成器,16 档同色相)
# ============================================================================

PALETTES = {
    # 肉类
    "beef": {"K": "#4A1512", "M": "#B03030", "L": "#D06050", "D": "#8A2020"},
    "pork": {"K": "#6E3030", "M": "#E8908A", "L": "#F5B5AE", "D": "#C46862"},
    "mutton": {"K": "#4A1E1E", "M": "#C04848", "L": "#E07870", "D": "#983430"},
    "chicken": {"K": "#7A5030", "M": "#F0D0B0", "L": "#FAE8D0", "D": "#D0A880"},
    "mutton_cooked": {"K": "#3A1A10", "M": "#9A5A30", "L": "#C28250", "D": "#6E3E1E"},
    "beef_cooked": {"K": "#34140C", "M": "#8A4A28", "L": "#B07040", "D": "#5E3018"},
    "chicken_cooked": {"K": "#6E4018", "M": "#D8A060", "L": "#F0C890", "D": "#A87840"},
    "fish": {"K": "#5A6070", "M": "#D8DCE0", "L": "#F0F2F5", "D": "#B0B8C0"},
    # 蔬果
    "potato": {"K": "#6E5A20", "M": "#E8D598", "L": "#F5E8C0", "D": "#C9B070"},
    "tofu_white": {"K": "#C9C2B0", "M": "#F7F4E8", "L": "#FDFCF6", "D": "#E0DACA"},
    "carrot": {"K": "#7A3A0E", "M": "#F28C1B", "L": "#FFB55A", "D": "#C96E10"},
    "cabbage": {"K": "#3E5C26", "M": "#A8C97A", "L": "#C9E09A", "D": "#7FA84F"},
    "chili": {"K": "#7A1810", "M": "#D93A2B", "L": "#F06A55", "D": "#B02417"},
    "tomato": {"K": "#6E1F16", "M": "#E04B3A", "L": "#F2836F", "D": "#C23325"},
    "apple": {"K": "#6E5A20", "M": "#F2E3A8", "L": "#FAF2CE", "D": "#D9C070"},
    "onion": {"K": "#5C5260", "M": "#E8E0EC", "L": "#F7F3F8", "D": "#C9BECF"},
    "dough": {"K": "#8A6E3E", "M": "#E8D9A8", "L": "#F5ECCB", "D": "#C9B478"},
    "garlic": {"K": "#6E6A5A", "M": "#F2EFE2", "L": "#FAF8EE", "D": "#D9D4BE"},
    "rice_flour": {"K": "#8A8468", "M": "#F2EDDE", "L": "#FAF7EC", "D": "#D9D2B8"},
    "rice_dough": {"K": "#8A8058", "M": "#EDE7CE", "L": "#F7F3E4", "D": "#CFC6A4"},
    "rice_noodles": {"K": "#8A7A4E", "M": "#F0EAD0", "L": "#F9F5E6", "D": "#D4CBA6"},
    # 烘焙/熟制
    "baked": {"K": "#7A5A20", "M": "#D8A860", "L": "#F0D0A0", "D": "#B08840"},
    "toast_brown": {"K": "#6E4A20", "M": "#C89050", "L": "#E0B478", "D": "#A07038"},
    "cake_cream": {"K": "#8A7440", "M": "#F5E8D0", "L": "#FDF6E8", "D": "#D8C098"},
    # 签子
    "skewer_wood": {"K": "#6E4A26", "M": "#B08A48", "L": "#D0A868", "D": "#8A6A30"},
    "skewer_iron": {"K": "#4A5056", "M": "#B8BEC4", "L": "#E8EBEE", "D": "#8A929B"},
    # 菜肴汤色/盘色
    "dish_yellow": {"K": "#8A6E20", "M": "#F0D060", "L": "#FAE89A", "D": "#C8A030"},
    "dish_pink": {"K": "#7A5030", "M": "#F0C0B8", "L": "#FAE0D8", "D": "#D09890"},
    "noodle_white": {"K": "#8A8468", "M": "#F2EFE2", "L": "#FAF8EE", "D": "#D9D4BE"},
    "soup_red": {"K": "#6E1F16", "M": "#E04B3A", "L": "#F2836F", "D": "#C23325"},
    "soup_brown": {"K": "#5A4020", "M": "#C89A50", "L": "#E0BC78", "D": "#9A7038"},
    "soup_white": {"K": "#8A8468", "M": "#F2EDDE", "L": "#FAF7EC", "D": "#D9D2B8"},
    "soup_yellow": {"K": "#8A6E20", "M": "#F2CB4B", "L": "#FBE9A0", "D": "#D9A92E"},
    "dish_green": {"K": "#3E5C26", "M": "#8FBF5A", "L": "#C9E09A", "D": "#6E9B3E"},
    "dish_red": {"K": "#7A1810", "M": "#D93A2B", "L": "#F06A55", "D": "#B02417"},
    "dish_gold": {"K": "#8A5A10", "M": "#E8B44A", "L": "#F5D488", "D": "#C08A28"},
    "dish_cream": {"K": "#8A7440", "M": "#F0E0B8", "L": "#FAF0D8", "D": "#D4BC84"},
    "soy_glaze": {"K": "#3E1A08", "M": "#8A4A18", "L": "#B86A2E", "D": "#5E3010"},
    "kimchi": {"K": "#6E2A14", "M": "#D86840", "L": "#F0A080", "D": "#A84426"},
    # 大豆链(soybean 生成器移植)
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
    "beans_raw": {"K": "#5A6420", "M": "#C9D46A", "L": "#E4EC9A", "D": "#9AA84A"},
}

# ============================================================================
# 有机画法核心(全程归一化坐标,教训见 docstring)
# ============================================================================

def _c(hexstr):
    h = hexstr.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16))


def sh(c, d):
    return tuple(max(0, min(255, int(v + d))) for v in c)


def paint_blobs(im, blobs, base, seed=7, noise=9, char_ratio=0.0, char_col=(58, 30, 14),
                fat_ratio=0.0, fat_col=(238, 196, 186), light_dir=1):
    """metaball 有机填充:blobs = [(cx, cy, r, wob_amp, wob_freq, phase)],归一化坐标。

    光照 = 斜向明暗(light_dir=1 左上亮) + 边缘压暗 + 噪点;可叠加焦斑/脂肪纹。
    只对最上层的 blob 生效(列表序即层级,后者在上)。
    """
    px = im.load()
    rnd = random.Random(seed)
    n = SIZE
    for y in range(n):
        for x in range(n):
            fx, fy = x / n, y / n
            hit = None
            for b in blobs:
                cx, cy, r = b[0], b[1], b[2]
                amp = b[3] if len(b) > 3 else 0.12
                freq = b[4] if len(b) > 4 else 3
                ph = b[5] if len(b) > 5 else 0
                d = math.hypot(fx - cx, fy - cy)
                ang = math.atan2(fy - cy, fx - cx)
                w = 1.0 + amp * math.sin(ang * freq + ph) + amp * 0.5 * math.sin(ang * (freq * 2 + 1) + ph * 2)
                if d < r * w:
                    hit = (d / (r * w), cx, cy, r * w)
            if not hit:
                continue
            k, cx, cy, rw = hit
            light = light_dir * ((fx - cx) + (fy - cy))
            shade = -light / rw * 34
            shade -= max(0.0, (k - 0.72) / 0.28) * 40
            v = rnd.randint(-noise, noise)
            col = sh(base, shade + v)
            roll = rnd.random()
            if char_ratio and roll < char_ratio:
                col = char_col
            elif fat_ratio and roll < char_ratio + fat_ratio:
                col = fat_col
            px[x, y] = col + (255,)


def paint_stick(im, wood=True, seed=3):
    """斜置签子:对角线渐变木色/金属色 + 描边 + 尖端,归一化宽度。"""
    px = im.load()
    n = SIZE
    half = 0.020 if wood else 0.016
    outline = 0.024 if wood else 0.019
    if wood:
        body, edge = _c("#B08A48"), _c("#6E4A26")
    else:
        body, edge = _c("#B8BEC4"), _c("#4A5056")
    for y in range(n):
        for x in range(n):
            fx, fy = x / n, y / n
            t = max(0.0, min(1.0, (0.94 - fy) / 0.91))
            lx, ly = 0.06 + 0.88 * t, 0.94 - 0.91 * t
            ds = math.hypot(fx - lx, fy - ly)
            if ds < half:
                depth = ds / half
                col = sh(body, int(-70 * depth))
                if not wood and depth < 0.45:
                    col = _c("#E8EBEE")          # 金属高光带
                px[x, y] = col + (255,)
            elif ds < outline:
                px[x, y] = edge + (255,)
    d = ImageDraw.Draw(im)
    tip_w = max(1, n // 40)
    d.line([(int(0.92 * n), int(0.05 * n)), (int(0.975 * n), int(0.005 * n))],
           fill=(sh(edge, -10) + (255,)), width=tip_w)


def ring_stick(im, wood=True):
    """铁签尾环(金属专用,木签无)。"""
    d = ImageDraw.Draw(im)
    col = _c("#4A5056") if wood else _c("#4A5056")
    mid = _c("#8A929B")
    r0, r1 = int(0.06 * SIZE), int(0.13 * SIZE)
    cx, cy = int(0.08 * SIZE), int(0.92 * SIZE)
    d.ellipse([cx - r1, cy - r1, cx + r1, cy + r1], outline=col + (255,), width=max(2, SIZE // 24))
    d.ellipse([cx - r0, cy - r0, cx + r0, cy + r0], outline=mid + (255,), width=1)


def speckle(im, col, count, seed, r_min=1, r_max=2, box=(0.14, 0.14, 0.86, 0.86)):
    """表面撒点:盐粒/辣椒面/糖粒。"""
    d = ImageDraw.Draw(im)
    rnd = random.Random(seed)
    for _ in range(count):
        x = rnd.uniform(box[0], box[2]) * SIZE
        y = rnd.uniform(box[1], box[3]) * SIZE
        r = rnd.randint(r_min, r_max)
        d.ellipse([x - r, y - r, x + r, y + r], fill=col + (255,))


# ============================================================================
# 食材形态模板(64x64)
# ============================================================================

def t_slices(im, pal, seed=11):
    """切片:3 片圆角厚片对角堆叠,肉质噪点+受光。"""
    rnd = random.Random(seed)
    for i, (cx, cy) in enumerate(((0.32, 0.38), (0.52, 0.54), (0.70, 0.68))):
        blobs = [(cx, cy, 0.19, 0.06, 4, i * 1.7)]
        paint_blobs(im, blobs, _c(pal["M"]), seed=seed + i)
    # 每片加内圈亮部(切片切面纹理)
    d = ImageDraw.Draw(im)
    for (cx, cy) in ((0.32, 0.38), (0.52, 0.54), (0.70, 0.68)):
        d.ellipse([(cx - 0.10) * SIZE, (cy - 0.10) * SIZE, (cx + 0.10) * SIZE, (cy + 0.10) * SIZE],
                  outline=_c(pal["L"]) + (255,), width=2)
    # 描边
    for b in (((0.32, 0.38), (0.52, 0.54), (0.70, 0.68))):
        pass
    speckle(im, _c(pal["L"]), 8, seed + 9, 1, 1)


def t_strips(im, pal, seed=12):
    """肉丝/条:3 根圆头长条,中部高光。"""
    for i, x in enumerate((0.24, 0.48, 0.72)):
        blobs = [(x, 0.50 - i * 0.05, 0.17, 0.05, 6, i)]
        paint_blobs(im, blobs, _c(pal["M"]), seed=seed + i, noise=7)
    d = ImageDraw.Draw(im)
    for x in (0.24, 0.48, 0.72):
        d.line([x * SIZE, 0.30 * SIZE, x * SIZE, 0.72 * SIZE], fill=_c(pal["L"]) + (255,), width=2)


def t_diced(im, pal, seed=13):
    """切块:3 个带体积感的方块(顶面亮/侧面暗)。"""
    for i, (cx, cy) in enumerate(((0.30, 0.36), (0.62, 0.50), (0.38, 0.68))):
        x0, y0 = (cx - 0.16) * SIZE, (cy - 0.16) * SIZE
        s = 0.32 * SIZE
        d = ImageDraw.Draw(im)
        d.polygon([(x0, y0 + s * 0.25), (x0 + s * 0.25, y0), (x0 + s, y0),
                   (x0 + s * 0.75, y0 + s * 0.25)], fill=_c(pal["L"]) + (255,))
        d.polygon([(x0, y0 + s * 0.25), (x0 + s * 0.25, y0 + s * 0.25), (x0 + s * 0.25, y0 + s),
                   (x0, y0 + s * 0.75)], fill=_c(pal["M"]) + (255,))
        d.polygon([(x0 + s * 0.25, y0 + s * 0.25), (x0 + s, y0 + s * 0.25), (x0 + s, y0 + s * 0.75),
                   (x0 + s * 0.25, y0 + s)], fill=_c(pal["D"]) + (255,))
        d.polygon([(x0, y0 + s * 0.25), (x0 + s * 0.25, y0), (x0 + s, y0), (x0 + s * 0.75, y0 + s * 0.25),
                   (x0 + s, y0 + s * 0.75), (x0 + s * 0.25, y0 + s), (x0, y0 + s * 0.75)],
                  outline=_c(pal["K"]) + (255,))
        rnd = random.Random(seed + i)
        for _ in range(6):
            px = rnd.uniform(x0 + 2, x0 + s - 2)
            py = rnd.uniform(y0 + 2, y0 + s - 2)
            d.point((px, py), fill=sh(_c(pal["M"]), rnd.randint(-16, 16)) + (255,))


def t_minced(im, pal, seed=14):
    """肉馅/碎粒:12 个小有机粒。"""
    rnd = random.Random(seed)
    blobs = []
    for i in range(12):
        cx = rnd.uniform(0.18, 0.82)
        cy = rnd.uniform(0.20, 0.80)
        blobs.append((cx, cy, rnd.uniform(0.05, 0.09), 0.20, 5, i))
    paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=10)
    d = ImageDraw.Draw(im)
    for i in range(5):
        cx, cy = rnd.uniform(0.2, 0.8) * SIZE, rnd.uniform(0.2, 0.8) * SIZE
        d.point((cx, cy), fill=_c(pal["L"]) + (255,))


def t_powder(im, pal, seed=15):
    """粉堆:半椭圆堆 + 顶部颗粒。"""
    blobs = [(0.5, 0.62, 0.30, 0.10, 5, 0), (0.5, 0.50, 0.22, 0.14, 4, 2)]
    paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=12)
    speckle(im, _c(pal["L"]), 14, seed + 1, 1, 1, box=(0.28, 0.42, 0.72, 0.72))
    speckle(im, _c(pal["K"]), 5, seed + 2, 1, 1, box=(0.30, 0.30, 0.70, 0.50))
    d = ImageDraw.Draw(im)
    d.arc([0.12 * SIZE, 0.40 * SIZE, 0.88 * SIZE, 0.95 * SIZE], 15, 165, fill=_c(pal["K"]) + (255,), width=2)


def t_ring(im, pal, seed=16):
    """洋葱圈:两只斜置圆环,有厚度。"""
    d = ImageDraw.Draw(im)
    for i, (cx, cy, r) in enumerate(((0.36, 0.40, 0.20), (0.62, 0.62, 0.18))):
        w = int(r * SIZE * 0.30)
        d.ellipse([(cx - r) * SIZE, (cy - r) * SIZE, (cx + r) * SIZE, (cy + r) * SIZE],
                  fill=_c(pal["M"]) + (255,))
        hole = r * 0.45
        d.ellipse([(cx - hole) * SIZE, (cy - hole) * SIZE, (cx + hole) * SIZE, (cy + hole) * SIZE],
                  fill=(0, 0, 0, 0))
        d.ellipse([(cx - r) * SIZE, (cy - r) * SIZE, (cx + r) * SIZE, (cy + r) * SIZE],
                  outline=_c(pal["K"]) + (255,), width=2)
        d.arc([(cx - r + 0.02) * SIZE, (cy - r + 0.02) * SIZE, (cx + r - 0.02) * SIZE, (cy + r - 0.02) * SIZE],
              160, 300, fill=_c(pal["L"]) + (255,), width=w // 2)


def t_flesh(im, pal, seed=17):
    """果肉(苹果肉/米团):大圆块+芯部弧纹。"""
    paint_blobs(im, [(0.5, 0.55, 0.34, 0.07, 4, 0)], _c(pal["M"]), seed=seed, noise=6)
    d = ImageDraw.Draw(im)
    d.ellipse([0.34 * SIZE, 0.34 * SIZE, 0.62 * SIZE, 0.52 * SIZE], fill=_c(pal["L"]) + (255,))
    d.arc([0.34 * SIZE, 0.52 * SIZE, 0.66 * SIZE, 0.74 * SIZE], 20, 160, fill=_c(pal["D"]) + (255,), width=3)


def t_fries(im, pal, seed=18):
    """薯条/长条炸物:5 根斜置金条。"""
    d = ImageDraw.Draw(im)
    rnd = random.Random(seed)
    for i, (x0, y0, x1, y1) in enumerate(((0.14, 0.78, 0.62, 0.24), (0.26, 0.84, 0.74, 0.30),
                                          (0.40, 0.86, 0.86, 0.34), (0.20, 0.66, 0.60, 0.14),
                                          (0.34, 0.70, 0.80, 0.18))):
        w = max(2, SIZE // 18)
        d.line([(x0 * SIZE, y0 * SIZE), (x1 * SIZE, y1 * SIZE)], fill=_c(pal["K"]) + (255,), width=w + 2)
        d.line([(x0 * SIZE, y0 * SIZE), (x1 * SIZE, y1 * SIZE)], fill=_c(pal["M"]) + (255,), width=w)
        d.line([(x0 * SIZE + 1, y0 * SIZE - 1), (x1 * SIZE - 1, y1 * SIZE - 1)],
               fill=_c(pal["L"]) + (255,), width=1)
    speckle(im, _c(pal["D"]), 8, seed + 3, 1, 1)


def t_noodle(im, pal, seed=19):
    """面条:4 条波浪线。"""
    d = ImageDraw.Draw(im)
    rnd = random.Random(seed)
    for i, y0 in enumerate((0.26, 0.42, 0.58, 0.74)):
        pts = []
        for k in range(9):
            x = 0.10 + k * 0.10
            y = y0 + 0.045 * math.sin(k * 1.9 + i * 1.3)
            pts.append((x * SIZE, y * SIZE))
        w = max(2, SIZE // 22)
        d.line(pts, fill=_c(pal["K"]) + (255,), width=w + 2, joint="curve")
        d.line(pts, fill=_c(pal["M"]) + (255,), width=w, joint="curve")
        d.line([(p[0], p[1] - 1) for p in pts[:5]], fill=_c(pal["L"]) + (255,), width=1)


def t_sheet(im, pal, seed=20):
    """面皮/浆坯:双层圆角大薄片。"""
    d = ImageDraw.Draw(im)
    for i, off in enumerate((0.0, 0.10)):
        box = [0.14 * SIZE + off * SIZE, 0.26 * SIZE + off * SIZE,
               0.86 * SIZE + off * SIZE * 0.5, 0.74 * SIZE + off * SIZE * 0.5]
        d.rounded_rectangle(box, radius=SIZE // 12, fill=sh(_c(pal["M"]), -14 * i) + (255,),
                            outline=_c(pal["K"]) + (255,), width=2)
    d.line([0.20 * SIZE, 0.34 * SIZE, 0.80 * SIZE, 0.34 * SIZE], fill=_c(pal["L"]) + (255,), width=2)


def t_sheets(im, pal, seed=21):
    """千张:多层薄片侧面。"""
    d = ImageDraw.Draw(im)
    for i, y in enumerate((0.30, 0.42, 0.54, 0.66)):
        d.rounded_rectangle([0.12 * SIZE, y * SIZE, 0.88 * SIZE, (y + 0.10) * SIZE], radius=4,
                            fill=sh(_c(pal["M"]), -12 * (i % 2)) + (255,), outline=_c(pal["K"]) + (255,), width=2)
        d.line([0.16 * SIZE, y * SIZE + 2, 0.84 * SIZE, y * SIZE + 2], fill=_c(pal["L"]) + (255,), width=1)


def t_ribs(im, pal, seed=22):
    """肋排:3 条带骨肋排。"""
    d = ImageDraw.Draw(im)
    for i, (y, off) in enumerate(((0.30, 0.0), (0.50, 0.08), (0.70, 0.04))):
        d.rounded_rectangle([(0.16 + off) * SIZE, y * SIZE, (0.78 + off) * SIZE, (y + 0.14) * SIZE],
                            radius=SIZE // 14, fill=_c(pal["M"]) + (255,), outline=_c(pal["K"]) + (255,), width=2)
        d.ellipse([(0.74 + off) * SIZE, (y - 0.03) * SIZE, (0.90 + off) * SIZE, (y + 0.17) * SIZE],
                  fill=_c(pal["L"]) + (255,), outline=_c(pal["K"]) + (255,), width=2)
        d.line([(0.20 + off) * SIZE, y * SIZE + 3, (0.70 + off) * SIZE, y * SIZE + 3],
               fill=_c(pal["L"]) + (255,), width=2)


def t_nugget(im, pal, seed=23):
    """肉饼/鸡块:2 块厚椭圆饼。"""
    for i, (cx, cy) in enumerate(((0.40, 0.40), (0.60, 0.64))):
        paint_blobs(im, [(cx, cy, 0.22, 0.10, 4, i * 2)], _c(pal["M"]), seed=seed + i, noise=8)
    speckle(im, _c(pal["L"]), 6, seed + 5, 1, 1)


def t_surimi(im, pal, seed=24):
    """鱼糜/鱼糕:斜置圆柱两段。"""
    d = ImageDraw.Draw(im)
    for i, (cx, cy) in enumerate(((0.36, 0.36), (0.62, 0.62))):
        r = 0.16 * SIZE
        d.ellipse([cx * SIZE - r, cy * SIZE - r, cx * SIZE + r, cy * SIZE + r],
                  fill=_c(pal["M"]) + (255,), outline=_c(pal["K"]) + (255,), width=2)
        d.ellipse([cx * SIZE - r * 0.55, cy * SIZE - r * 0.55, cx * SIZE + r * 0.55, cy * SIZE + r * 0.55],
                  fill=_c(pal["L"]) + (255,))
        d.arc([cx * SIZE - r, cy * SIZE - r, cx * SIZE + r, cy * SIZE + r], 150, 260,
              fill=_c(pal["D"]) + (255,), width=3)


def t_beans(im, pal, seed=25):
    """豆粒堆:7-9 粒椭球豆。"""
    rnd = random.Random(seed)
    blobs = []
    for i in range(9):
        cx = rnd.uniform(0.22, 0.78)
        cy = rnd.uniform(0.30, 0.72)
        blobs.append((cx, cy, rnd.uniform(0.09, 0.13), 0.10, 4, i * 1.1))
    paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=8)
    d = ImageDraw.Draw(im)
    for i in range(4):
        cx, cy = rnd.uniform(0.25, 0.75) * SIZE, rnd.uniform(0.35, 0.70) * SIZE
        d.arc([cx - 5, cy - 3, cx + 5, cy + 3], 200, 340, fill=_c(pal["D"]) + (255,), width=1)


def t_tofu(im, pal, seed=26):
    """豆腐块:白色方砖+切面。"""
    d = ImageDraw.Draw(im)
    d.rounded_rectangle([0.14 * SIZE, 0.30 * SIZE, 0.86 * SIZE, 0.80 * SIZE], radius=6,
                        fill=_c(pal["M"]) + (255,), outline=_c(pal["K"]) + (255,), width=2)
    d.rounded_rectangle([0.20 * SIZE, 0.24 * SIZE, 0.80 * SIZE, 0.36 * SIZE], radius=4,
                        fill=_c(pal["L"]) + (255,), outline=_c(pal["K"]) + (255,), width=2)
    d.line([0.20 * SIZE, 0.55 * SIZE, 0.80 * SIZE, 0.55 * SIZE], fill=_c(pal["D"]) + (255,), width=1)
    speckle(im, _c(pal["L"]), 5, seed, 1, 1, box=(0.2, 0.4, 0.8, 0.75))


def t_kojiblk(im, pal, seed=27):
    """曲块:黄绿板块+接种白斑。"""
    paint_blobs(im, [(0.5, 0.52, 0.30, 0.10, 4, 0)], _c(pal["M"]), seed=seed, noise=10)
    speckle(im, _c("#FFFFFF"), 12, seed + 1, 1, 2, box=(0.25, 0.28, 0.75, 0.75))
    speckle(im, _c(pal["D"]), 8, seed + 2, 1, 1)
    d = ImageDraw.Draw(im)
    d.arc([0.20 * SIZE, 0.28 * SIZE, 0.80 * SIZE, 0.76 * SIZE], 150, 260, fill=_c(pal["K"]) + (255,), width=2)


# ============================================================================
# 菜肴容器模板(64x64)
# ============================================================================

BOWL = {"rim": "#8A7440", "body": "#B08A48", "inner": "#6E5230", "hl": "#D0A868"}
PLATE = {"rim": "#B8B2A4", "body": "#E8E4D8", "inner": "#F5F2EA", "hl": "#FFFFFF"}


def _bowl(im, liquid_pal):
    """汤碗:陶碗+汤面+浮料。"""
    d = ImageDraw.Draw(im)
    d.ellipse([0.08 * SIZE, 0.34 * SIZE, 0.92 * SIZE, 0.88 * SIZE], fill=_c(BOWL["body"]) + (255,),
              outline=_c(BOWL["rim"]) + (255,), width=3)
    d.ellipse([0.13 * SIZE, 0.34 * SIZE, 0.87 * SIZE, 0.56 * SIZE], fill=_c(BOWL["inner"]) + (255,))
    d.ellipse([0.16 * SIZE, 0.355 * SIZE, 0.84 * SIZE, 0.545 * SIZE], fill=_c(liquid_pal["M"]) + (255,))
    d.ellipse([0.22 * SIZE, 0.38 * SIZE, 0.50 * SIZE, 0.44 * SIZE], fill=_c(liquid_pal["L"]) + (255,))
    speckle(im, _c(liquid_pal["D"]), 6, 31, 1, 2, box=(0.25, 0.38, 0.75, 0.52))
    d.arc([0.12 * SIZE, 0.55 * SIZE, 0.88 * SIZE, 0.95 * SIZE], 40, 140, fill=_c(BOWL["hl"]) + (255,), width=2)


def _plate(im):
    d = ImageDraw.Draw(im)
    d.ellipse([0.06 * SIZE, 0.38 * SIZE, 0.94 * SIZE, 0.86 * SIZE], fill=_c(PLATE["body"]) + (255,),
              outline=_c(PLATE["rim"]) + (255,), width=3)
    d.ellipse([0.14 * SIZE, 0.44 * SIZE, 0.86 * SIZE, 0.80 * SIZE], fill=_c(PLATE["inner"]) + (255,))
    d.arc([0.16 * SIZE, 0.46 * SIZE, 0.84 * SIZE, 0.78 * SIZE], 170, 320, fill=_c(PLATE["hl"]) + (255,), width=2)


def t_soup(im, pal, seed=30):
    _bowl(im, pal)
    # 浮料:3 块料沉在汤面
    rnd = random.Random(seed)
    for i in range(3):
        cx, cy = rnd.uniform(0.28, 0.72), rnd.uniform(0.38, 0.48)
        r = rnd.uniform(0.05, 0.08)
        d = ImageDraw.Draw(im)
        d.ellipse([(cx - r) * SIZE, (cy - r * 0.6) * SIZE, (cx + r) * SIZE, (cy + r * 0.6) * SIZE],
                  fill=_c(pal["D"]) + (255,), outline=_c(pal["K"]) + (255,))
        d.point((cx * SIZE, cy * SIZE), fill=_c(pal["L"]) + (255,))


def t_plate(im, pal, seed=31):
    _plate(im)
    paint_blobs(im, [(0.50, 0.52, 0.20, 0.12, 4, 0)], _c(pal["M"]), seed=seed, noise=8)
    d = ImageDraw.Draw(im)
    d.ellipse([0.40 * SIZE, 0.40 * SIZE, 0.58 * SIZE, 0.48 * SIZE], fill=_c(pal["L"]) + (255,))


def t_stirfry(im, pal, seed=32):
    _plate(im)
    # 混炒堆:主料+配菜双色块
    rnd = random.Random(seed)
    blobs = []
    for i in range(7):
        cx = rnd.uniform(0.30, 0.70)
        cy = rnd.uniform(0.36, 0.60)
        blobs.append((cx, cy, rnd.uniform(0.07, 0.11), 0.15, 5, i))
    paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=10)
    speckle(im, _c("dish_green" in PALETTES and PALETTES["dish_green"]["M"] or "#8FBF5A"), 5, seed + 1, 1, 2,
            box=(0.32, 0.38, 0.68, 0.60))
    speckle(im, _c(pal["L"]), 6, seed + 2, 1, 1)


def t_fried(im, pal, seed=33):
    """炸物:无盘,金黄不规则炸块+面包糠点。"""
    rnd = random.Random(seed)
    blobs = [(0.36, 0.42, 0.20, 0.14, 5, 0), (0.64, 0.58, 0.18, 0.12, 4, 2)]
    paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=12,
                char_ratio=0.06, char_col=_c(pal["K"]))
    speckle(im, _c(pal["L"]), 10, seed + 1, 1, 1)
    speckle(im, _c(pal["D"]), 6, seed + 2, 1, 1)


# ============================================================================
# 串类全家(64x64,移植 proc_skewer_hi.py 有机画法)
# ============================================================================

def skewer64(im, pal_meat, stick="wood", cooked=False, long_shape=False,
             seasoning=None, seed=7):
    """斜签+签上食材。long_shape=整椒/鸡爪两长段;cubes=三块方料。"""
    paint_stick(im, wood=(stick == "wood"), seed=seed)
    if stick == "iron":
        ring_stick(im, wood=False)
    base = _c(pal_meat["M"]) if not cooked else _c(pal_meat["M"])
    if long_shape:
        blobs = [(0.34, 0.66, 0.13, 0.18, 4, 0), (0.66, 0.34, 0.13, 0.18, 4, 1)]
        paint_blobs(im, blobs, base, seed=seed, noise=9,
                    char_ratio=0.10 if cooked else 0.0,
                    fat_ratio=0.0 if cooked else 0.10)
    else:
        blobs = [(0.27, 0.70, 0.155, 0.12, 3, 0), (0.50, 0.48, 0.155, 0.12, 3, 1),
                 (0.73, 0.26, 0.155, 0.12, 3, 2)]
        paint_blobs(im, blobs, base, seed=seed, noise=9,
                    char_ratio=0.10 if cooked else 0.0,
                    fat_ratio=0.0 if cooked else 0.08)
    if seasoning == "salt":
        speckle(im, (255, 255, 255), 18, seed + 10, 1, 1, box=(0.20, 0.20, 0.80, 0.78))
    elif seasoning == "chili":
        speckle(im, _c("#E04818"), 16, seed + 10, 1, 2, box=(0.20, 0.20, 0.80, 0.78))
    d = ImageDraw.Draw(im)
    # 竹节痕(木签)
    if stick == "wood":
        d.point([(0.30 * SIZE, 0.66 * SIZE), (0.31 * SIZE, 0.65 * SIZE)], fill=_c("#6E4A26") + (255,))
        d.point([(0.62 * SIZE, 0.36 * SIZE), (0.63 * SIZE, 0.35 * SIZE)], fill=_c("#6E4A26") + (255,))


SKEWER_PARAMS = {
    "skewer_wood": dict(stick="wood", cooked=False, long_shape=False, seasoning=None, bare=True),
    "skewer_iron": dict(stick="iron", cooked=False, long_shape=False, seasoning=None, bare=True),
    "skewer_raw": dict(stick="wood", cooked=False, long_shape=False),
    "skewer_raw_long": dict(stick="wood", cooked=False, long_shape=True),
    "skewer_cooked": dict(stick="wood", cooked=True, long_shape=False),
    "skewer_cooked_long": dict(stick="wood", cooked=True, long_shape=True),
    "skewer_cooked_salt": dict(stick="wood", cooked=True, long_shape=False, seasoning="salt"),
    "skewer_cooked_chili": dict(stick="wood", cooked=True, long_shape=False, seasoning="chili"),
    "skewer_iron_cooked": dict(stick="iron", cooked=True, long_shape=False),
    "skewer_iron_cooked_long": dict(stick="iron", cooked=True, long_shape=True),
}


# ============================================================================
# 作物:13 产物(形态原型参数化)+ 13 种子 + 4 灰度生长模板
# ============================================================================

# 产物原型:shape -> pod / cob / grains / round_fruit / bulb / chili_pod / leafy_head /
#          grape_bunch / berries / leaves / cone / kernel_bag / peanut_pod
CROPS = {
    "soybean":  dict(shape="pod_cluster", pal={"K": "#2F4A1E", "M": "#6E9B4C", "L": "#8FBF68", "D": "#4E7A33"},
                 inner="#D9C877"),
    "corn":     dict(shape="cob", pal={"K": "#7A5A10", "M": "#F2CB4B", "L": "#FBE9A0", "D": "#D9A92E"},
                 inner="#7FA84F"),
    "rice":     dict(shape="grain_sheaf", pal={"K": "#8A7A2E", "M": "#E8D598", "L": "#F5E8C0", "D": "#C9B070"}),
    "barley":   dict(shape="grain_sheaf", pal={"K": "#8A7A3E", "M": "#DCCB88", "L": "#EFE3B4", "D": "#B8A45C"}),
    "peanut":   dict(shape="peanut_pod", pal={"K": "#6E5A30", "M": "#D9B478", "L": "#EBD0A0", "D": "#B08A48"}),
    "tomato":   dict(shape="round_fruit", pal={"K": "#6E1F16", "M": "#E04B3A", "L": "#F2836F", "D": "#C23325"},
                 leaf="#3E5C26"),
    "onion":    dict(shape="bulb", pal={"K": "#5C5260", "M": "#E8E0EC", "L": "#F7F3F8", "D": "#C9BECF"},
                 inner="#B09AC0"),
    "chili":    dict(shape="chili_pod", pal={"K": "#7A1810", "M": "#D93A2B", "L": "#F06A55", "D": "#B02417"},
                 leaf="#3E5C26"),
    "cabbage":  dict(shape="leafy_head", pal={"K": "#3E5C26", "M": "#A8C97A", "L": "#C9E09A", "D": "#7FA84F"}),
    "grape":    dict(shape="grape_bunch", pal={"K": "#4A1E4E", "M": "#9A5A9E", "L": "#C08AC0", "D": "#6E3E70"},
                 leaf="#3E5C26"),
    "coffee":   dict(shape="berries", pal={"K": "#5A1810", "M": "#C04030", "L": "#E07060", "D": "#8E2A1E"},
                 leaf="#3E5C26"),
    "tea":      dict(shape="leaves", pal={"K": "#2E4A20", "M": "#4E7A3E", "L": "#6E9B54", "D": "#3A5E2C"}),
    "hops":     dict(shape="cone", pal={"K": "#4A6A2A", "M": "#90C46A", "L": "#B8E094", "D": "#6E9B4C"}),
}

# 种子形状(沿用旧生成器分类)与配色
SEEDS = {
    "soybean": ("bean_pair", {"K": "#6E5B26", "M": "#D9C877", "L": "#F0E6A8", "D": "#B5A050"}),
    "corn": ("single_kernel", {"K": "#8A6A1E", "M": "#F2CB4B", "L": "#FBE9A0", "D": "#D9A92E"}),
    "rice": ("grains", {"K": "#8A7A3E", "M": "#E8D598", "L": "#F5E8C0", "D": "#C9B070"}),
    "barley": ("grains", {"K": "#8A7A4A", "M": "#DCCB88", "L": "#EFE3B4", "D": "#B8A45C"}),
    "peanut": ("bean_pair", {"K": "#6E5A30", "M": "#D9B478", "L": "#EBD0A0", "D": "#B08A48"}),
    "tomato": ("dots", {"K": "#7A5A28", "M": "#E0C878", "L": "#F2E3A8", "D": "#B89A50"}),
    "onion": ("dots", {"K": "#5C5260", "M": "#D8CCD8", "L": "#F0E8F0", "D": "#A898B0"}),
    "chili": ("dots", {"K": "#7A5A20", "M": "#E8D9A8", "L": "#F5ECCB", "D": "#C9B478"}),
    "cabbage": ("dots", {"K": "#5C5226", "M": "#C9B868", "L": "#E0D498", "D": "#A08E48"}),
    "grape": ("dots", {"K": "#4A3A5E", "M": "#9A8AB8", "L": "#C0B0D8", "D": "#6E5E8E"}),
    "coffee": ("bean_pair", {"K": "#4A2E1A", "M": "#8E6848", "L": "#B08A64", "D": "#6E4E32"}),
    "tea": ("dots", {"K": "#3E4A28", "M": "#8A9858", "L": "#AAB878", "D": "#6E7A40"}),
    "hops": ("grains", {"K": "#5A6A32", "M": "#A8C470", "L": "#C8E098", "D": "#84A050"}),
}


def crop_product(im, crop, seed=40):
    spec = CROPS[crop]
    pal = spec["pal"]
    shape = spec["shape"]
    d = ImageDraw.Draw(im)
    if shape == "pod_cluster":
        for i, (cx, cy, ang) in enumerate(((0.34, 0.42, 0.5), (0.58, 0.52, 0.4), (0.44, 0.66, 0.6))):
            blobs = [(cx, cy, 0.13, 0.16, 6, i)]
            paint_blobs(im, blobs, _c(pal["M"]), seed=seed + i, noise=8)
            # 豆粒鼓包
            for k in range(3):
                bx = cx + (k - 1) * 0.055
                d.ellipse([(bx - 0.045) * SIZE, (cy - 0.045) * SIZE, (bx + 0.045) * SIZE, (cy + 0.045) * SIZE],
                          fill=sh(_c(spec.get("inner", "#D9C877")), k * 8 - 8) + (255,))
    elif shape == "cob":
        paint_blobs(im, [(0.5, 0.5, 0.24, 0.10, 8, 0)], _c(pal["M"]), seed=seed, noise=6)
        for gy in range(6):
            for gx in range(4):
                x = (0.34 + gx * 0.105) * SIZE + (2 if gy % 2 else 0)
                y = (0.30 + gy * 0.068) * SIZE
                d.point((x, y), fill=_c(pal["D"]) + (255,))
        for i, (cx, cy, ang) in enumerate(((0.24, 0.62, 0.8), (0.76, 0.38, 2.3))):
            paint_blobs(im, [(cx, cy, 0.10, 0.2, 5, i)], _c(spec.get("inner", "#7FA84F")), seed=seed + 5 + i)
    elif shape == "grain_sheaf":
        rnd = random.Random(seed)
        blobs = []
        for i in range(14):
            cx = rnd.uniform(0.25, 0.75)
            cy = rnd.uniform(0.22, 0.70)
            blobs.append((cx, cy, rnd.uniform(0.05, 0.075), 0.25, 5, i))
        paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=10)
        for i in range(5):
            x, y = rnd.uniform(0.3, 0.7) * SIZE, rnd.uniform(0.3, 0.6) * SIZE
            d.line([(x, y), (x, y - 0.12 * SIZE)], fill=_c(pal["D"]) + (255,), width=1)
    elif shape == "peanut_pod":
        paint_blobs(im, [(0.5, 0.5, 0.20, 0.10, 4, 0)], _c(pal["M"]), seed=seed, noise=8)
        d = ImageDraw.Draw(im)
        d.line([0.44 * SIZE, 0.38 * SIZE, 0.44 * SIZE, 0.62 * SIZE], fill=_c(pal["D"]) + (255,), width=3)
        speckle(im, _c(pal["D"]), 8, seed + 1, 1, 1)
    elif shape == "round_fruit":
        paint_blobs(im, [(0.5, 0.54, 0.28, 0.08, 4, 0)], _c(pal["M"]), seed=seed, noise=6)
        d = ImageDraw.Draw(im)
        d.ellipse([0.42 * SIZE, 0.26 * SIZE, 0.58 * SIZE, 0.36 * SIZE], fill=_c(spec.get("leaf")) + (255,))
        d.ellipse([0.40 * SIZE, 0.38 * SIZE, 0.52 * SIZE, 0.48 * SIZE], fill=_c(pal["L"]) + (255,))
    elif shape == "bulb":
        paint_blobs(im, [(0.5, 0.56, 0.26, 0.10, 4, 0)], _c(pal["M"]), seed=seed, noise=5)
        d = ImageDraw.Draw(im)
        d.polygon([(0.44 * SIZE, 0.30 * SIZE), (0.56 * SIZE, 0.30 * SIZE), (0.52 * SIZE, 0.16 * SIZE),
                   (0.48 * SIZE, 0.16 * SIZE)], fill=_c(pal["D"]) + (255,))
        for x in (0.42, 0.50, 0.58):
            d.line([x * SIZE, 0.34 * SIZE, x * SIZE, 0.78 * SIZE], fill=_c(spec.get("inner")) + (255,), width=1)
    elif shape == "chili_pod":
        blobs = [(0.44, 0.40, 0.10, 0.15, 4, 0), (0.56, 0.62, 0.11, 0.20, 5, 1)]
        paint_blobs(im, blobs, _c(pal["M"]), seed=seed, noise=7)
        d = ImageDraw.Draw(im)
        d.polygon([(0.38 * SIZE, 0.30 * SIZE), (0.52 * SIZE, 0.30 * SIZE), (0.48 * SIZE, 0.22 * SIZE),
                   (0.42 * SIZE, 0.22 * SIZE)], fill=_c(spec.get("leaf")) + (255,))
    elif shape == "leafy_head":
        paint_blobs(im, [(0.5, 0.52, 0.30, 0.12, 5, 0)], _c(pal["M"]), seed=seed, noise=6)
        d = ImageDraw.Draw(im)
        for i in range(4):
            d.arc([0.24 * SIZE, (0.30 + i * 0.10) * SIZE, 0.76 * SIZE, (0.62 + i * 0.10) * SIZE],
                  200, 340, fill=_c(pal["D"]) + (255,), width=2)
        d.ellipse([0.38 * SIZE, 0.36 * SIZE, 0.62 * SIZE, 0.50 * SIZE], fill=_c(pal["L"]) + (255,))
    elif shape == "grape_bunch":
        rnd = random.Random(seed)
        layout = [(0.5, 0.24), (0.38, 0.36), (0.62, 0.36), (0.30, 0.50), (0.50, 0.50),
                  (0.70, 0.50), (0.40, 0.64), (0.60, 0.64), (0.50, 0.76)]
        for i, (cx, cy) in enumerate(layout):
            r = 0.085
            d.ellipse([(cx - r) * SIZE, (cy - r) * SIZE, (cx + r) * SIZE, (cy + r) * SIZE],
                      fill=sh(_c(pal["M"]), rnd.randint(-14, 10)) + (255,),
                      outline=_c(pal["K"]) + (255,))
            d.point((cx * SIZE - 2, cy * SIZE - 2), fill=_c(pal["L"]) + (255,))
        d.line([0.5 * SIZE, 0.10 * SIZE, 0.5 * SIZE, 0.20 * SIZE], fill=_c(pal.get("leaf", "#3E5C26")) + (255,), width=3)
    elif shape == "berries":
        rnd = random.Random(seed)
        layout = [(0.40, 0.34), (0.60, 0.30), (0.50, 0.48), (0.34, 0.56), (0.66, 0.52), (0.52, 0.68)]
        for i, (cx, cy) in enumerate(layout):
            r = 0.095
            d.ellipse([(cx - r) * SIZE, (cy - r) * SIZE, (cx + r) * SIZE, (cy + r) * SIZE],
                      fill=sh(_c(pal["M"]), rnd.randint(-14, 10)) + (255,), outline=_c(pal["K"]) + (255,))
            d.point((cx * SIZE - 2, cy * SIZE - 3), fill=_c(pal["L"]) + (255,))
        paint_blobs(im, [(0.42, 0.70, 0.09, 0.2, 4, 0), (0.62, 0.74, 0.08, 0.2, 4, 1)],
                    _c(pal.get("leaf", "#3E5C26")), seed=seed + 3)
    elif shape == "leaves":
        for i, (cx, cy, ang) in enumerate(((0.36, 0.44, 0.6), (0.60, 0.50, 2.5), (0.46, 0.66, 1.6))):
            paint_blobs(im, [(cx, cy, 0.12, 0.25, 5, i)], _c(pal["M"]), seed=seed + i, noise=6)
        d = ImageDraw.Draw(im)
        d.line([0.28 * SIZE, 0.80 * SIZE, 0.68 * SIZE, 0.26 * SIZE], fill=_c(pal["K"]) + (255,), width=3)
    elif shape == "cone":
        paint_blobs(im, [(0.5, 0.52, 0.18, 0.15, 7, 0)], _c(pal["M"]), seed=seed, noise=8)
        d = ImageDraw.Draw(im)
        for gy in range(5):
            d.arc([0.34 * SIZE, (0.30 + gy * 0.09) * SIZE, 0.66 * SIZE, (0.50 + gy * 0.09) * SIZE],
                  200, 340, fill=_c(pal["D"]) + (255,), width=2)


def crop_seed(im, crop, seed=50):
    shape, pal = SEEDS[crop]
    d = ImageDraw.Draw(im)
    if shape == "bean_pair":
        for i, (cx, cy) in enumerate(((0.38, 0.40), (0.60, 0.58))):
            paint_blobs(im, [(cx, cy, 0.11, 0.10, 4, i)], _c(pal["M"]), seed=seed + i, noise=6)
            d.line([(cx - 0.03) * SIZE, cy * SIZE, (cx + 0.03) * SIZE, cy * SIZE],
                   fill=_c(pal["D"]) + (255,), width=1)
    elif shape == "grains":
        rnd = random.Random(seed)
        for i in range(6):
            cx, cy = rnd.uniform(0.26, 0.74), rnd.uniform(0.28, 0.72)
            ang = rnd.uniform(0, math.pi)
            dx, dy = math.cos(ang) * 0.07, math.sin(ang) * 0.07
            d.line([(cx - dx) * SIZE, (cy - dy) * SIZE, (cx + dx) * SIZE, (cy + dy) * SIZE],
                   fill=_c(pal["M"]) + (255,), width=max(2, SIZE // 24))
            d.point(((cx + dx) * SIZE, (cy + dy) * SIZE), fill=_c(pal["L"]) + (255,))
    else:  # dots / single_kernel
        rnd = random.Random(seed)
        count = 6 if shape == "dots" else 1
        for i in range(count):
            cx, cy = rnd.uniform(0.30, 0.70), rnd.uniform(0.30, 0.70)
            r = 0.13 if count == 1 else 0.065
            paint_blobs(im, [(cx, cy, r, 0.08, 4, i)], _c(pal["M"]), seed=seed + i, noise=6)
            d = ImageDraw.Draw(im)
            d.point((cx * SIZE, cy * SIZE - r * SIZE * 0.4), fill=_c(pal["L"]) + (255,))


# 灰度生长模板(64x64,W=255/S=200/D=128/K=64,运行时 BlockColors multiply 染色)
STAGE_GRAY = {"W": 255, "S": 200, "D": 128, "K": 64}


def crop_stage(im, stage, seed=60):
    """stage 0-3:芽/幼苗/成株/满株。灰度,乘染色。"""
    px = im.load()
    rnd = random.Random(seed + stage)
    n = SIZE
    cx = n // 2

    def stem(x0, y0, x1, y1, w):
        steps = int(max(abs(x1 - x0), abs(y1 - y0)) * 1.5)
        for i in range(steps):
            t = i / max(steps - 1, 1)
            x = int(x0 + (x1 - x0) * t)
            y = int(y0 + (y1 - y0) * t)
            g = STAGE_GRAY["D"] if t < 0.3 else STAGE_GRAY["K"]
            for dx in range(-(w // 2), w // 2 + 1):
                if 0 <= x + dx < n and 0 <= y < n:
                    px[x + dx, y] = (g, g, g, 255)

    def leaf(x, y, r):
        for yy in range(-r, r + 1):
            for xx in range(-r, r + 1):
                d2 = (xx * xx) / (r * r) + (yy * yy) / ((r * 0.55) ** 2)
                if d2 <= 1.0 and 0 <= x + xx < n and 0 <= y + yy < n:
                    g = STAGE_GRAY["W"] if d2 < 0.4 else STAGE_GRAY["S"]
                    if rnd.random() < 0.15:
                        g = STAGE_GRAY["D"]
                    px[x + xx, y + yy] = (g, g, g, 255)

    base_y = int(0.86 * n)
    stem(cx, base_y, cx, int(0.62 * n), 3)
    if stage >= 1:
        leaf(cx - 6, int(0.60 * n), 7)
        leaf(cx + 6, int(0.58 * n), 7)
        stem(cx, int(0.62 * n), cx, int(0.48 * n), 2)
    if stage >= 2:
        for ang in (-0.9, -0.45, 0.45, 0.9):
            x2 = cx + int(math.sin(ang) * 0.28 * n)
            y2 = int(0.48 * n) + int(0.10 * n * math.cos(ang))
            stem(cx, int(0.50 * n), x2, y2, 2)
            leaf(x2, y2, 6)
    if stage >= 3:
        for ang in (-1.3, -0.7, 0.7, 1.3):
            x2 = cx + int(math.sin(ang) * 0.34 * n)
            y2 = int(0.44 * n) + int(0.14 * n * abs(math.cos(ang)))
            stem(cx, int(0.46 * n), x2, y2, 2)
            leaf(x2, y2, 8)
        leaf(cx, int(0.34 * n), 9)
        leaf(cx - 10, int(0.42 * n), 7)
        leaf(cx + 10, int(0.42 * n), 7)


# ============================================================================
# 物品清单:食材+菜肴(prep FORMS 全量)+ 大豆链 ITEMS
# ============================================================================

FORMS = [
    ("beef_slice", "slices", "beef"), ("fatty_beef_roll", "strips", "beef"),
    ("cooked_beef_slice", "slices", "toast_brown"), ("beef_strip", "strips", "beef"),
    ("beef_cube", "diced", "beef"), ("beef_ribs", "ribs", "beef"),
    ("beef_minced", "minced", "beef"),
    ("pork_slice", "slices", "pork"), ("cooked_pork_slice", "slices", "dish_pink"),
    ("pork_strip", "strips", "pork"),
    ("pork_cube", "diced", "pork"), ("pork_ribs", "ribs", "pork"),
    ("pork_minced", "minced", "pork"),
    ("mutton_slice", "slices", "mutton"), ("fatty_mutton_roll", "strips", "mutton"),
    ("cooked_mutton_slice", "slices", "toast_brown"),
    ("mutton_cube", "diced", "mutton"),
    ("mutton_ribs", "ribs", "mutton"), ("mutton_minced", "minced", "mutton"),
    ("chicken_slice", "slices", "chicken"), ("cooked_chicken_slice", "slices", "dish_gold"),
    ("chicken_shred", "strips", "chicken"),
    ("chicken_diced", "diced", "chicken"), ("chicken_cuts", "nugget", "chicken"),
    ("chicken_feet", "nugget", "chicken"), ("chicken_minced", "minced", "chicken"),
    ("fish_slice", "slices", "fish"), ("cooked_fish_slice", "slices", "noodle_white"),
    ("fish_cube", "diced", "fish"),
    ("fish_surimi", "surimi", "fish"), ("fish_bones", "strips", "fish"),
    ("chili_ring", "ring", "chili"),
    ("potato_slice", "slices", "potato"), ("apple_slice", "slices", "apple"),
    ("tomato_slice", "slices", "tomato"),
    ("potato_strip", "strips", "potato"), ("carrot_strip", "strips", "carrot"),
    ("cabbage_strip", "strips", "cabbage"), ("chili_strip", "strips", "chili"),
    ("tomato_diced", "diced", "tomato"), ("onion_diced", "diced", "onion"),
    ("tofu_cube", "diced", "tofu_white"),
    ("fries_blank", "fries", "potato"), ("chili_diced", "diced", "chili"),
    ("potato_diced", "diced", "potato"),
    ("onion_minced", "minced", "onion"), ("garlic_minced", "minced", "garlic"),
    ("chili_powder", "powder", "chili"),
    ("apple_flesh", "flesh", "apple"),
    ("noodle", "noodle", "dough"), ("dough_sheet", "sheet", "dough"),
    ("rice_flour", "powder", "rice_flour"), ("rice_dough", "flesh", "rice_dough"),
    ("rice_noodles", "noodle", "rice_noodles"),
    ("bread", "nugget", "baked"), ("toast", "slices", "toast_brown"),
    ("sweet_bread", "nugget", "dish_gold"), ("cake", "plate", "cake_cream"),
    ("apple_pie", "plate", "dish_gold"), ("baguette", "fries", "baked"),
    ("dinner_roll", "nugget", "dish_cream"), ("corn_bread", "diced", "soup_yellow"),
    ("baked_corn", "nugget", "soup_yellow"), ("garlic_baguette", "fries", "dish_cream"),
    ("bread_slice", "slices", "baked"), ("baguette_slice", "slices", "toast_brown"),
    ("burger_bun", "plate", "dish_cream"),
    ("raw_bread", "flesh", "dough"), ("raw_toast", "flesh", "dough"),
    ("raw_baguette", "fries", "dough"),
    ("tomato_egg_soup", "soup", "soup_red"), ("beef_soup", "soup", "beef"),
    ("chicken_soup", "soup", "chicken"), ("peanut_soup", "soup", "baked"),
    ("geda_soup", "soup", "soup_white"),
    ("steamed_fish", "plate", "fish"), ("steamed_meat_patty", "plate", "chicken"),
    ("tomato_beef", "stirfry", "tomato"), ("onion_fried_lamb", "stirfry", "mutton"),
    ("cabbage_fried_pork", "stirfry", "dish_green"), ("muxu_pork", "stirfry", "pork"),
    ("grilled_fish", "fries", "fish"),
    ("raw_dinner_roll", "flesh", "dough"),
    ("raw_beef_patty", "nugget", "beef"), ("cooked_beef_patty", "nugget", "toast_brown"),
    ("raw_chicken_cutlet", "flesh", "chicken"), ("raw_chicken_tender", "strips", "chicken"),
    ("vegetable_sandwich", "stirfry", "dish_green"), ("egg_sandwich", "plate", "soup_yellow"),
    ("beef_sandwich", "stirfry", "beef"), ("chicken_sandwich", "stirfry", "chicken"),
    ("beef_burger", "nugget", "beef"), ("chicken_burger", "nugget", "chicken"),
    ("wooden_skewer", "skewer_wood", "skewer_wood"), ("iron_skewer", "skewer_iron", "skewer_iron"),
    ("raw_lamb_skewer", "skewer_raw", "mutton"), ("raw_beef_skewer", "skewer_raw", "beef"),
    ("raw_chicken_skewer", "skewer_raw", "chicken"),
    ("raw_chili_skewer", "skewer_raw_long", "chili"),
    ("raw_chicken_feet_skewer", "skewer_raw_long", "chicken"),
    ("campfire_lamb_skewer", "skewer_cooked", "mutton_cooked"),
    ("campfire_beef_skewer", "skewer_cooked", "beef_cooked"),
    ("campfire_chicken_skewer", "skewer_cooked", "chicken_cooked"),
    ("campfire_chili_skewer", "skewer_cooked_long", "chili"),
    ("campfire_chicken_feet_skewer", "skewer_cooked_long", "chicken_cooked"),
    ("grilled_lamb_skewer", "skewer_cooked", "mutton_cooked"),
    ("grilled_beef_skewer", "skewer_cooked", "beef_cooked"),
    ("grilled_chicken_skewer", "skewer_cooked", "chicken_cooked"),
    ("salt_grilled_lamb_skewer", "skewer_cooked_salt", "mutton_cooked"),
    ("salt_grilled_beef_skewer", "skewer_cooked_salt", "beef_cooked"),
    ("salt_grilled_chicken_skewer", "skewer_cooked_salt", "chicken_cooked"),
    ("chili_grilled_lamb_skewer", "skewer_cooked_chili", "mutton_cooked"),
    ("chili_grilled_beef_skewer", "skewer_cooked_chili", "beef_cooked"),
    ("chili_grilled_chicken_skewer", "skewer_cooked_chili", "chicken_cooked"),
    ("soy_grilled_lamb_skewer", "skewer_cooked", "soy_glaze"),
    ("soy_grilled_beef_skewer", "skewer_cooked", "soy_glaze"),
    ("soy_grilled_chicken_skewer", "skewer_cooked", "soy_glaze"),
    ("grilled_chili_skewer", "skewer_cooked_long", "chili"),
    ("grilled_chicken_feet_skewer", "skewer_cooked_long", "chicken_cooked"),
    ("iron_lamb_skewer", "skewer_iron_cooked", "mutton_cooked"),
    ("iron_beef_roll_skewer", "skewer_iron_cooked_long", "beef_cooked"),
    ("iron_beef_skewer", "skewer_iron_cooked", "beef_cooked"),
    ("pickled_cabbage", "diced", "kimchi"),
    ("sliced_bread", "slices", "baked"), ("sliced_baguette", "fries", "toast_brown"),
    ("sliced_burger_bun", "plate", "dish_cream"),
    ("fruit_platter", "stirfry", "dish_green"), ("sugar_tomato", "plate", "dish_red"),
    ("chicken_cold_noodles", "noodle", "dish_pink"), ("fried_egg", "plate", "soup_yellow"),
    ("plain_noodles", "noodle", "noodle_white"), ("hand_fried_rice", "stirfry", "soup_yellow"),
    ("hand_steamed_egg", "plate", "soup_yellow"), ("hand_steamed_corn", "plate", "soup_yellow"),
    ("rice_noodle_soup", "soup", "rice_noodles"), ("tomato_soup", "soup", "soup_red"),
    ("vegetable_soup", "soup", "dish_green"), ("rib_soup", "soup", "soup_brown"),
    ("rice_porridge", "soup", "soup_white"), ("corn_soup", "soup", "soup_yellow"),
    ("dumplings", "soup", "soup_white"), ("shabu_beef", "soup", "beef"),
    ("shabu_mutton", "soup", "mutton"), ("beef_rice_bowl", "stirfry", "soup_brown"),
    ("steamed_rice", "plate", "soup_white"), ("mantou", "plate", "dish_cream"),
    ("baozi", "plate", "dish_cream"), ("steamed_egg", "plate", "soup_yellow"),
    ("steamed_corn", "plate", "soup_yellow"), ("rice_cake", "plate", "soup_white"),
    ("rice_steamed_pork", "plate", "soup_brown"), ("white_cake", "plate", "soup_white"),
    ("tomato_scrambled_egg", "stirfry", "soup_yellow"), ("fried_rice", "stirfry", "soup_yellow"),
    ("fried_noodles", "stirfry", "soup_brown"), ("chili_shredded_pork", "stirfry", "dish_red"),
    ("stir_fried_pork", "stirfry", "dish_red"), ("kung_pao_chicken", "stirfry", "dish_red"),
    ("stir_fried_vegetables", "stirfry", "dish_green"), ("fried_rice_noodles", "stirfry", "soup_brown"),
    ("beef_chow_fun", "stirfry", "soup_brown"),
    ("fries", "fried", "dish_gold"), ("potato_chips", "fried", "dish_gold"),
    ("fried_chicken_cuts", "fried", "dish_gold"), ("fried_peanuts", "fried", "soup_brown"),
    ("chicken_cutlet", "fried", "dish_gold"), ("chicken_tender", "fries", "dish_gold"),
    ("fried_fish_fillet", "fried", "dish_gold"), ("onion_rings", "fried", "dish_gold"),
    ("spring_roll", "fried", "dish_gold"), ("rice_cracker", "fried", "soup_white"),
    # 大豆链
    ("bran", "powder", "bran"), ("cooked_soybean", "beans", "cooked_soybean"),
    ("koji", "kojiblk", "koji"), ("koji_starter", "powder", "koji_starter"),
    ("koji_batch", "kojiblk", "koji_batch"), ("okara", "minced", "okara"),
    ("soybean_meal", "powder", "soybean_meal"), ("soybean_pomace", "minced", "soybean_pomace"),
    ("firm_tofu", "tofu", "firm_tofu"), ("soft_tofu", "tofu", "soft_tofu"),
    ("tofu_pudding", "soup", "pudding_white"),
    ("qianye_tofu_blank", "tofu", "qianye_blank"), ("qianye_tofu", "tofu", "qianye_tofu"),
    ("dried_tofu", "tofu", "dried_tofu"),
    ("tofu_sheet_blank", "sheet", "sheet_blank"), ("tofu_sheet", "sheets", "tofu_sheet"),
]

# 模板注册表:名称 -> 函数(im, pal, seed)
PATTERNS = {
    "slices": t_slices, "strips": t_strips, "diced": t_diced, "minced": t_minced,
    "powder": t_powder, "ring": t_ring, "flesh": t_flesh, "fries": t_fries,
    "noodle": t_noodle, "sheet": t_sheet, "sheets": t_sheets, "ribs": t_ribs,
    "nugget": t_nugget, "surimi": t_surimi, "beans": t_beans, "tofu": t_tofu,
    "kojiblk": t_kojiblk,
    "soup": t_soup, "plate": t_plate, "stirfry": t_stirfry, "fried": t_fried,
}


def render_item(item_id, form, pal_name, seed):
    im = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    pal = PALETTES[pal_name]
    if form in SKEWER_PARAMS:
        p = SKEWER_PARAMS[form]
        if p.get("bare"):
            paint_stick(im, wood=(p["stick"] == "wood"), seed=seed)
            if p["stick"] == "iron":
                ring_stick(im, wood=False)
        else:
            skewer64(im, pal, stick=p.get("stick", "wood"), cooked=p.get("cooked", False),
                     long_shape=p.get("long_shape", False), seasoning=p.get("seasoning"), seed=seed)
    else:
        PATTERNS[form](im, pal, seed=seed)
    return im


# ============================================================================
# 审查图
# ============================================================================

def build_preview(images, out_path, cols=8, scale=4):
    if not images:
        return
    cell, gut, label = SIZE * scale, 12, 18
    rows = (len(images) + cols - 1) // cols
    w = cols * (cell + gut) + gut
    h = rows * (cell + label + gut) + gut
    sheet = Image.new("RGBA", (w, h), (26, 26, 30, 255))
    dr = ImageDraw.Draw(sheet)
    for i, (name, im) in enumerate(images):
        r, c = divmod(i, cols)
        x, y = gut + c * (cell + gut), gut + r * (cell + label + gut)
        big = im.resize((cell, cell), Image.NEAREST)
        sheet.paste(big, (x, y), big)
        dr.text((x + 2, y + cell + 2), name[:22], fill=(210, 210, 210, 255))
    sheet.save(out_path)


def group_of(name):
    if name.endswith("_skewer") or name in ("wooden_skewer", "iron_skewer"):
        return "skewer"
    if name in CROPS or name + "_seeds" in [c + "_seeds" for c in CROPS]:
        return "crop"
    if name.endswith("_soup") or name in ("rice_porridge", "dumplings", "shabu_beef", "shabu_mutton",
                                          "tofu_pudding", "rice_noodle_soup"):
        return "soup"
    if any(name.endswith(s) for s in ("_rice", "_egg", "_pork", "_chicken_burger", "_beef_burger")):
        return "dish"
    return "ingredient"


def main():
    ap = argparse.ArgumentParser(description="64x64 程序化贴图生成(未执行,交付后用户确认再跑)")
    ap.add_argument("--only", help="只生成 id 含该关键字的物品")
    ap.add_argument("--list", action="store_true", help="只列清单不落盘")
    ap.add_argument("--out", help="输出目录覆盖(默认写进资源目录)")
    ap.add_argument("--no-preview", action="store_true")
    args = ap.parse_args()

    items = [(i, f, p) for (i, f, p) in FORMS if not args.only or args.only in i]
    items += [(c, "crop_product", c) for c in CROPS if not args.only or args.only in c]
    items += [(c + "_seeds", "crop_seed", c) for c in CROPS if not args.only or args.only in c]

    print(f"清单 {len(items)} 项 + 4 张灰度生长模板,尺寸 {SIZE}x{SIZE}")
    if args.list:
        for i, f, p in items:
            print(f"  {i:34s} {f:24s} {p}")
        return

    out_dir = args.out or ITEM_DIR
    os.makedirs(out_dir, exist_ok=True)
    made = []
    for idx, (item_id, form, pal_name) in enumerate(items):
        seed = 100 + idx
        im = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
        if form == "crop_product":
            crop_product(im, pal_name, seed=seed)
        elif form == "crop_seed":
            crop_seed(im, pal_name, seed=seed)
        else:
            im = render_item(item_id, form, pal_name, seed)
        im.save(os.path.join(out_dir, item_id + ".png"))
        made.append((item_id, im))
        print(f"  ok {item_id}")

    # 灰度生长模板(仅全量模式生成;--out 时随重定向,避免误写资源目录)
    if not args.only:
        stage_dir = os.path.join(args.out, "block", "crop") if args.out else BLOCK_DIR
        os.makedirs(stage_dir, exist_ok=True)
        for stage in range(4):
            im = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
            crop_stage(im, stage)
            im.save(os.path.join(stage_dir, f"stage_{stage}.png"))
            print(f"  ok stage_{stage}.png -> {stage_dir}")

    if not args.no_preview:
        groups = {}
        for name, im in made:
            groups.setdefault(group_of(name), []).append((name, im))
        for g, lst in groups.items():
            build_preview(lst, os.path.join(PREVIEW_DIR, f"preview64_{g}.png"))
        print("审查图: preview64_*.png(按 skewer/crop/soup/dish/ingredient 分组)")


if __name__ == "__main__":
    main()
