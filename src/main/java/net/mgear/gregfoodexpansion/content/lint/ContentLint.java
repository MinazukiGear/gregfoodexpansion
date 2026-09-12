package net.mgear.gregfoodexpansion.content.lint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.mgear.gregfoodexpansion.content.ContentTables;
import net.mgear.gregfoodexpansion.content.ContentTypes;
import net.mgear.gregfoodexpansion.content.ContentTypes.AnimalEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.BaseIngredientEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.CropEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.FlavorEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.MatrixRow;
import net.mgear.gregfoodexpansion.content.ContentTypes.MatrixTable;
import net.mgear.gregfoodexpansion.content.ContentTypes.RegistryEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.RegistryTable;
import net.mgear.gregfoodexpansion.content.ContentTypes.SampleRow;

/**
 * 内容 lint 全清单(content-pipeline.md §5,2026-09-12 首版实装)。
 *
 * <p>严重级映射(按规则原文动词):1/2/3/4/8/9 = 错误(构建失败);
 * 5/6/7/10 = 警告或信息(报缺口/报警,不阻断,用于批次缺口报告)。</p>
 *
 * <p>第 4 条解读:品位维度轴取 crops.md §3 维度表括号内的细粒度子轴
 * (辣素/精油/呈味/糖度/…),"维度完全相同"指两配料的细粒度轴集合相等。</p>
 */
public final class ContentLint {
    private final ContentTables t;
    /** locale → 语言键集合(从 generated/main 语言文件合并读取;可为空映射)。 */
    private final Map<String, Set<String>> langKeys;
    private final List<LintIssue> issues = new ArrayList<>();

    /** 配料解析结果:显示名 + 品位维度轴 + 最早可达档位。 */
    public record Resolved(String ref, String displayName, List<String> grades, String tier) {}

    private record Resolution(Resolved ing, String error) {
        boolean ok() {
            return ing != null;
        }
    }

    public ContentLint(ContentTables tables, Map<String, Set<String>> langKeys) {
        this.t = tables;
        this.langKeys = langKeys;
    }

    public List<LintIssue> run() {
        try {
            new net.mgear.gregfoodexpansion.content.GameplayAudit(t).errors().forEach(message -> err(3, "gameplay", message));
        } catch (IllegalArgumentException | NullPointerException e) {
            err(3, "gameplay", "Invalid gameplay schema/references: " + e.getMessage());
        }
        rule1ThreeVotes();
        rule2Source();
        rule3Universe();
        rule4GradeDedup();
        rule5TierCoverage();
        rule6SampleReachability();
        rule7Redundancy();
        rule8GainCeiling();
        rule9LangParity();
        rule10AliasMerge();
        return issues;
    }

    // ---------------------------------------------------------------- 规则 1
    /** 三票制全检(crops.md §2):工业归属 / 环境参数 / 品位维度,外加真实性来源与模板。 */
    private void rule1ThreeVotes() {
        Set<String> seen = new HashSet<>();
        for (CropEntry c : t.crops) {
            String at = "crops#" + c.id();
            if (c.id() == null || !c.id().matches("[a-z0-9_-]+")) {
                err(1, at, "作物 id 缺失或非法(需 [a-z0-9_-])");
            } else if (!seen.add(c.id())) {
                err(1, at, "作物 id 重复");
            }
            if (c.industry() == null || blank(c.industry().chain())) {
                err(1, at, "无工业归属(类别链模板为空)——三票制第一票不过,拒绝注册");
            }
            if (c.env() == null || (blank(c.env().temperature()) && blank(c.env().humidity())
                    && blank(c.env().light()))) {
                err(1, at, "环境参数(温·湿·光)全空——三票制第二票不过,拒绝注册");
            }
            if (c.grades() == null || c.grades().isEmpty()) {
                err(1, at, "未绑定品位维度——三票制第三票不过,拒绝注册");
            } else {
                List<String> allowed = ContentTypes.CATEGORY_GRADES.get(c.category());
                if (allowed == null) {
                    err(1, at, "未知作物类别: " + c.category());
                } else {
                    for (String g : c.grades()) {
                        if (!allowed.contains(g)) {
                            err(1, at, "品位维度 '%s' 不属于类别 '%s' 的维度集 %s"
                                    .formatted(g, c.category(), allowed));
                        }
                    }
                }
            }
            if (c.template() == null || !ContentTypes.CROP_TEMPLATES.contains(c.template())) {
                err(1, at, "骨架模板缺失或未知(允许: %s)".formatted(ContentTypes.CROP_TEMPLATES));
            }
            if (c.source() == null || c.source().tier() == null || blank(c.source().ref())) {
                err(1, at, "真实性来源缺失(候选池只收有农业史的真实植物)");
            }
            if (blank(c.batch())) {
                err(1, at, "批次字段缺失");
            }
        }
    }

