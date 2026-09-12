package net.mgear.gregfoodexpansion.content;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Shared, Minecraft-independent reference resolution for lint, datagen and registration. */
public final class ContentIds {
    public static final String MOD = "gregfoodexpansion";
    public static final List<String> TOOLS = List.of("mortar", "rolling_pin", "kitchen_knife");
    public static final List<String> UTENSILS = List.of("water_bowl", "unfired_clay_pot", "clay_pot");
    private final Map<String, String> refs = new LinkedHashMap<>();
    private final Map<String, String> owners = new LinkedHashMap<>();

    public ContentIds(ContentTables t) {
        for (var link : t.vanillaLinks) {
            if (link.item() == null || !link.item().matches("minecraft:[a-z0-9_/.-]+")) {
                throw new IllegalArgumentException("Invalid vanilla item: " + link.item());
            }
            put("vanilla:" + link.id(), link.item(), false);
            if (link.aliases() != null) for (String alias : link.aliases()) put(alias, link.item(), false);
        }
        t.crops.forEach(c -> own("crop:" + c.id(), c.id()));
        t.baseIngredients.forEach(b -> own("base:" + b.id(), b.id()));
        t.flavors.forEach(f -> own("b2:" + f.id(), f.id()));
        t.animals.forEach(a -> a.products().forEach(p -> {
            if (!refs.containsKey("animal:" + p.id())) own("animal:" + p.id(), p.id());
        }));
        t.matrixTables.forEach(tb -> tb.rows().forEach(r -> own("dish:" + r.id(), r.id())));
        t.registryTables.forEach(tb -> tb.rows().forEach(r -> own("dish:" + r.id(), r.id())));
        if (t.gameplay != null && t.gameplay.machineDishes() != null) {
            t.gameplay.machineDishes().forEach(d -> own("machine-dish:" + d.id(), d.id()));
        }
        TOOLS.forEach(id -> own("tool:" + id, id));
        UTENSILS.forEach(id -> own("utensil:" + id, id));
        if (t.gameplay != null) for (String id : t.gameplay.cultivation()) {
            if (!refs.containsKey("crop:" + id)) throw new IllegalArgumentException("Unknown cultivated crop: " + id);
            own("seed:" + id, id + "_seeds");
        }
    }

    private void own(String ref, String id) {
        if (!id.matches("[a-z0-9_/.-]+")) throw new IllegalArgumentException("Invalid item id: " + id);
        put(ref, MOD + ":" + id, true);
    }

    private void put(String ref, String item, boolean own) {
        if (refs.putIfAbsent(ref, item) != null) throw new IllegalArgumentException("Duplicate reference: " + ref);
        if (own && owners.putIfAbsent(item, ref) != null) throw new IllegalArgumentException("Duplicate runtime item: " + item);
    }

    public String item(String ref) {
        String item = refs.get(ref);
        if (item == null) throw new IllegalArgumentException("Unknown gameplay reference: " + ref);
        return item;
    }

    public Map<String, String> ownedItems() { return Map.copyOf(owners); }
}
