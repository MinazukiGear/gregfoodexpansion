package net.mgear.gregfoodexpansion.content;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A conservative graph audit of implemented manual recipes, independent of Minecraft classes. */
public final class GameplayAudit {
    private final ContentTables t;
    private final ContentIds ids;
    public GameplayAudit(ContentTables t) { this.t = t; ids = new ContentIds(t); }

    public List<String> errors() {
        var errors = new ArrayList<String>();
        if (t.gameplay == null || t.gameplay.cultivation() == null || t.gameplay.crafting() == null || t.gameplay.potDishes() == null || t.gameplay.machineDishes() == null) {
            return List.of("gameplay.json must declare cultivation, crafting, potDishes and machineDishes");
        }
        Set<String> cropIds = new HashSet<>();
        for (String crop : t.gameplay.cultivation()) {
            if (!cropIds.add(crop)) errors.add("Duplicate cultivated crop: " + crop);
            checkRef("crop:" + crop, errors);
        }
        Set<String> recipes = new HashSet<>();
        for (var recipe : t.gameplay.crafting()) {
            if (recipe.id() == null || !recipe.id().matches("[a-z0-9_/.-]+") || !recipes.add(recipe.id()) || recipe.id().equals("clay_pot")) errors.add("Invalid/duplicate hand recipe: " + recipe.id());
            if (recipe.inputs() == null || recipe.inputs().isEmpty() || recipe.inputs().size() > 9) errors.add("Invalid hand recipe inputs: " + recipe.id());
            else recipe.inputs().forEach(ref -> checkRef(ref, errors));
            checkRef(recipe.output(), errors);
            if (recipe.count() < 1 || recipe.count() > 64) errors.add("Invalid hand recipe output count: " + recipe.id());
        }
        Set<String> dishes = new HashSet<>();
        for (var dish : t.gameplay.potDishes()) {
            if (!dishes.add(dish.id())) errors.add("Duplicate pot dish: " + dish.id());
            if (dish.duration() < 1 || dish.duration() > 72000 || dish.servings() < 2 || dish.servings() > 4
                    || dish.nutrition() < 1 || dish.nutrition() > 20 || !Float.isFinite(dish.saturation())
                    || dish.saturation() < 0 || dish.saturation() > 1) errors.add("Invalid pot nutrition/time/servings: " + dish.id());
            var row = matrix(dish.id());
            if (row == null) { errors.add("Pot dish must reference a matrix row: " + dish.id()); continue; }
            var refs = refs(row);
            if (refs.isEmpty() || refs.size() > 4) errors.add("Pot requires 1-4 ingredients: " + dish.id());
            refs.forEach(ref -> checkRef(ref, errors));
            if (row.craft() == null || row.craft().stream().noneMatch(s -> s.equals("pot-boil") || s.equals("pot-stew"))
                    || row.craft().stream().anyMatch(s -> !List.of("hand-cut", "mix", "pot-boil", "pot-stew").contains(s))) errors.add("Unsupported manual pot process: " + dish.id());
            if (!dishReachable(dish.id())) errors.add("Pot dish has no complete manual acquisition chain: " + dish.id());
        }
        Set<String> upgraded = new HashSet<>();
        for (var dish : t.gameplay.machineDishes()) {
            if (!upgraded.add(dish.source()) || !dishes.contains(dish.source())) errors.add("Machine dish must uniquely upgrade an enabled pot dish: " + dish.source());
            if (dish.duration() < 1 || dish.duration() > 72000 || dish.water() < 1 || dish.water() > 16000
                    || dish.nutrition() < 1 || dish.nutrition() > 20 || !Float.isFinite(dish.saturation())
                    || dish.saturation() < 0 || dish.saturation() > 1) errors.add("Invalid machine food values: " + dish.id());
            checkRef("machine-dish:" + dish.id(), errors);
        }
        for (String dish : dishes) if (!upgraded.contains(dish)) errors.add("Pot dish lacks a machine upgrade: " + dish);
        return errors;
    }

    private void checkRef(String ref, List<String> errors) {
        try { ids.item(ref); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }
    }
    public boolean sampleReachable(ContentTypes.SampleRow sample) {
        return t.gameplay != null && t.gameplay.potDishes().stream().anyMatch(d -> {
            var row = matrix(d.id());
            return row != null && row.name().zh().equals(sample.name().zh()) && dishReachable(d.id());
        });
    }
    public boolean dishReachable(String id) {
        var row = matrix(id);
        return row != null && reachable("utensil:clay_pot", new HashSet<>())
                && reachable("utensil:water_bowl", new HashSet<>())
                && refs(row).stream().allMatch(ref -> reachable(ref, new HashSet<>()));
    }
    private boolean reachable(String ref, Set<String> visiting) {
        if (!visiting.add(ref)) return false;
        try {
            if (ref.equals("utensil:water_bowl")) return true; // Vanilla bowls + world water source, implemented event.
            if (ref.equals("utensil:clay_pot")) return reachable("utensil:unfired_clay_pot", visiting); // Generated smelting recipe.
            if (ref.startsWith("crop:") && t.gameplay.cultivation().contains(ref.substring(5))) return true;
            if (t.vanillaLinks.stream().anyMatch(v -> ref.equals("vanilla:" + v.id()) || v.aliases() != null && v.aliases().contains(ref))) return true;
            return t.gameplay.crafting().stream().filter(r -> ref.equals(r.output())).anyMatch(r ->
                    r.inputs().stream().allMatch(input -> reachable(input, new HashSet<>(visiting))));
        } finally { visiting.remove(ref); }
    }
    private ContentTypes.MatrixRow matrix(String id) {
        return t.matrixTables.stream().flatMap(tb -> tb.rows().stream()).filter(r -> r.id().equals(id)).findFirst().orElse(null);
    }
    public static List<String> refs(ContentTypes.MatrixRow row) {
        var refs = new ArrayList<String>();
        if (row.main() != null) refs.addAll(row.main());
        if (row.aux() != null) refs.addAll(row.aux());
        if (row.flavor() != null) refs.add(row.flavor());
        return refs;
    }
}