    // ---------------------------------------------------------------- 规则 2
    /** 菜肴无来源字段/来源等级不足 → 构建失败;B1 旗舰强制 ①②(cuisine-matrix.md §4)。 */
    private void rule2Source() {
        for (MatrixTable table : t.matrixTables) {
            for (MatrixRow row : table.rows()) {
                checkSource(2, "matrix/" + table.file() + "#" + row.id(), row.source(), false);
            }
        }
        for (RegistryTable table : t.registryTables) {
            for (RegistryEntry row : table.rows()) {
                checkSource(2, "registry/" + table.file() + "#" + row.id(), row.source(),
                        "flagship".equals(row.level()));
            }
        }
    }

    private void checkSource(int rule, String at, ContentTypes.SourceRef source, boolean flagship) {
        if (source == null) {
            err(rule, at, "无来源字段 = 构建失败");
            return;
        }
        Integer tier = source.tier();
        if (tier == null || tier < 1 || tier > 3) {
            err(rule, at, "来源等级非法(需 1=典籍/2=百科/3=地方实证): " + tier);
            return;
        }
        if (flagship && tier > 2) {
            err(rule, at, "旗舰级强制来源 ①②,当前为 %d 级".formatted(tier));
        }
        if (blank(source.ref())) {
            err(rule, at, "来源引用(谱源)为空");
        }
    }

    // ---------------------------------------------------------------- 规则 3
    /** 配料不在宇宙内 / 工艺不在工艺总表 / 味型未登记 → 拒绝生成。 */
    private void rule3Universe() {
        Set<String> craftIds = new HashSet<>();
        t.processes.forEach(p -> craftIds.add(p.id()));
        for (MatrixTable table : t.matrixTables) {
            for (MatrixRow row : table.rows()) {
                String at = "matrix/" + table.file() + "#" + row.id();
                for (String ref : refs(row)) {
                    Resolution res = resolve(ref);
                    if (!res.ok()) {
                        err(3, at, "配料 '%s' 不在宇宙内(%s)——拒绝生成".formatted(ref, res.error()));
                    }
                }
                if (row.flavor() != null) {
                    Resolution res = resolve(row.flavor());
                    if (!res.ok()) {
                        err(3, at, "味型 '%s' 未在 B2 味型表登记(%s)".formatted(row.flavor(), res.error()));
                    }
                }
                checkCraft(3, at, row.craft(), craftIds);
            }
        }
        for (RegistryTable table : t.registryTables) {
            for (RegistryEntry row : table.rows()) {
                String at = "registry/" + table.file() + "#" + row.id();
                for (String ref : nullList(row.ingredients())) {
                    Resolution res = resolve(ref);
                    if (!res.ok()) {
                        err(3, at, "配料 '%s' 不在宇宙内(%s)——拒绝生成".formatted(ref, res.error()));
                    }
                }
                checkCraft(3, at, row.craft(), craftIds);
            }
        }
        // 入宇宙即有归宿(food-processing.md §2 原则 4):配料至少出现在一张组合表或登记行 —— 缺口报告级。
        Set<String> used = new HashSet<>();
        t.matrixTables.forEach(tb -> tb.rows().forEach(r -> used.addAll(refs(r))));
        t.registryTables.forEach(tb -> tb.rows().forEach(r -> used.addAll(nullList(r.ingredients()))));
        if (t.gameplay != null && t.gameplay.crafting() != null) {
            t.gameplay.crafting().forEach(recipe -> used.addAll(nullList(recipe.inputs())));
        }
        Set<String> universe = new LinkedHashSet<>();
        t.crops.forEach(c -> universe.add("crop:" + c.id()));
        t.flavors.forEach(f -> universe.add("b2:" + f.id()));
        t.baseIngredients.forEach(b -> universe.add("base:" + b.id()));
        t.animals.forEach(a -> a.products().forEach(p -> universe.add("animal:" + p.id())));
        for (String ing : universe) {
            if (!used.contains(ing)) {
                warn(3, "universe", "配料 '%s' 未出现在任何组合表/登记行(入宇宙即有归宿)".formatted(ing));
            }
        }
    }

