package net.mgear.gregfoodexpansion.content.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.mgear.gregfoodexpansion.content.ContentIds;
import net.mgear.gregfoodexpansion.content.ContentTables;

/** Deterministic models, loot, tags, world generation and recipes from the playable subset. */
public final class GameplayDataProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final ContentTables t;
    private final ContentIds ids;
    private final List<CompletableFuture<?>> writes = new ArrayList<>();
    private CachedOutput cache;

    public GameplayDataProvider(PackOutput output, ContentTables tables) {
        this.output = output;
        t = tables;
        ids = new ContentIds(tables);
    }
    @Override public String getName() { return "GFE gameplay resources"; }

    @Override public CompletableFuture<?> run(CachedOutput cache) {
        this.cache = cache;
        writes.clear();
        ids.ownedItems().forEach((item, ref) -> {
            String id = item.substring(ContentIds.MOD.length() + 1);
            if (id.equals("clay_pot")) return;
            String texture = texture(id, ref);
            save("assets/gregfoodexpansion/models/item/" + id + ".json",
                    obj("parent", "minecraft:item/generated", "textures", obj("layer0", texture)));
        });
        for (String crop : t.gameplay.cultivation()) crop(crop);
        potModel();
        for (var recipe : t.gameplay.crafting()) {
            save("data/gregfoodexpansion/recipes/hand/" + recipe.id() + ".json",
                    obj("type", "minecraft:crafting_shapeless", "category", "misc",
                            "ingredients", recipe.inputs().stream().map(ref -> obj("item", ids.item(ref))).toList(),
                            "result", obj("item", ids.item(recipe.output()), "count", recipe.count())));
            unlock("hand/" + recipe.id(), recipe.inputs().get(0));
        }
        save("data/gregfoodexpansion/recipes/hand/clay_pot.json",
                obj("type", "minecraft:smelting", "category", "blocks",
                        "ingredient", obj("item", ids.item("utensil:unfired_clay_pot")),
                        "result", ids.item("utensil:clay_pot"), "experience", 0.1, "cookingtime", 200));
        unlock("hand/clay_pot", "utensil:unfired_clay_pot");
        for (var dish : t.gameplay.potDishes()) {
            var row = t.matrixTables.stream().flatMap(tb -> tb.rows().stream()).filter(r -> r.id().equals(dish.id())).findFirst().orElseThrow();
            var refs = new ArrayList<>(row.main());
            refs.addAll(row.aux());
            if (row.flavor() != null) refs.add(row.flavor());
            save("data/gregfoodexpansion/recipes/pot/" + dish.id() + ".json",
                    obj("type", "gregfoodexpansion:clay_pot_cooking",
                            "ingredients", refs.stream().map(ref -> obj("item", ids.item(ref))).toList(),
                            "result", obj("item", ids.item("dish:" + dish.id())),
                            "duration", dish.duration(), "servings", dish.servings()));
        }
        tag("forge", "items", "crops", t.crops.stream().map(c -> ids.item("crop:" + c.id())).toList());
        for (var crop : t.crops) tag("forge", "items", "crops/" + crop.id(), List.of(ids.item("crop:" + crop.id())));
        tag("forge", "items", "seeds", t.gameplay.cultivation().stream().map(c -> ids.item("seed:" + c)).toList());
        tag("gregfoodexpansion", "items", "foods", java.util.stream.Stream.concat(t.gameplay.potDishes().stream().map(d -> ids.item("dish:" + d.id())), t.gameplay.machineDishes().stream().map(d -> ids.item("machine-dish:" + d.id()))).toList());
        // Pickaxe tag is owned by the Registrate block-tag provider alongside machine tags.
        tag("forge", "items", "flour/wheat", List.of(ids.item("base:flour")));
        tag("forge", "items", "flour", List.of("#forge:flour/wheat"));
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private String texture(String id, String ref) {
        if (ref.startsWith("crop:")) return "gregfoodexpansion:item/crop/" + id + "_stage_3";
        if (ref.startsWith("seed:")) return "minecraft:item/wheat_seeds";
        if (ref.startsWith("machine-dish:")) {
            String source = t.gameplay.machineDishes().stream().filter(d -> d.id().equals(id)).findFirst().orElseThrow().source();
            return "gregfoodexpansion:item/dish/" + source;
        }
        if (ref.startsWith("dish:")) {
            boolean matrix = t.matrixTables.stream().flatMap(tb -> tb.rows().stream()).anyMatch(r -> r.id().equals(id));
            return matrix ? "gregfoodexpansion:item/dish/" + id : "minecraft:item/mushroom_stew";
        }
        return "minecraft:item/" + switch (id) {
            case "kitchen_knife" -> "iron_sword";
            case "rolling_pin" -> "stick";
            case "mortar", "water_bowl" -> "bowl";
            case "unfired_clay_pot" -> "clay_ball";
            case "flour" -> "sugar";
            case "dough", "dough-skin", "noodles" -> "wheat";
            case "chicken-cut" -> "chicken";
            case "beef-mince" -> "beef";
            case "ham" -> "cooked_porkchop";
            case "cooking-oil", "scallion-oil" -> "honey_bottle";
            default -> "mushroom_stew";
        };
    }

    private void crop(String id) {
        String block = "gregfoodexpansion:" + id + "_crop";
        Map<String, Object> variants = new LinkedHashMap<>();
        for (int age = 0; age <= 7; age++) variants.put("age=" + age, obj("model", "gregfoodexpansion:block/" + id + "_stage_" + age / 2));
        save("assets/gregfoodexpansion/blockstates/" + id + "_crop.json", obj("variants", variants));
        for (int stage = 0; stage < 4; stage++) {
            save("assets/gregfoodexpansion/models/block/" + id + "_stage_" + stage + ".json",
                    obj("parent", "minecraft:block/cross", "render_type", "minecraft:cutout",
                            "textures", obj("cross", "gregfoodexpansion:item/crop/" + id + "_stage_" + stage)));
        }
        Object mature = obj("condition", "minecraft:block_state_property", "block", block, "properties", obj("age", "7"));
        save("data/gregfoodexpansion/loot_tables/blocks/" + id + "_crop.json",
                obj("type", "minecraft:block", "pools", List.of(
                        pool(ids.item("seed:" + id), List.of()),
                        pool(ids.item("seed:" + id), List.of(mature)),
                        pool(ids.item("crop:" + id), List.of(mature)))));
        tag("forge", "items", "seeds/" + id, List.of(ids.item("seed:" + id)));
        Object wildState = obj("Name", block, "Properties", obj("age", "7", "wild", "true"));
        Object survive = obj("type", "minecraft:all_of", "predicates", List.of(
                obj("type", "minecraft:matching_blocks", "blocks", List.of("minecraft:air")),
                obj("type", "minecraft:would_survive", "state", wildState)));
        save("data/gregfoodexpansion/worldgen/configured_feature/wild_" + id + ".json",
                obj("type", "minecraft:random_patch", "config", obj("tries", 16, "xz_spread", 4, "y_spread", 2,
                        "feature", obj("feature", obj("type", "minecraft:simple_block",
                                "config", obj("to_place", obj("type", "minecraft:simple_state_provider", "state", wildState))),
                                "placement", List.of(obj("type", "minecraft:block_predicate_filter", "predicate", survive))))));
        save("data/gregfoodexpansion/worldgen/placed_feature/wild_" + id + ".json",
                obj("feature", "gregfoodexpansion:wild_" + id, "placement", List.of(
                        obj("type", "minecraft:rarity_filter", "chance", 12),
                        obj("type", "minecraft:in_square"),
                        obj("type", "minecraft:heightmap", "heightmap", "WORLD_SURFACE_WG"),
                        obj("type", "minecraft:biome"))));
        save("data/gregfoodexpansion/forge/biome_modifier/wild_" + id + ".json",
                obj("type", "forge:add_features", "biomes", List.of("minecraft:plains", "minecraft:sunflower_plains", "minecraft:forest"),
                        "features", "gregfoodexpansion:wild_" + id, "step", "vegetal_decoration"));
    }

    private void potModel() {
        List<Object> elements = new ArrayList<>();
        for (var bounds : new int[][]{{2,0,2,14,2,14},{2,2,2,4,12,14},{12,2,2,14,12,14},{4,2,2,12,12,4},{4,2,12,12,12,14}}) {
            Map<String,Object> faces = new LinkedHashMap<>();
            for (String face : List.of("north","south","east","west","up","down")) faces.put(face, obj("texture", "#clay"));
            elements.add(obj("from", List.of(bounds[0],bounds[1],bounds[2]), "to", List.of(bounds[3],bounds[4],bounds[5]), "faces", faces));
        }
        save("assets/gregfoodexpansion/models/block/clay_pot.json",
                obj("parent","minecraft:block/block", "textures",obj("clay","minecraft:block/terracotta","particle","minecraft:block/terracotta"),"elements",elements));
        save("assets/gregfoodexpansion/models/item/clay_pot.json", obj("parent","gregfoodexpansion:block/clay_pot"));
        save("assets/gregfoodexpansion/blockstates/clay_pot.json", obj("variants",obj("",obj("model","gregfoodexpansion:block/clay_pot"))));
        save("data/gregfoodexpansion/loot_tables/blocks/clay_pot.json", obj("type","minecraft:block","pools",List.of(pool("gregfoodexpansion:clay_pot",List.of()))));
    }

    private void unlock(String recipe, String ref) {
        save("data/gregfoodexpansion/advancements/recipes/" + recipe + ".json",
                obj("parent","minecraft:recipes/root","criteria",obj("has_ingredient",obj("trigger","minecraft:inventory_changed",
                        "conditions",obj("items",List.of(obj("items",List.of(ids.item(ref))))))),
                        "requirements",List.of(List.of("has_ingredient")),
                        "rewards",obj("recipes",List.of("gregfoodexpansion:" + recipe))));
    }
    private Object pool(String item, List<Object> conditions) {
        var all = new ArrayList<>(conditions);
        all.add(obj("condition","minecraft:survives_explosion"));
        return obj("rolls",1,"conditions",all,"entries",List.of(obj("type","minecraft:item","name",item)));
    }
    private void tag(String namespace, String kind, String name, List<String> values) {
        save("data/" + namespace + "/tags/" + kind + "/" + name + ".json", obj("replace",false,"values",values));
    }
    private void save(String path, Object value) {
        writes.add(DataProvider.saveStable(cache, GSON.toJsonTree(value), output.getOutputFolder().resolve(path)));
    }
    private static Map<String,Object> obj(Object... pairs) {
        Map<String,Object> map = new LinkedHashMap<>();
        for (int i=0; i<pairs.length; i+=2) map.put((String)pairs[i], pairs[i+1]);
        return map;
    }
}
