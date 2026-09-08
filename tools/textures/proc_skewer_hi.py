#!/usr/bin/env python
"""有机程序化烤串生成器(64x64 原生,metaball 肉块+法线光照+噪点)。

要点(踩过的坑):
- metaball 判定必须全程归一化坐标,d 与 r 同单位(曾犯 d 归一化/r 像素化导致全画布判内)。
- 半径随角度正弦扰动 -> 有机肉形;光照 = (fx-cx)+(fy-cy) 斜向明暗 + 边缘压暗。
- 熟肉:焦斑(60%)/油渍(40%)随机点;生肉:脂肪白纹点。
"""
import math
import random

from PIL import Image, ImageDraw


def proc_skewer(size, cooked, seed=7):
    im = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    p = im.load()
    rnd = random.Random(seed)
    n = size
    meat_base = (172, 78, 58) if not cooked else (128, 74, 36)
    fat = (238, 196, 186) if not cooked else (208, 150, 92)
    char_c = (58, 30, 14)
    centers = [(0.27, 0.70), (0.50, 0.48), (0.73, 0.26)]
    base_r = 0.155
    for y in range(n):
        for x in range(n):
            fx, fy = x / n, y / n
            t = max(0.0, min(1.0, (0.94 - fy) / 0.91))
            lx, ly = 0.06 + 0.88 * t, 0.94 - 0.91 * t
            ds = math.hypot(fx - lx, fy - ly)
            inside, best = False, None
            for i, (cx, cy) in enumerate(centers):
                d = math.hypot(fx - cx, fy - cy)
                ang = math.atan2(fy - cy, fx - cx)
                w = 1.0 + 0.12 * math.sin(ang * 3 + i) + 0.06 * math.sin(ang * 7 + i * 2)
                r = base_r * w
                if d < r:
                    inside = True
                    best = (i, d / r, cx, cy, r)
                    break
            if inside:
                i, k, cx, cy, r = best
                light = (fx - cx) + (fy - cy)
                shade = -light / r * 34
                edge = max(0.0, (k - 0.72) / 0.28)
                shade -= edge * 40
                v = rnd.randint(-9, 9)
                col = tuple(max(0, min(255, int(c + shade + v))) for c in meat_base)
                if rnd.random() < (0.10 if cooked else 0.08):
                    col = char_c if (cooked and rnd.random() < 0.6) else fat
                p[x, y] = col + (255,)
            elif ds < 0.020:
                depth = ds / 0.020
                wood = (int(206 - 90 * depth), int(166 - 76 * depth), int(108 - 54 * depth))
                p[x, y] = wood + (255,)
            elif ds < 0.024:
                p[x, y] = (96, 70, 40, 255)
    d = ImageDraw.Draw(im)
    d.line([(int(0.92 * n), int(0.05 * n)), (int(0.97 * n), int(0.01 * n))],
           fill=(120, 88, 48), width=max(1, n // 32))
    return im


if __name__ == "__main__":
    import os
    os.makedirs("proc_hi", exist_ok=True)
    proc_skewer(64, False).save("proc_hi/raw_lamb_64.png")
    proc_skewer(64, True).save("proc_hi/campfire_lamb_64.png")
    print("saved proc_hi/")
