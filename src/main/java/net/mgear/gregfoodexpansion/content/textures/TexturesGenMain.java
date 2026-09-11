package net.mgear.gregfoodexpansion.content.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.mgear.gregfoodexpansion.content.ContentTables;
import net.mgear.gregfoodexpansion.content.ContentTables.FsSource;
import net.mgear.gregfoodexpansion.content.ContentTypes.CropEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.MatrixRow;

/**
 * 贴图管线 CLI(Gradle 任务 generateTextures,content-pipeline.md §4)。
 *
 * <p>两条管线:① 模板调色——作物生长期贴图 = 骨架模板 × 作物配色主题(crops.md §7);
 * ② 分层合成——菜肴贴图 = 基底(碗/盘…)× 内容物调色层,内容物颜色取主料首个
 * 作物配料的 produce 色。</p>
 *
 * <p>模板缺失时自动生成 16×16 占位骨架(叶菜/禾本/藤蔓/菌菇/水生 × 4 期 + 碗),
 * 供管线联调用;正式模板为手绘资产,放同目录覆盖即可(输出到 generated,精修图入 main)。</p>
 *
 * <p>参数:content 表根目录、模板目录、资产输出根(generated assets)。</p>
 */
public final class TexturesGenMain {
    private static final int SIZE = 16;
    private static final int STAGES = 4;
    private static final List<String> FAMILIES = List.of("leafy", "grain", "vine", "fungus", "aquatic");

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("用法: TexturesGenMain <contentDir> <templatesDir> <assetsOutDir>");
            System.exit(2);
        }
        ContentTables tables = ContentTables.load(new FsSource(java.nio.file.Path.of(args[0])));
        File templatesDir = new File(args[1]);
        File outRoot = new File(args[2], "gregfoodexpansion/textures");

        ensureTemplates(templatesDir);

        int crops = 0;
        for (CropEntry crop : tables.crops) {
            String family = crop.template() == null ? "leafy" : crop.template();
            for (int stage = 0; stage < STAGES; stage++) {
                BufferedImage mask = ImageIO.read(new File(templatesDir, family + "/stage_" + stage + ".png"));
                int tint = stageColor(crop, stage);
                File out = new File(outRoot, "item/crop/" + crop.id() + "_stage_" + stage + ".png");
                write(out, tint(mask, tint));
            }
            crops++;
        }

        int dishes = 0;
        BufferedImage bowl = ImageIO.read(new File(templatesDir, "bowl/bowl.png"));
        BufferedImage contentMask = ImageIO.read(new File(templatesDir, "bowl/content.png"));
        for (var table : tables.matrixTables) {
            for (MatrixRow row : table.rows()) {
                int contentTint = firstProduceColor(tables, row);
                BufferedImage composed = compose(bowl, contentMask, contentTint);
                write(new File(outRoot, "item/dish/" + row.id() + ".png"), composed);
                dishes++;
            }
        }
        System.out.printf("贴图管线完成: 作物调色 %d 种 × %d 期,菜肴合成 %d 张 → %s%n",
                crops, STAGES, dishes, outRoot.getPath());
    }

    // ---------------------------------------------------------------- 模板
    /** 模板缺失时生成占位骨架(灰度掩码:亮度=明暗,不透明度=覆盖)。 */
    private static void ensureTemplates(File templatesDir) throws IOException {
        boolean missing = FAMILIES.stream().anyMatch(f -> !new File(templatesDir, f + "/stage_0.png").isFile())
                || !new File(templatesDir, "bowl/bowl.png").isFile();
        if (!missing) {
            return;
        }
        System.out.println("模板缺失,生成 16×16 占位骨架(正式模板可同路径覆盖)…");
        for (String family : FAMILIES) {
            for (int stage = 0; stage < STAGES; stage++) {
                write(new File(templatesDir, family + "/stage_" + stage + ".png"), drawTemplate(family, stage));
            }
        }
        write(new File(templatesDir, "bowl/bowl.png"), drawBowl());
        write(new File(templatesDir, "bowl/content.png"), drawContent());
    }

    /** 画一族骨架的第 stage 期(程序化占位像素画)。 */
    private static BufferedImage drawTemplate(String family, int stage) {
        BufferedImage img = blank();
        int grow = stage + 1;
        switch (family) {
            case "leafy" -> {
                fillRect(img, 7, 15 - grow * 3, 2, grow * 3, 210);
                for (int i = 0; i < grow; i++) {
                    int y = 14 - i * 3;
                    fillRect(img, 4, y, 3, 2, 235 - i * 12);
                    fillRect(img, 9, y - 1, 3, 2, 245 - i * 12);
                }
            }
            case "grain" -> {
                for (int stalk = 0; stalk < 3; stalk++) {
                    int x = 3 + stalk * 5;
                    fillRect(img, x, 15 - grow * 2 - 2, 1, grow * 2 + 2, 220);
                    for (int g = 0; g < grow; g++) {
                        fillRect(img, x - 1, 13 - grow * 2 + g * 2, 3, 1, 250);
                    }
                }
            }
            case "vine" -> {
                for (int i = 0; i <= stage; i++) {
                    int y = 12 - (i % 2) * 3;
                    fillRect(img, 2 + i * 3, y, 3, 1, 200 + i * 10);
                }
                for (int i = 0; i < grow / 2 + 1; i++) {
                    fillEllipse(img, 4 + i * 4, 6 + (i % 2) * 2, 1, 1, 255);
                }
            }
            case "fungus" -> {
                fillRect(img, 7, 13 - grow, 2, grow + 2, 230);
                fillEllipse(img, 8, 12 - grow, 1 + grow, 1 + grow / 2, 250);
            }
            default -> { // aquatic
                fillRect(img, 7, 15 - grow * 3, 2, grow * 3, 205);
                for (int i = 0; i < grow; i++) {
                    fillRect(img, 5 + (i % 2) * 6, 13 - i * 3, 2, 1, 240);
                }
            }
        }
        return img;
    }

    private static BufferedImage drawBowl() {
        BufferedImage img = blank();
        fillRect(img, 3, 6, 10, 1, 255);          // 沿口
        for (int row = 0; row < 5; row++) {        // 碗身收腰
            int half = 5 - row / 2;
            fillRect(img, 8 - half, 7 + row, half * 2, 1, 235 - row * 18);
        }
        fillRect(img, 6, 12, 4, 2, 210);           // 碗足
        return img;
    }

    private static BufferedImage drawContent() {
        BufferedImage img = blank();
        fillEllipse(img, 8, 6, 5, 2, 245);
        fillEllipse(img, 7, 5, 2, 1, 255);         // 堆顶
        return img;
    }

    // ---------------------------------------------------------------- 合成
    /** 分层合成:内容物层(produce 色)在下,碗体层(亮灰)在上。 */
    private static BufferedImage compose(BufferedImage bowl, BufferedImage contentMask, int contentTint) {
        BufferedImage composed = blank();
        blit(composed, tint(contentMask, contentTint));
        blit(composed, tint(bowl, 0xE9E9E9));
        return composed;
    }

    private static int firstProduceColor(ContentTables tables, MatrixRow row) {
        List<String> refs = new ArrayList<>();
        if (row.main() != null) {
            refs.addAll(row.main());
        }
        if (row.aux() != null) {
            refs.addAll(row.aux());
        }
        for (String ref : refs) {
            if (ref.startsWith("crop:")) {
                String id = ref.substring(5);
                for (CropEntry crop : tables.crops) {
                    if (crop.id().equals(id) && crop.colors() != null && crop.colors().produce() != null) {
                        return parseHex(crop.colors().produce(), 0xC8C8C8);
                    }
                }
            }
        }
        return 0xC8C8C8;
    }

    /** 模板调色:掩码亮度 × 主题色。生长期主色由 primary 向 accent 渐近,末期混入 produce。 */
    private static BufferedImage tint(BufferedImage mask, int rgb) {
        BufferedImage out = blank();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int argb = mask.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                int shade = argb & 0xFF;
                out.setRGB(x, y, (0xFF << 24)
                        | (clamp((rgb >>> 16 & 0xFF) * shade / 255) << 16)
                        | (clamp((rgb >>> 8 & 0xFF) * shade / 255) << 8)
                        | clamp((rgb & 0xFF) * shade / 255));
            }
        }
        return out;
    }

    private static int stageColor(CropEntry crop, int stage) {
        int primary = parseHex(crop.colors() != null ? crop.colors().primary() : null, 0x4E9E48);
        int accent = parseHex(crop.colors() != null ? crop.colors().accent() : null, 0x35722F);
        int produce = parseHex(crop.colors() != null ? crop.colors().produce() : null, 0xE8E8E8);
        double t = stage / (double) (STAGES - 1);
        int base = lerp(primary, accent, t);
        return stage == STAGES - 1 ? lerp(base, produce, 0.45) : base;
    }

    // ---------------------------------------------------------------- 像素工具
    private static BufferedImage blank() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private static void blit(BufferedImage target, BufferedImage src) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int argb = src.getRGB(x, y);
                if ((argb >>> 24) != 0) {
                    target.setRGB(x, y, argb);
                }
            }
        }
    }

    private static void fillRect(BufferedImage img, int x, int y, int w, int h, int shade) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                put(img, x + dx, y + dy, shade);
            }
        }
    }

    private static void fillEllipse(BufferedImage img, int cx, int cy, int rx, int ry, int shade) {
        for (int y = cy - ry; y <= cy + ry; y++) {
            for (int x = cx - rx; x <= cx + rx; x++) {
                int dx = (x - cx) * (x - cx) * ry * ry;
                int dy = (y - cy) * (y - cy) * rx * rx;
                if (dx + dy <= rx * rx * ry * ry) {
                    put(img, x, y, shade);
                }
            }
        }
    }

    private static void put(BufferedImage img, int x, int y, int shade) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            img.setRGB(x, y, (0xFF << 24) | (shade << 16) | (shade << 8) | shade);
        }
    }

    private static int lerp(int a, int b, double t) {
        int r = (int) ((a >>> 16 & 0xFF) + ((b >>> 16 & 0xFF) - (a >>> 16 & 0xFF)) * t);
        int g = (int) ((a >>> 8 & 0xFF) + ((b >>> 8 & 0xFF) - (a >>> 8 & 0xFF)) * t);
        int bl = (int) ((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        return (clamp(r) << 16) | (clamp(g) << 8) | clamp(bl);
    }

    private static int parseHex(String hex, int fallback) {
        if (hex == null || !hex.matches("#?[0-9a-fA-F]{6}")) {
            return fallback;
        }
        return Integer.parseInt(hex.replace("#", ""), 16);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static void write(File file, BufferedImage img) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ImageIO.write(img, "png", file);
    }
}
