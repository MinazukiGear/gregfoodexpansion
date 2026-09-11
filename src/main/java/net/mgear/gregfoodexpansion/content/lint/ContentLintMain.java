package net.mgear.gregfoodexpansion.content.lint;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.mgear.gregfoodexpansion.content.ContentTables;
import net.mgear.gregfoodexpansion.content.ContentTables.FsSource;

/**
 * lint CLI 入口(Gradle 任务 contentLint)。
 * 参数:content 表根目录、generated 资产目录、main 资产目录(用于语言键差集检查)。
 * 有错误级问题以退出码 1 结束,使构建失败(content-pipeline.md §5)。
 */
public final class ContentLintMain {
    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out), true,
                StandardCharsets.UTF_8));
        if (args.length < 3) {
            System.err.println("用法: ContentLintMain <contentDir> <generatedAssetsDir> <mainAssetsDir>");
            System.exit(2);
        }
        Path contentDir = Path.of(args[0]);
        ContentTables tables;
        try {
            tables = ContentTables.load(new FsSource(contentDir));
        } catch (IOException e) {
            System.err.println("内容表装载失败: " + e.getMessage());
            System.exit(2);
            return;
        }

        Map<String, Set<String>> langKeys = readLangKeys(tables.manifest.languages(),
                List.of(Path.of(args[1]), Path.of(args[2])));
        List<LintIssue> issues = new ContentLint(tables, langKeys).run();

        long errors = issues.stream().filter(i -> i.severity() == LintIssue.Severity.ERROR).count();
        long warnings = issues.stream().filter(i -> i.severity() == LintIssue.Severity.WARNING).count();
        long infos = issues.stream().filter(i -> i.severity() == LintIssue.Severity.INFO).count();

        System.out.println("== GFE 内容 lint(content-pipeline.md §5)==");
        issues.sort((a, b) -> {
            int bySeverity = a.severity().compareTo(b.severity());
            return bySeverity != 0 ? bySeverity : Integer.compare(a.rule(), b.rule());
        });
        List<String> report = new ArrayList<>();
        issues.forEach(i -> report.add("  " + i));
        report.add("汇总: 作物 %d、矩阵菜 %d、签名菜 %d、样本 %d | 错误 %d / 警告 %d / 信息 %d"
                .formatted(tables.crops.size(),
                        tables.matrixTables.stream().mapToInt(tb -> tb.rows().size()).sum(),
                        tables.registryTables.stream().mapToInt(tb -> tb.rows().size()).sum(),
                        tables.samples.size(), errors, warnings, infos));
        report.forEach(System.out::println);
        // 报告落盘:终端代码页(GBK)下中文可能乱码,文件恒为 UTF-8 可读
        if (args.length >= 4) {
            Path reportFile = Path.of(args[3]);
            try {
                Files.createDirectories(reportFile.getParent());
                Files.write(reportFile, ("== GFE 内容 lint ==\n" + String.join("\n", report) + "\n")
                        .getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println("lint 报告写盘失败(不影响结论): " + e.getMessage());
            }
        }
        if (errors > 0) {
            System.out.println("结果: 未通过(错误级问题必须清零后才能合入)");
            System.exit(1);
        } else {
            System.out.println("结果: 通过(警告/信息为批次缺口报告,见 content-pipeline.md §5)");
        }
    }

    /** 合并多个资产目录下 <locale>.json 的键集合(generated 与 main 两处来源)。 */
    private static Map<String, Set<String>> readLangKeys(List<String> locales, List<Path> assetRoots)
            throws IOException {
        Map<String, Set<String>> result = new HashMap<>();
        for (String locale : locales == null ? List.<String>of() : locales) {
            Set<String> keys = new java.util.HashSet<>();
            for (Path root : assetRoots) {
                Path file = root.resolve("gregfoodexpansion/lang/" + locale + ".json");
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                    StringBuilder sb = new StringBuilder();
                    lines.forEach(sb::append);
                    JsonObject obj = JsonParser.parseString(sb.toString()).getAsJsonObject();
                    obj.keySet().forEach(keys::add);
                }
            }
            result.put(locale, keys);
        }
        return result;
    }
}