    private void checkCraft(int rule, String at, List<String> craft, Set<String> known) {
        if (craft == null || craft.isEmpty()) {
            warn(rule, at, "工艺序列为空(草案待补)");
            return;
        }
        for (String step : craft) {
            if (!known.contains(step)) {
                err(rule, at, "工艺 '%s' 不在工艺总表(processes.json)——拒绝生成".formatted(step));
            }
        }
    }

    // ---------------------------------------------------------------- 规则 4
    /** 同一组合表内品位维度轴完全相同的两种配料 → 拒绝(crops.md §3 规则)。 */
    private void rule4GradeDedup() {
        for (MatrixTable table : t.matrixTables) {
            Map<String, Set<String>> byDims = new LinkedHashMap<>();
            for (MatrixRow row : table.rows()) {
                for (String ref : refs(row)) {
                    Resolution res = resolve(ref);
                    if (!res.ok() || res.ing().grades().isEmpty()) {
                        continue;
                    }
                    String key = String.join("+", res.ing().grades());
                    byDims.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(res.ing().displayName());
                }
            }
            for (Map.Entry<String, Set<String>> e : byDims.entrySet()) {
                if (e.getValue().size() > 1) {
                    err(4, "matrix/" + table.file(),
                            "品位维度完全相同(%s)的配料同表: %s——拒绝"
                                    .formatted(e.getKey(), String.join("、", e.getValue())));
                }
            }
        }
    }

    // ---------------------------------------------------------------- 规则 5
    /** 每档位无基础可达菜(钥匙制验收)→ 报缺口。行档位 = max(工艺档, 配料档)。 */
    private void rule5TierCoverage() {
        Map<String, List<String>> byTier = new HashMap<>();
        for (MatrixTable table : t.matrixTables) {
            for (MatrixRow row : table.rows()) {
                if (!"basic".equals(row.quality())) {
                    continue;
                }
                byTier.computeIfAbsent(rowTier(row.craft(), refs(row)), k -> new ArrayList<>())
                        .add(row.id());
            }
        }
        for (RegistryTable table : t.registryTables) {
            for (RegistryEntry row : table.rows()) {
                byTier.computeIfAbsent(rowTier(row.craft(), nullList(row.ingredients())),
                        k -> new ArrayList<>()).add(row.id());
            }
        }
        for (String tier : t.manifest.tiers()) {
            if (byTier.getOrDefault(tier, List.of()).isEmpty()) {
                warn(5, "tiers", "档位 %s 无基础可达菜(钥匙制验收缺口)".formatted(tier));
            }
        }
    }

    private String rowTier(List<String> craft, List<String> refs) {
        int max = 0;
        String name = tierName(0);
        for (String step : nullList(craft)) {
            int i = t.processes.stream().filter(p -> p.id().equals(step))
                    .mapToInt(p -> tierIndex(p.tier())).findFirst().orElse(0);
            if (i > max) {
                max = i;
                name = tierName(i);
            }
        }
        for (String ref : refs) {
            Resolution res = resolve(ref);
            int i = res.ok() ? tierIndex(res.ing().tier()) : 0;
            if (i > max) {
                max = i;
                name = tierName(i);
            }
        }
        return name;
    }

