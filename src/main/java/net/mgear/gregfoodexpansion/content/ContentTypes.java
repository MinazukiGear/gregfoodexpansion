package net.mgear.gregfoodexpansion.content;

import java.util.List;
import java.util.Map;

/**
 * 三张核心表 + 辅助表的行类型定义(content-pipeline.md §2)。
 *
 * <p>表以 JSON 存放于 {@code src/main/resources/content/}(content-pipeline.md §1,
 * 2026-09-12 定案):作物表 {@code crops/},组合表 {@code matrix/}(每模板一档),
 * 菜谱登记表 {@code registry/};辅助表 {@code flavors/}(B2 味型/中间品)、
 * {@code ingredients/}(加工基料)、{@code animals/}(动物源分割)、
 * {@code processes.json}(工艺总表,food-processing.md §3 的机器可读镜像)、
 * {@code samples/}(家常菜样本集)。内容增删改 = 表 PR + lint 通过。</p>
 *
 * <p>品位维度轴为细粒度轴(crops.md §3 维度表括号内子维度):辣素/精油/呈味/糖度/酸度/
 * 果胶/淀粉/麸质蛋白/含油率/脂肪酸谱/蛋白/咖啡因/茶多酚。菌菇类别为维度表收口时
 * 补充的扩展(原表六类未覆盖香菇等,见 content-pipeline.md §2.1 修订记录)。</p>
 */
public final class ContentTypes {
    private ContentTypes() {}

    public record BilingualName(String zh, String en) {}

    /** 真实性来源三级(cuisine-matrix.md §4):1=典籍/权威菜谱集 2=百科/常见菜谱可检索 3=地方/名店实证。 */
    public record SourceRef(Integer tier, String ref) {}

    public record EnvParams(String temperature, String humidity, String light) {}

    /** 工业归属 = 类别链模板 + 单品签名(crops.md §2 三票制第一票)。 */
    public record Industry(String chain, String signature) {}

    public record CropColors(String primary, String accent, String produce) {}

    public record CropEntry(String id, BilingualName name, String category, String template,
                            EnvParams env, Industry industry, List<String> grades,
                            CropColors colors, String batch, SourceRef source) {}

    /** B2 味型/中间品(signature-cuisine.md §3):kind ∈ compound(复合味型)/ intermediate(中间品)。 */
    public record FlavorEntry(String id, BilingualName name, String kind, List<String> grades,
                              String tier, String batch) {}

    /** 加工基料(food-processing.md §1 第四层):面团系/油脂/酵母菌种/高汤等。 */
    public record BaseIngredientEntry(String id, BilingualName name, List<String> grades,
                                      String tier, String note, String batch) {}

    /** 动物源产品:via = 产出的最早工艺(工序 id,null=源层直出)。 */
    public record AnimalProduct(String id, BilingualName name, List<String> grades,
                                String tier, String via) {}
    public record AnimalEntry(String id, BilingualName name, String source, List<AnimalProduct> products) {}

    /** 处理侧工艺钥匙(food-processing.md §3):tier = 最早可达档位。 */
    public record ProcessEntry(String id, BilingualName name, String tier, String carrier) {}

    /** 组合表行(cuisine-matrix.md §3)。品质轨 quality ∈ basic/refined/synthetic。 */
    public record MatrixRow(String id, BilingualName name, List<String> aliases, List<String> main,
                            List<String> aux, String flavor, List<String> craft, String quality,
                            String variant, String batch, SourceRef source) {}

    /** 菜谱登记表行(signature-cuisine.md §6)。level ∈ flagship/standard/intermediate;
     *  textureLevel ∈ hand-drawn/ai-refined/composite;gradeResponse ∈ none/scaled/exclusive。 */
    public record RegistryEntry(String id, BilingualName name, String cuisine, String level,
                                List<String> ingredients, List<String> craft, String exclusive,
                                String naturalGate, String gradeResponse, String gainTier,
                                SourceRef source, String textureLevel, String batch) {}

    /** 家常菜样本集行(cuisine-matrix.md §5):仅名称 + 配料依赖 + 工艺,用于可达率审计。 */
    public record SampleRow(BilingualName name, List<String> ingredients, List<String> craft) {}

    public record GainCeiling(String tier, String owner) {}
    public record LeverBand(Integer min, Integer max) {}
    public record Gates(Double sampleReachableRate, Double sampleTargetRate,
                        Integer ingredientRedundancyMin, LeverBand leverBand) {}
    public record Manifest(Integer schemaVersion, List<String> languages, List<String> tiers,
                           String defaultRegisteredCeiling, GainCeiling gainCeiling, Gates gates) {}

    public record MatrixTable(String file, List<MatrixRow> rows) {}
    public record RegistryTable(String file, List<RegistryEntry> rows) {}

    /** 类别 → 允许的品位维度轴(crops.md §3;fungus 为收口补充)。 */
    public static final Map<String, List<String>> CATEGORY_GRADES = Map.of(
            "cereal", List.of("starch", "gluten-protein"),
            "fruitveg", List.of("sugar", "acid", "pectin"),
            "spice", List.of("capsaicin", "essential-oil", "umami"),
            "oilseed", List.of("oil-yield", "fatty-acid"),
            "legume", List.of("protein"),
            "drink", List.of("caffeine", "tea-polyphenol"),
            "fungus", List.of("umami", "protein"));

    /** 作物骨架模板家族(crops.md §7 模板先行)。 */
    public static final List<String> CROP_TEMPLATES = List.of("leafy", "grain", "vine", "fungus", "aquatic");
}
