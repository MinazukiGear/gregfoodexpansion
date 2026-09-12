package net.mgear.gregfoodexpansion.registry;

import java.util.List;
import java.util.Optional;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.mgear.gregfoodexpansion.GregFoodExpansion;

/** Machine registration and resources; language files belong exclusively to ModLangProvider. */
public final class GFERegistration {
    public static final GTRegistrate REGISTRATE = new FoodRegistrate();
    private GFERegistration() {}

    private static final class FoodRegistrate extends GTRegistrate {
        private RegistrateDataProvider machineProviders;
        private FoodRegistrate() { super(GregFoodExpansion.MOD_ID); }

        @Override protected void onData(GatherDataEvent event) {
            machineProviders = new RegistrateDataProvider(this, GregFoodExpansion.MOD_ID, event);
            // GT replaces Registrate's blockstate type; resolve the active provider by name.
            var types = com.gregtechceu.gtceu.core.mixins.registrate.RegistrateDataProviderAccessor.gtceu$getTypes();
            for (String name : List.of("recipe", "advancement", "loot", "tags/block", "tags/item",
                    "tags/fluid", "tags/entity", "registrate_generic_server_provider",
                    "blockstate", "item_model", "registrate_generic_client_provider")) {
                var type = types.get(name);
                if (type != null) machineProviders.getSubProvider(type)
                        .ifPresent(provider -> event.getGenerator().addProvider(true, provider));
            }
        }
        @Override public <P extends RegistrateProvider> Optional<P> getDataProvider(ProviderType<P> type) {
            return machineProviders == null ? Optional.empty() : machineProviders.getSubProvider(type);
        }
    }
}