    // ---------------------------------------------------------------- 规则 6
    /** 家常样本集可达率 < 门槛 → 报缺口作物(需求拉动供给;M1' 门槛 ≥50%/目标 75%)。 */
    private void rule6SampleReachability() {
        List<SampleRow> samples = t.samples;
        if (samples.isEmpty()) {
            warn(6, "samples", "家常菜样本集为空(100 道样本集随 M1' 批次入表)");
            return;
        }
        List<String> missing = new ArrayList<>();
        List<String> unreachable = new ArrayList<>();
        for (SampleRow row : samples) {
            boolean ok = true;
            for (String ref : nullList(row.ingredients())) {
                if (!resolve(ref).ok()) {
                    ok = false;
                    missing.add("%s(%s)".formatted(row.name().zh(), ref));
                }
            }
            for (String step : nullList(row.craft())) {
                if (t.processes.stream().noneMatch(p -> p.id().equals(step))) {
                    ok = false;
                    missing.add("%s(工艺 %s)".formatted(row.name().zh(), step));
                }
            }
            if (!ok) {
                unreachable.add(row.name().zh());
            }
        }
        if (samples.size() < 100) warn(6, "samples", "正式 M1 验收需 100 道固定样本;当前 " + samples.size() + " 道仅为开发样本");
        try {
            var audit = new net.mgear.gregfoodexpansion.content.GameplayAudit(t);
            long implemented = samples.stream().filter(audit::sampleReachable).count();
            info(6, "samples", "已实现手工获取链覆盖 %d/%d(%.0f%%);表引用覆盖不等于游戏内验收"
                    .formatted(implemented, samples.size(), implemented * 100.0 / samples.size()));
        } catch (RuntimeException e) {
            warn(6, "samples", "实装链统计不可用: " + e.getMessage());
        }
        double rate = (samples.size() - unreachable.size()) / (double) samples.size();
        double gate = t.manifest.gates() != null && t.manifest.gates().sampleReachableRate() != null
                ? t.manifest.gates().sampleReachableRate() : 0.5;
        double target = t.manifest.gates() != null && t.manifest.gates().sampleTargetRate() != null
                ? t.manifest.gates().sampleTargetRate() : 0.75;
        info(6, "samples", "家常样本集表引用覆盖率 %.0f%%(%d/%d),门槛 ≥%.0f%%、目标 %.0f%%"
                .formatted(rate * 100, samples.size() - unreachable.size(), samples.size(),
                        gate * 100, target * 100));
        if (rate < gate) {
            warn(6, "samples", "可达率低于门槛——缺口作物/配料: %s".formatted(String.join("、", missing)));
        } else if (!missing.isEmpty()) {
            warn(6, "samples", "缺口报告(下一批选题来源): %s".formatted(String.join("、", missing)));
        }
    }

    // ---------------------------------------------------------------- 规则 7
    /** 配料冗余 <3× / 组合覆盖率异常 → 报警;杠杆曲线监控(cuisine-matrix.md §6)。 */
    private void rule7Redundancy() {
        Map<String, Integer> usage = new LinkedHashMap<>();
        for (MatrixTable table : t.matrixTables) {
            for (MatrixRow row : table.rows()) {
                new LinkedHashSet<>(refs(row)).forEach(r -> usage.merge(r, 1, Integer::sum));
            }
        }
        for (RegistryTable table : t.registryTables) {
            for (RegistryEntry row : table.rows()) {
                new LinkedHashSet<>(nullList(row.ingredients())).forEach(r -> usage.merge(r, 1, Integer::sum));
            }
        }
        int min = t.manifest.gates() != null && t.manifest.gates().ingredientRedundancyMin() != null
                ? t.manifest.gates().ingredientRedundancyMin() : 3;
        List<String> thin = new ArrayList<>();
        usage.forEach((ref, n) -> {
            if (n < min) {
                thin.add("%s×%d".formatted(ref, n));
            }
        });
        if (!thin.isEmpty()) {
            warn(7, "universe", "配料冗余不足 %d× 的条目 %d 个: %s"
                    .formatted(min, thin.size(), String.join("、", thin)));
        }
        int crops = t.crops.size();
        int dishes = dishCount();
        if (crops > 0) {
            var band = t.manifest.gates() != null ? t.manifest.gates().leverBand() : null;
            info(7, "lever", "杠杆 菜:作物 = %.2f(当前 %d 菜 / %d 作物),健康带 1:%d~1:%d"
                    .formatted((double) dishes / crops, dishes, crops,
                            band != null && band.min() != null ? band.min() : 10,
                            band != null && band.max() != null ? band.max() : 13));
        }
    }

