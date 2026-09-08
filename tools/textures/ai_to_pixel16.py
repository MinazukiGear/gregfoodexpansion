#!/usr/bin/env python
"""AI 生图 -> 16/32x32 Minecraft 物品贴图管线。

用法:把 ImageGen 产出的 1024x1024 原图路径与目标名写进 JOBS,运行本脚本。
     SIZE 环境变量可切换输出分辨率(默认 32)。
前提:原图背景为纯白或纯洋红(生成时 prompt 指定 #FF00FF 底最稳);
     透明背景常被模型画成棋盘格,不可信,一律按白底处理。

管线:key_bg_flood 边界泛洪抠底(保护内部高光/盐粒)-> 方形补齐 ->
     预乘 alpha BOX 降采样(消暗边)-> MEDIANCUT 量化无抖动 ->
     孤点清理。输出到 ai_px/<name>.png。
"""
import os
import sys
from collections import deque

from PIL import Image, ImageChops, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
OUT_DIR = os.path.join(HERE, "ai_px")
SIZE = int(os.environ.get("SIZE", "32"))

# (源图路径, 目标贴图名)
JOBS = [
]


def key_bg_flood(im):
    """边界泛洪:从四边清除连通的近白/近洋红背景,不碰内部高光。"""
    im = im.resize((256, 256), Image.LANCZOS)
    p = im.load()

    def is_bg(c):
        r, g, b = c[0], c[1], c[2]
        white = r > 225 and g > 225 and b > 225
        mag = r > 140 and b > 140 and g < 150 and (r + b) - 2 * g > 100
        return white or mag

    seen = [[False] * 256 for _ in range(256)]
    dq = deque()
    for i in range(256):
        for x, y in ((i, 0), (i, 255), (0, i), (255, i)):
            if not seen[y][x] and is_bg(p[x, y]):
                seen[y][x] = True
                dq.append((x, y))
    while dq:
        x, y = dq.popleft()
        p[x, y] = (0, 0, 0, 0)
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            xx, yy = x + dx, y + dy
            if 0 <= xx < 256 and 0 <= yy < 256 and not seen[yy][xx] and is_bg(p[xx, yy]):
                seen[yy][xx] = True
                dq.append((xx, yy))
    return im


def denoise(px16):
    """孤点/悬链清理:反复移除 8 邻域不透明数 <=1 的像素。"""
    w, h = px16.size
    p = px16.load()
    changed = True
    while changed:
        changed = False
        kill = []
        for y in range(h):
            for x in range(w):
                if p[x, y][3] == 0:
                    continue
                n = sum(1 for dy in (-1, 0, 1) for dx in (-1, 0, 1)
                        if (dx or dy) and 0 <= x + dx < w and 0 <= y + dy < h
                        and p[x + dx, y + dy][3] > 0)
                if n <= 1:
                    kill.append((x, y))
        if kill:
            changed = True
            for x, y in kill:
                p[x, y] = (0, 0, 0, 0)
    return px16


def to16(src, size=SIZE):
    im = Image.open(src).convert("RGBA")
    im = key_bg_flood(im)
    im = im.crop(im.getbbox())
    s = max(im.size)
    sq = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    sq.paste(im, ((s - im.width) // 2, (s - im.height) // 2))
    r, g, b, a = sq.split()
    pm = Image.merge("RGB", tuple(ImageChops.multiply(ch, a) for ch in (r, g, b)))
    pm_s = pm.resize((size, size), Image.BOX)
    a_s = a.resize((size, size), Image.BOX)
    pp, pa = pm_s.load(), a_s.load()
    rgb_flat = Image.new("RGB", (size, size))
    alpha = Image.new("L", (size, size), 0)
    prf, paa = rgb_flat.load(), alpha.load()
    for y in range(size):
        for x in range(size):
            A = pa[x, y]
            if A > 60:
                rr, gg, bb = pp[x, y]
                prf[x, y] = (min(255, rr * 255 // A), min(255, gg * 255 // A),
                             min(255, bb * 255 // A))
                paa[x, y] = 255
    colors = 24 if size >= 32 else 16
    q = rgb_flat.quantize(colors=colors, method=Image.MEDIANCUT, kmeans=8).convert("RGB")
    out = q.convert("RGBA")
    out.putalpha(alpha)
    return denoise(out)


def preview(results, size=SIZE):
    cell, gut, label = size * 8, 14, 22
    w = len(results) * (cell + gut) + gut
    h = cell + label + 2 * gut
    sheet = Image.new("RGBA", (w, h), (26, 26, 30, 255))
    dr = ImageDraw.Draw(sheet)
    for i, name in enumerate(results):
        x = gut + i * (cell + gut)
        px = Image.open(os.path.join(OUT_DIR, name + ".png"))
        px = px.resize((cell, cell), Image.NEAREST)
        sheet.paste(px, (x, gut), px)
        dr.text((x + 2, gut + cell + 4), name, fill=(215, 215, 215, 255))
    sheet.save(os.path.join(HERE, f"preview_ai_{size}.png"))
    print("preview:", f"preview_ai_{size}.png")


def main():
    os.makedirs(OUT_DIR, exist_ok=True)
    done = []
    for src, name in JOBS:
        px = to16(src)
        px.save(os.path.join(OUT_DIR, name + ".png"))
        done.append(name)
        print(f"{SIZE}x{SIZE}:", name)
    if done:
        preview(done)


if __name__ == "__main__":
    main()
