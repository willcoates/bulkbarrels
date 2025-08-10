package uk.willtc.mcmods.bulkbarrels;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import uk.willtc.mcmods.bulkbarrels.item.TieredBlockItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BulkBarrelsItems {
    public static final Map<Tier, BlockItem> TIERED_BULK_BARRELS = new HashMap<>();

    public static void initialize() {
        for (Tier tier : Tier.values()) {
            String name = tier.getSerializedName() + "_bulk_barrel";
            BlockItem item = register(name, props -> new TieredBlockItem(BulkBarrelsBlocks.BULK_BARREL, tier, props), new Item.Properties());
            TIERED_BULK_BARRELS.put(tier, item);
        }
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }
}
