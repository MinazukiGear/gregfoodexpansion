package net.mgear.gregfoodexpansion.content;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import net.mgear.gregfoodexpansion.content.ContentTypes.AnimalEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.BaseIngredientEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.CropEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.FlavorEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.MachineEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.Manifest;
import net.mgear.gregfoodexpansion.content.ContentTypes.MatrixTable;
import net.mgear.gregfoodexpansion.content.ContentTypes.ProcessEntry;
import net.mgear.gregfoodexpansion.content.ContentTypes.RegistryTable;
import net.mgear.gregfoodexpansion.content.ContentTypes.SampleRow;

/**
 * 内容表装载器:从 {@code content/} 根读取 manifest、三张核心表与辅助表。
 * lint 与贴图管线走文件系统源,datagen 走类路径源(同构,见 ContentSource)。
 */
public final class ContentTables {
    public static final Gson GSON = new Gson();

    public final Manifest manifest;
    public final List<ProcessEntry> processes;
    public final List<MachineEntry> machines;
    public final List<CropEntry> crops;
    public final List<FlavorEntry> flavors;
    public final List<BaseIngredientEntry> baseIngredients;
    public final List<AnimalEntry> animals;
    public final List<MatrixTable> matrixTables;
    public final List<RegistryTable> registryTables;
    public final List<SampleRow> samples;
    public final List<ContentTypes.VanillaLink> vanillaLinks;
    public final ContentTypes.Gameplay gameplay;

    private ContentTables(Manifest manifest, List<ProcessEntry> processes, List<MachineEntry> machines,
                          List<CropEntry> crops, List<FlavorEntry> flavors,
                          List<BaseIngredientEntry> baseIngredients, List<AnimalEntry> animals,
                          List<MatrixTable> matrixTables, List<RegistryTable> registryTables,
                          List<SampleRow> samples, List<ContentTypes.VanillaLink> vanillaLinks, ContentTypes.Gameplay gameplay) {
        this.manifest = manifest;
        this.processes = processes;
        this.machines = machines;
        this.crops = crops;
        this.flavors = flavors;
        this.baseIngredients = baseIngredients;
        this.animals = animals;
        this.matrixTables = matrixTables;
        this.registryTables = registryTables;
        this.samples = samples;
        this.vanillaLinks = vanillaLinks;
        this.gameplay = gameplay;
    }

    /** 表数据来源抽象:文件系统(lint/贴图任务)或类路径(datagen)。 */
    public interface ContentSource extends AutoCloseable {
        /** 列出目录下的表文件名(不递归;目录不存在返回空列表)。 */
        List<String> listFiles(String dir) throws IOException;

        /** 打开相对路径的字符流。 */
        Reader open(String path) throws IOException;

        @Override
        void close() throws IOException;
    }