    // ---------------------------------------------------------------- 规则 8
    /** 增益越过天花板档 → 拒绝(天花板唯一 = MAX 造粮机;菜肴永不持有,原则 8)。 */
    private void rule8GainCeiling() {
        String ceilingTier = t.manifest.gainCeiling() != null ? t.manifest.gainCeiling().tier() : "MAX";
        int ceiling = Math.max(tierIndex(ceilingTier), t.manifest.tiers().size() - 1);
        for (RegistryTable table : t.registryTables) {
            for (RegistryEntry row : table.rows()) {
                if (row.gainTier() == null) {
                    continue;
                }
                if (tierIndex(row.gainTier()) >= ceiling) {
                    err(8, "registry/" + table.file() + "#" + row.id(),
                            "独家增益档 %s 触及/越过全模组天花板(%s,归属 %s)——拒绝".formatted(
                                    row.gainTier(), ceilingTier,
                                    t.manifest.gainCeiling() != null ? t.manifest.gainCeiling().owner() : "造粮机"));
                }
            }
        }
    }

    // ---------------------------------------------------------------- 规则 9
    /** zh/en/ud 语言条目差集 ≠ 0 → 构建失败(en_ud = en_us 颠倒翻转,原版内置语言)。 */
    private void rule9LangParity() {
        List<String> locales = t.manifest.languages();
        if (locales == null || locales.size() < 2) {
            return;
        }
        if (langKeys.values().stream().allMatch(Set::isEmpty)) {
            warn(9, "lang", "语言文件尚未生成(先运行 runData 再复检)");
            return;
        }
        for (int i = 0; i < locales.size(); i++) {
            for (int j = i + 1; j < locales.size(); j++) {
                final String localeA = locales.get(i);
                final String localeB = locales.get(j);
                Set<String> a = langKeys.getOrDefault(localeA, Set.of());
                Set<String> b = langKeys.getOrDefault(localeB, Set.of());
                Set<String> diff = new TreeSet<>();
                a.stream().filter(k -> !b.contains(k)).forEach(k -> diff.add(localeA + ":" + k));
                b.stream().filter(k -> !a.contains(k)).forEach(k -> diff.add(localeB + ":" + k));
                if (!diff.isEmpty()) {
                    err(9, "lang", "%s 与 %s 键差集 = %d(仅列前 8): %s".formatted(
                            localeA, localeB, diff.size(),
                            String.join("、", diff.stream().limit(8).toList())));
                }
            }
        }
    }

    // --------------------------------------------------------------- 规则 10
    /** 同菜异名未合并(同源同名变体)→ 报警。 */
    private void rule10AliasMerge() {
        Map<String, List<String>> byEn = new LinkedHashMap<>();
        Map<String, List<String>> byZh = new LinkedHashMap<>();
        for (MatrixTable table : t.matrixTables) {
            for (MatrixRow row : table.rows()) {
                if (row.name() != null) {
                    String at = "matrix/" + table.file() + "#" + row.id();
                    byEn.computeIfAbsent(norm(row.name().en()), k -> new ArrayList<>()).add(at);
                    byZh.computeIfAbsent(norm(row.name().zh()), k -> new ArrayList<>()).add(at);
                }
            }
        }
        for (RegistryTable table : t.registryTables) {
            for (RegistryEntry row : table.rows()) {
                if (row.name() != null) {
                    String at = "registry/" + table.file() + "#" + row.id();
                    byEn.computeIfAbsent(norm(row.name().en()), k -> new ArrayList<>()).add(at);
                    byZh.computeIfAbsent(norm(row.name().zh()), k -> new ArrayList<>()).add(at);
                }
            }
        }
        byEn.forEach((name, locs) -> {
            if (name.length() > 3 && locs.size() > 1) {
                warn(10, "names", "英文名 '%s' 出现在 %s——疑似同菜异名未合并(aliases)"
                        .formatted(name, String.join("、", locs)));
            }
        });
        byZh.forEach((name, locs) -> {
            if (name.length() > 1 && locs.size() > 1) {
                warn(10, "names", "中文名 '%s' 出现在 %s——疑似同菜异名未合并(aliases)"
                        .formatted(name, String.join("、", locs)));
            }
        });
    }

