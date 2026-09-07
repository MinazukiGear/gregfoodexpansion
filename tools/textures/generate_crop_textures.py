#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""生成 M1 首批 13 种作物的贴图(crop-system-foundation.md §5 资产规范)。

输出:
  src/main/resources/assets/gregfoodexpansion/textures/block/crop/stage_{0..3}.png
      4 张共享灰度生长模板(8 个 age 映射 0,1,1,2,2,3,3,3),运行时经 BlockColors 按作物染色;
  src/main/resources/assets/gregfoodexpansion/textures/item/<crop>.png
      产物物品贴图(咖啡为 coffee_cherries);
  src/main/resources/assets/gregfoodexpansion/textures/item/<crop>_seeds.png
      种子物品贴图;
  tools/textures/preview.png
      8 倍放大审阅图:灰度模板 / 每作物种子+产物 / 每作物 stage_3 染色预览。

种子贴图由 5 个共享形状模板 + 每作物配色参数化生成;产物与生长模板为手工像素画。
TINTS 表是运行时染色的基准色,第 2 步代码中 BlockColors 注册须与此保持一致。

用法: python tools/textures/generate_crop_textures.py
"""

from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
TEX = ROOT / "src/main/resources/assets/gregfoodexpansion/textures"

GRAY = {"W": "#FFFFFF", "S": "#C8C8C8", "D": "#808080", "K": "#404040"}

# 运行时染色基准色( foliage multiply ),step 2 BlockColors 注册以此为准。
TINTS = {
    "soybean": "#96BE64", "corn": "#7FA84F", "rice": "#A8B95C", "barley": "#BCAE64",
    "peanut": "#7FA354", "tomato": "#74A854", "onion": "#74A08C", "chili": "#64A054",
    "cabbage": "#90C06E", "grape": "#74A05C", "coffee": "#55783E", "tea": "#44703C",
    "hops": "#90C46A",
}

# ---- 灰度生长模板(共享) ------------------------------------------------

STAGES = [
    [
        "................", "................", "................", "................",
        "................", "................", "................", "................",
        "................", "................", "......W.........", ".....WWWW.......",
        "......WW........", ".....W.W........", ".......W........", ".......S........",
    ],
    [
        "................", "................", "................", "................",
        "................", "................", "................", ".......W........",
        "......WSW.......", ".....W.WSW......", ".....WSW.W......", "......SWW.......",
        ".....W.SW.......", "......WSW.......", ".......SW.......", ".......S........",
    ],
    [
        "................", "................", "................", "................",
        ".......WW.......", "......WWWW......", ".....WW.WWW.....", "....WW.WWWW.....",
        ".....WW.WWW.....", "....WWSWWWW.....", ".....WW.WWW.....", "......WSWW......",
        ".....WW.WW......", "......WSWW......", ".......SW.......", ".......S........",
    ],
    [
        "................", "....W.W.W.W.....", "...WSWSWSWSW....", "....WWWWWWW.....",
        "...WW.WWW.WW....", "....WWWWW.WW....", "...WWW.WWWW.W...", "....WWWWWWW.W...",
        "...WW.WWWWWW....", "....WWWWW.WW....", "...WW.WWWWW.W...", "....WWWWWWW.....",
        ".....WW.WW......", "....WWWWWWW.....", "......WWW.......", ".......S........",
    ],
]

# ---- 种子形状模板(共享,参数化配色) ------------------------------------

SEED_SHAPES = {
    "trio_oval": [
        "................", "................", "................", "................",
        "......KKK.......", ".....KMLLK......", ".....KMLLK......", "......KDK.......",
        "...KKK...KKK....", "..KMLLK.KMLLK...", "..KMLLK.KMLLK...", "...KDK...KDK....",
        "................", "................", "................", "................",
    ],
    "grains": [
        "................", "................", "................", ".........KKK....",
        "........KMLK....", ".......KMLK.....", "......KDK.......", "..KKK...........",
        ".KMLK...........", "KMLK............", "KDK.............", "................",
        "................", "................", "................", "................",
    ],
    "dots": [
        "................", "................", "................", "................",
        "....KK...KKK....", "...KMLK.KMLLK...", "....KK...KDK....", "................",
        "..KKK..KKK......", ".KMLLK.KMLK.....", "..KDK..KDK......", "................",
        "......KKK.......", ".....KMLK.......", "......KK........", "................",
    ],
    "bean_pair": [
        "................", "................", "................", "................",
        "....KKKK..KKK...", "...KMLLLKKMLLK..", "...KMLDMLKMLDLK.", "...KMLDMLKMLDLK.",
        "....KDDDK.KDK...", "................", "................", "................",
        "................", "................", "................", "................",
    ],
    "single_kernel": [
        "................", "................", "................", "................",
        "......KKKK......", ".....KLLLLK.....", "....KLLWLLK.....", "....KLLLMLK.....",
        "....KMMMMDK.....", "....KMMMMDK.....", ".....KMMDK......", ".....KKKKK......",
        "................", "................", "................", "................",
    ],
}

# ---- 作物表:种子形状/配色、产物像素画、运行时染色 -------------------------

CROPS = [
    {
        "id": "soybean",
        "seed_shape": "trio_oval",
        "seed_palette": {"K": "#6E5B26", "M": "#D9C877", "L": "#F0E6A8", "D": "#B5A050"},
        "palette": {"K": "#2F4A1E", "G": "#6E9B4C", "E": "#8FBF68", "D": "#4E7A33",
                    "B": "#D9C877", "N": "#B5A050"},
        "product": [
            "................", "................", "................", "..........KK....",
            ".........KGK....", "........KGEEK...", ".......KGEBEK...", "......KGEBEGK...",
            ".....KGEBEGK....", ".....KGEBGK.....", "....KGEGGGK.....", "....KGEGGK......",
            "...KKGGGKK......", "...KDKK.........", "................", "................",
        ],
    },
    {
        "id": "corn",
        "seed_shape": "single_kernel",
        "seed_palette": {"K": "#7A5A10", "M": "#F2CB4B", "L": "#FBE9A0", "W": "#FFF6D0",
                         "D": "#D9A92E"},
        "palette": {"K": "#4A3A10", "Y": "#F2CB4B", "O": "#D9A92E", "W": "#FBE9A0",
                    "G": "#7FA84F", "H": "#5C8536"},
        "product": [
            "................", "...........KKK..", "..........KYYK..", ".........KYWYK..",
            "........KYWYOYK.", ".......KYWYOYK..", "......KYWYOYOK..", ".....KYWYOYOYK..",
            "....KGKYOYOYOK..", "...KGGKKYOYOK...", "...KGHGGKKYKK...", "..KGGHGGKKKK....",
            "..KGHHGGGK......", "...KKGGGK.......", ".....KKK........", "................",
        ],
    },
    {
        "id": "rice",
        "seed_shape": "grains",
        "seed_palette": {"K": "#6B5A2A", "M": "#E8D59A", "L": "#F5EBC2", "D": "#CBB678"},
        "palette": {"K": "#6B5A2A", "T": "#E8D59A", "U": "#CBB678", "V": "#F5EBC2",
                    "G": "#A8B95C", "H": "#8A9B4C"},
        "product": [
            "................", "........KKKK....", ".......KTVTVK...", ".......KTUTUK...",
            "........KTK.....", ".......KGK......", "......KGK.......", "......KGK.......",
            ".....KGK........", ".....KGKKTK.....", ".....KGKTUTK....", "......KGKKK.....",
            "......KGK.......", ".......KGK......", ".......KK.......", "................",
        ],
    },
    {
        "id": "barley",
        "seed_shape": "grains",
        "seed_palette": {"K": "#6E5B26", "M": "#DEC06A", "L": "#EFE3B0", "D": "#C2A34B"},
        "palette": {"K": "#6E5B26", "M": "#DEC06A", "N": "#C2A34B", "L": "#EFE3B0",
                    "G": "#93A457"},
        "product": [
            "...L..L..L..L...", "....L.L.L.L.....", "....KLKLKLK.....", "....KMNKMNK.....",
            "....KMNKMNK.....", "...LKMLKMLK.....", "....KMNKMNK.....", "....KMLKMLK.....",
            "....KMNKMNK.....", "...LKMLKMLK.....", "....KMNKMNK.....", "....KKMNKK......",
            "......KGK.......", "......KGK.......", "......KGK.......", ".......K........",
        ],
    },
    {
        "id": "peanut",
        "seed_shape": "trio_oval",
        "seed_palette": {"K": "#6E3A26", "M": "#C9714B", "L": "#E09B72", "D": "#A65438"},
        "palette": {"K": "#6E5326", "M": "#D9B67F", "D": "#BE9758", "L": "#EAD2A8"},
        "product": [
            "................", "................", "................", "................",
            "....KKKKK.KKKK..", "...KMLLLMKKMLLK.", "..KMLMLLMLKDMDK.", "..KMLMLLMLKDMDK.",
            "..KMMLLMLKDMDMK.", "...KMLLLMKKMLKK.", "....KKKKK.KKKK..", "................",
            "................", "................", "................", "................",
        ],
    },
    {
        "id": "tomato",
        "seed_shape": "dots",
        "seed_palette": {"K": "#7A5A2E", "M": "#C9B48A", "L": "#E5D9B8", "D": "#A89058"},
        "palette": {"K": "#6E1F16", "R": "#E04B3A", "D": "#C23325", "W": "#F2836F",
                    "V": "#F5B0A0", "S": "#5C8536"},
        "product": [
            "................", "................", ".......SK.......", "......SKSK......",
            ".......KK.......", "....KKKRSKKK....", "...KRRRRRRRK....", "..KRVRWRRRRDK...",
            "..KRVRWRRRRDK...", "..KRRRRRRRDDK...", "..KRRRRRRRDDK...", "...KRRRRRDDK....",
            "....KKRRDKK.....", "......KKK.......", "................", "................",
        ],
    },
    {
        "id": "onion",
        "seed_shape": "dots",
        "seed_palette": {"K": "#2E2A20", "M": "#4A4234", "L": "#5C5240", "D": "#3A342A"},
        "palette": {"K": "#5C3A6E", "M": "#A87BB8", "D": "#8A5E9E", "L": "#C9A0D6",
                    "S": "#7FA84F", "R": "#D9CBB0"},
        "product": [
            "................", ".......SS.......", "......SK........", ".......KSK......",
            "......KKMKK.....", ".....KMLLMK.....", "....KMLMLLKK....", "...KMLMLMLLLK...",
            "...KMLMDMLLLK...", "...KMLMDMLLDK...", "....KMLMLLDK....", ".....KMLLDK.....",
            "......KMDK......", ".......KK.......", "......R.R.R.....", "................",
        ],
    },
    {
        "id": "chili",
        "seed_shape": "dots",
        "seed_palette": {"K": "#8A7A2E", "M": "#E8D9A0", "L": "#F5EEC8", "D": "#C2B070"},
        "palette": {"K": "#7A1810", "M": "#D93A2B", "D": "#B02417", "W": "#F06A55",
                    "G": "#5C8536"},
        "product": [
            "................", "................", "....KK..........", "...KGGK.........",
            "....KGKK........", "...KKMMKK.......", "..KMMWMMMK......", "..KMWMMMMMK.....",
            "..KMWMMMMMDK....", "...KMMMMMMDK....", "...KMMMMMMDK....", "....KMMMMMDK....",
            ".....KMMMDK.....", "......KMMDK.....", ".......KKDK.....", "........KK......",
        ],
    },
    {
        "id": "cabbage",
        "seed_shape": "dots",
        "seed_palette": {"K": "#5E4A26", "M": "#8A6E3E", "L": "#A89058", "D": "#6E5630"},
        "palette": {"K": "#3E5C26", "M": "#A8C97A", "D": "#7FA84F", "E": "#5C8536",
                    "L": "#C9E09A"},
        "product": [
            "................", "................", "................", "......KKKK......",
            "....KKMLLMKK....", "...KMLLMMMMKK...", "..KMLMMMMMDMK...", "..KMLMMDMMMDDK..",
            ".KMLMMMMMDMMDDK.", ".KMDMMMMDMMMDDK.", ".KEMMMMMMMDMDK..", "..KEMMMMDMMEEK..",
            "...KEEMMEEEEK...", ".....KKKKK......", "................", "................",
        ],
    },
    {
        "id": "grape",
        "seed_shape": "dots",
        "seed_palette": {"K": "#5E4A26", "M": "#B59B6E", "L": "#D0BC94", "D": "#947E52"},
        "palette": {"K": "#4A2060", "M": "#8A4FA8", "D": "#6E3A8A", "L": "#B07BC4",
                    "G": "#5C8536", "T": "#6E5B26"},
        "product": [
            "................", ".........KGK....", "....KK..KGGGK...", "....KTK.KGGK....",
            "...KMLMKKGK.....", "..KMLMLMKK......", "..KMLMLMLMK.....", "...KMLMLMLMK....",
            "..KMDMLMLMLMK...", "..KMDMDMLMLMK...", "...KMDMDMLMK....", "....KMDMDMK.....",
            ".....KMDMK......", "......KMK.......", ".......K........", "................",
        ],
    },
    {
        "id": "coffee",
        "item_id": "coffee_cherries",
        "seed_shape": "bean_pair",
        "seed_palette": {"K": "#4A5228", "M": "#A8B078", "L": "#C2C89A", "D": "#8A9258"},
        "palette": {"K": "#7A1F18", "M": "#D9453A", "D": "#B03028", "L": "#F07A6A",
                    "G": "#3E6E2E", "T": "#6E4B26"},
        "product": [
            "................", "................", "......KGGKK.....", ".....KGGGGK.....",
            "....KKKGGKK.....", "...KMLMKKTK.....", "..KMLLMMKTK.....", "..KMLLMMKKMK....",
            "..KMMMDMKMLMK...", "...KMMMKKMLLMK..", "....KKK.KMLMMK..", "......KMDMMDMK..",
            "......KMMMMMK...", ".......KKMK.....", "........KK......", "................",
        ],
    },
    {
        "id": "tea",
        "seed_shape": "dots",
        "seed_palette": {"K": "#4A3A16", "M": "#6E5B26", "L": "#8A7440", "D": "#55461E"},
        "palette": {"K": "#1E3E1E", "M": "#3E7A3E", "D": "#2E5E2E", "L": "#5C9B55",
                    "T": "#5C4B26"},
        "product": [
            "................", "................", ".......KK.......", "......KMLK......",
            "......KMLLK.....", ".......KMLK.....", "..KK...KTK...KK.", ".KMLK..KTK..KMLK",
            ".KMLLK.KTK.KMLLK", "..KMLKKKTKKKMLK.", "...KK..KTK...KK.", ".......KTK......",
            "........KK......", "................", "................", "................",
        ],
    },
    {
        "id": "hops",
        "seed_shape": "dots",
        "seed_palette": {"K": "#6E6428", "M": "#D9C88A", "L": "#EBDCA8", "D": "#B5A45E"},
        "palette": {"K": "#4E6E2A", "M": "#A8C96A", "D": "#8AAB4C", "E": "#6E8F3A",
                    "L": "#C9E08A"},
        "product": [
            "................", ".......KK.......", "......KDK.......", ".....KKMKK......",
            "....KMLLLMK.....", "...KMLMMLLDK....", "...KMDMLMLDK....", "....KMLMLLDK....",
            "....KMDMLMDK....", ".....KMLMLDK....", ".....KMDMLDK....", "......KMLMDK....",
            "......KMMDK.....", ".......KMDK.....", ".......KKK......", "................",
        ],
    },
]

# ---- 渲染与输出 ----------------------------------------------------------


def hex_rgba(color: str) -> tuple:
    h = color.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


def grid_to_image(rows: list, palette: dict) -> Image.Image:
    assert len(rows) == 16, f"grid must have 16 rows, got {len(rows)}"
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        assert len(row) == 16, f"row {y} must have 16 chars, got {len(row)}: {row!r}"
        for x, ch in enumerate(row):
            if ch != ".":
                assert ch in palette, f"char {ch!r} missing from palette (row {y})"
                px[x, y] = hex_rgba(palette[ch])
    return img


def tinted(img: Image.Image, color: str) -> Image.Image:
    r, g, b, _ = hex_rgba(color)
    out = img.copy()
    px = out.load()
    for y in range(16):
        for x in range(16):
            pr, pg, pb, pa = px[x, y]
            if pa:
                px[x, y] = (pr * r // 255, pg * g // 255, pb * b // 255, pa)
    return out


def main() -> None:
    block_dir = TEX / "block" / "crop"
    item_dir = TEX / "item"
    block_dir.mkdir(parents=True, exist_ok=True)
    item_dir.mkdir(parents=True, exist_ok=True)

    stage_imgs = [grid_to_image(rows, GRAY) for rows in STAGES]
    for i, img in enumerate(stage_imgs):
        img.save(block_dir / f"stage_{i}.png")

    for crop in CROPS:
        item_id = crop.get("item_id", crop["id"])
        seed_img = grid_to_image(SEED_SHAPES[crop["seed_shape"]], crop["seed_palette"])
        seed_img.save(item_dir / f"{crop['id']}_seeds.png")
        product_img = grid_to_image(crop["product"], crop["palette"])
        product_img.save(item_dir / f"{item_id}.png")

    # ---- 审阅预览图(8× 最近邻放大,棋盘格底,含每作物染色预览) ----
    scale, cell, gutter, label_h = 8, 128, 8, 14
    cols = len(CROPS)
    rows_count = 4  # 灰度模板行 / 种子行 / 产物行 / 染色预览行
    width = max(4, cols) * (cell + gutter) + gutter
    height = rows_count * (cell + label_h + gutter) + gutter
    sheet = Image.new("RGBA", (width, height), (30, 30, 30, 255))
    draw = ImageDraw.Draw(sheet)
    for y in range(0, height, 8):
        for x in range(0, width, 8):
            if (x // 8 + y // 8) % 2 == 0:
                draw.rectangle([x, y, x + 7, y + 7], fill=(58, 58, 58, 255))

    def paste(img: Image.Image, col: int, row: int) -> None:
        x = gutter + col * (cell + gutter)
        y = gutter + row * (cell + label_h + gutter)
        sheet.paste(img.resize((cell, cell), Image.NEAREST), (x, y))

    for i, img in enumerate(stage_imgs):
        paste(img, i, 0)
    for col, crop in enumerate(CROPS):
        paste(Image.open(item_dir / f"{crop['id']}_seeds.png"), col, 1)
        paste(Image.open(item_dir / f"{crop.get('item_id', crop['id'])}.png"), col, 2)
        paste(tinted(stage_imgs[3], TINTS[crop["id"]]), col, 3)
        draw.text((gutter + col * (cell + gutter), gutter + 3 * (cell + label_h + gutter) + cell + 2),
                  crop["id"], fill=(220, 220, 220, 255))
    draw.text((gutter, gutter), "stages 0-3 (grayscale, tinted at runtime)", fill=(150, 200, 150, 255))
    draw.text((gutter, gutter + (cell + label_h + gutter)), "seeds", fill=(200, 200, 200, 255))
    draw.text((gutter, gutter + 2 * (cell + label_h + gutter)), "products", fill=(200, 200, 200, 255))
    draw.text((gutter, gutter + 3 * (cell + label_h + gutter)), "stage_3 tinted", fill=(150, 200, 150, 255))

    preview = ROOT / "tools/textures/preview.png"
    preview.parent.mkdir(parents=True, exist_ok=True)
    sheet.save(preview)
    print(f"written {4 + 2 * len(CROPS)} textures to {TEX}")
    print(f"preview: {preview}")


if __name__ == "__main__":
    main()