    public static ContentTables load(ContentSource source) throws IOException {
        try (source) {
            Manifest manifest = read(source, "manifest.json",
                    new TypeToken<Manifest>() {}.getType());
            List<ProcessEntry> processes = read(source, "processes.json",
                    new TypeToken<List<ProcessEntry>>() {}.getType());
            List<MachineEntry> machines = readDir(source, "machines",
                    new TypeToken<List<MachineEntry>>() {}.getType());
            List<CropEntry> crops = readDir(source, "crops",
                    new TypeToken<List<CropEntry>>() {}.getType());
            List<FlavorEntry> flavors = readDir(source, "flavors",
                    new TypeToken<List<FlavorEntry>>() {}.getType());
            List<BaseIngredientEntry> bases = readDir(source, "ingredients",
                    new TypeToken<List<BaseIngredientEntry>>() {}.getType());
            List<AnimalEntry> animals = readDir(source, "animals",
                    new TypeToken<List<AnimalEntry>>() {}.getType());
            List<MatrixTable> matrix = new ArrayList<>();
            for (String file : source.listFiles("matrix")) {
                String table = file.substring(0, file.length() - ".json".length());
                matrix.add(new MatrixTable(table, read(source, "matrix/" + file,
                        new TypeToken<List<ContentTypes.MatrixRow>>() {}.getType())));
            }
            List<RegistryTable> registry = new ArrayList<>();
            for (String file : source.listFiles("registry")) {
                String table = file.substring(0, file.length() - ".json".length());
                registry.add(new RegistryTable(table, read(source, "registry/" + file,
                        new TypeToken<List<ContentTypes.RegistryEntry>>() {}.getType())));
            }
            List<SampleRow> samples = readDir(source, "samples",
                    new TypeToken<List<SampleRow>>() {}.getType());
            List<ContentTypes.VanillaLink> vanillaLinks = readDir(source, "vanilla-links",
                    new TypeToken<List<ContentTypes.VanillaLink>>() {}.getType());
            ContentTypes.Gameplay gameplay = read(source, "gameplay.json",
                    new TypeToken<ContentTypes.Gameplay>() {}.getType());
            if (manifest == null) {
                throw new IOException("缺少 content/manifest.json");
            }
            return new ContentTables(manifest, nullSafe(processes), nullSafe(machines), nullSafe(crops),
                    nullSafe(flavors), nullSafe(bases), nullSafe(animals), List.copyOf(matrix),
                    List.copyOf(registry), nullSafe(samples), vanillaLinks, gameplay);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> readDir(ContentSource source, String dir, java.lang.reflect.Type type)
            throws IOException {
        List<T> rows = new ArrayList<>();
        List<String> files = new ArrayList<>(source.listFiles(dir));
        files.sort(Comparator.naturalOrder());
        for (String file : files) {
            rows.addAll((List<T>) read(source, dir + "/" + file, type));
        }
        return rows;
    }

    @SuppressWarnings("unchecked")
    private static <T> T read(ContentSource source, String path, java.lang.reflect.Type type)
            throws IOException {
        try (Reader reader = source.open(path)) {
            return (T) GSON.fromJson(reader, type);
        } catch (java.io.FileNotFoundException e) {
            if (path.equals("manifest.json")) {
                throw e;
            }
            return null;
        }
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }

    /** 文件系统源:lint(ContentLintMain)与贴图管线(TexturesGenMain)使用。 */
    public static final class FsSource implements ContentSource {
        private final Path root;

        public FsSource(Path root) {
            this.root = root;
        }

        @Override
        public List<String> listFiles(String dir) throws IOException {
            Path dirPath = root.resolve(dir);
            if (!Files.isDirectory(dirPath)) {
                return List.of();
            }
            try (Stream<Path> stream = Files.list(dirPath)) {
                return stream.map(p -> p.getFileName().toString())
                        .filter(n -> n.endsWith(".json"))
                        .sorted()
                        .toList();
            }
        }

        @Override
        public Reader open(String path) throws IOException {
            return Files.newBufferedReader(root.resolve(path), StandardCharsets.UTF_8);
        }

        @Override
        public void close() {}
    }

    /** 类路径源:datagen 在 runData 中使用(dev 环境目录 URL;jar 内枚举)。 */
    public static final class ClasspathSource implements ContentSource {
        private final ClassLoader loader;
        private final String base;

        public ClasspathSource(ClassLoader loader, String base) {
            this.loader = loader;
            this.base = base.endsWith("/") ? base : base + "/";
        }

        @Override
        public List<String> listFiles(String dir) throws IOException {
            java.net.URL url = loader.getResource(base + dir);
            if (url == null) {
                return List.of();
            }
            String protocol = url.getProtocol();
            // dev 环境为 file:,Forge modlauncher(datagen)下为 union:(已注册 NIO provider),
            // 二者均可转为 Path 后列目录;jar 内发布物走 JarFile 枚举。
            if ("file".equals(protocol) || "union".equals(protocol)) {
                try (Stream<Path> stream = Files.list(Path.of(url.toURI()))) {
                    return stream.map(p -> p.getFileName().toString())
                            .filter(n -> n.endsWith(".json"))
                            .sorted()
                            .toList();
                } catch (java.net.URISyntaxException e) {
                    throw new IOException(e);
                }
            }
            if ("jar".equals(protocol)) {
                String jarPath = url.getPath().substring(5, url.getPath().indexOf('!'));
                String prefix = base + dir + "/";
                List<String> names = new ArrayList<>();
                try (JarFile jar = new JarFile(jarPath)) {
                    jar.stream()
                            .map(JarEntry::getName)
                            .filter(n -> n.startsWith(prefix) && n.endsWith(".json"))
                            .map(n -> n.substring(prefix.length()))
                            .filter(n -> !n.contains("/"))
                            .sorted()
                            .forEach(names::add);
                }
                return names;
            }
            throw new IOException("不支持的类路径协议: " + protocol);
        }

        @Override
        public Reader open(String path) throws IOException {
            java.io.InputStream in = loader.getResourceAsStream(base + path);
            if (in == null) {
                throw new java.io.FileNotFoundException("classpath:" + base + path);
            }
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        }

        @Override
        public void close() {}
    }
}