    // ---------------------------------------------------------------- 公共
    /** 配料引用解析:kind ∈ crop/base/b2/animal/vanilla。 */
    private Resolution resolve(String ref) {
        if (ref == null || ref.isBlank()) {
            return new Resolution(null, "空引用");
        }
        int sep = ref.indexOf(':');
        if (sep <= 0) {
            return new Resolution(null, "缺少 kind: 前缀");
        }
        String kind = ref.substring(0, sep);
        String id = ref.substring(sep + 1);
        return switch (kind) {
            case "crop" -> t.crops.stream().filter(c -> c.id().equals(id)).findFirst()
                    .map(c -> new Resolution(new Resolved(ref,
                            c.name() != null ? c.name().zh() : id,
                            c.grades() == null ? List.of() : c.grades(), "ULV"), null))
                    .orElseGet(() -> new Resolution(null, "作物表无此 id"));
            case "b2" -> t.flavors.stream().filter(f -> f.id().equals(id)).findFirst()
                    .map(f -> new Resolution(new Resolved(ref,
                            f.name() != null ? f.name().zh() : id,
                            f.grades() == null ? List.of() : f.grades(), f.tier()), null))
                    .orElseGet(() -> new Resolution(null, "B2 味型表无此 id"));
            case "base" -> t.baseIngredients.stream().filter(b -> b.id().equals(id)).findFirst()
                    .map(b -> new Resolution(new Resolved(ref,
                            b.name() != null ? b.name().zh() : id,
                            b.grades() == null ? List.of() : b.grades(), b.tier()), null))
                    .orElseGet(() -> new Resolution(null, "加工基料表无此 id"));
            case "animal" -> t.animals.stream().flatMap(a -> a.products().stream())
                    .filter(p -> p.id().equals(id)).findFirst()
                    .map(p -> new Resolution(new Resolved(ref,
                            p.name() != null ? p.name().zh() : id,
                            p.grades() == null ? List.of() : p.grades(), p.tier()), null))
                    .orElseGet(() -> new Resolution(null, "动物源分割表无此 id"));
            case "vanilla" -> t.vanillaLinks.stream().filter(v -> v.id().equals(id)).findFirst()
                    .map(v -> new Resolution(new Resolved(ref, v.name().zh(), nullList(v.grades()), v.tier()), null))
                    .orElseGet(() -> new Resolution(null, "原版接链表未登记该条目"));
            default -> new Resolution(null, "未知 kind '%s'".formatted(kind));
        };
    }

    private List<String> refs(MatrixRow row) {
        List<String> all = new ArrayList<>();
        all.addAll(nullList(row.main()));
        all.addAll(nullList(row.aux()));
        return all;
    }

    private int tierIndex(String tier) {
        List<String> tiers = t.manifest.tiers();
        int i = tiers.indexOf(tier);
        return i < 0 ? tiers.size() : i;
    }

    private String tierName(int index) {
        List<String> tiers = t.manifest.tiers();
        return index >= 0 && index < tiers.size() ? tiers.get(index) : "?" + index;
    }

    private int dishCount() {
        int n = t.matrixTables.stream().mapToInt(tb -> tb.rows().size()).sum();
        n += t.registryTables.stream().mapToInt(tb -> tb.rows().size()).sum();
        return n;
    }

    private static String norm(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT)
                .replace(" ", "").replace("'", "").replace("&", "and");
    }

    private static <T> List<T> nullList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private void err(int rule, String at, String msg) {
        issues.add(new LintIssue(rule, LintIssue.Severity.ERROR, at, msg));
    }

    private void warn(int rule, String at, String msg) {
        issues.add(new LintIssue(rule, LintIssue.Severity.WARNING, at, msg));
    }

    private void info(int rule, String at, String msg) {
        issues.add(new LintIssue(rule, LintIssue.Severity.INFO, at, msg));
    }
}
