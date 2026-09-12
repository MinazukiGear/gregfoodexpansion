package net.mgear.gregfoodexpansion.content.datagen;

import com.google.common.hash.Hashing;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.mgear.gregfoodexpansion.content.ContentTables;
import net.mgear.gregfoodexpansion.content.textures.TexturesGenMain;

/** Keep generated textures in the datagen hash cache, so subsequent runs do not purge them. */
public final class TextureDataProvider implements DataProvider {
    private final PackOutput output;
    private final ContentTables tables;
    public TextureDataProvider(PackOutput output, ContentTables tables) { this.output = output; this.tables = tables; }
    @Override public String getName() { return "GFE template textures"; }
    @Override public CompletableFuture<?> run(CachedOutput cache) {
        try {
            TexturesGenMain.generate(tables, new File(System.getProperty("gregfoodexpansion.templates", "../src/templates")),
                    output.getOutputFolder().resolve("assets").toFile(), (file, image) -> {
                        var png = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", png);
                        byte[] bytes = png.toByteArray();
                        cache.writeIfNeeded(file.toPath(), bytes, Hashing.sha1().hashBytes(bytes));
                    });
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
