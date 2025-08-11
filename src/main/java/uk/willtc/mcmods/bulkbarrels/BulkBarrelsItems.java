package uk.willtc.mcmods.bulkbarrels;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import uk.willtc.mcmods.bulkbarrels.item.TierUpgradeItem;
import uk.willtc.mcmods.bulkbarrels.item.TieredBlockItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BulkBarrelsItems {
    public static final Map<Tier, BlockItem> TIERED_BULK_BARRELS = new HashMap<>();
    public static final Map<Tier, TierUpgradeItem> TIERED_UPGRADE_ITEMS = new HashMap<>();
    public static final Item BARREL_FRAME = register("barrel_frame", Item::new, new Item.Properties());

    public static void initialize() {
        var tiers = Tier.values();

        for (Tier tier : tiers) {
            registerTieredBarrel(tier);
        }

        // Skip first tier, makes no sense to upgrade to the lowest tier
        for (var i = 1; i < tiers.length; i++) {
            var previousTier = tiers[i - 1];
            var currentTier = tiers[i];
            registerTieredUpgrade(currentTier, previousTier);
        }
    }

    private static void registerTieredBarrel(Tier tier) {
        String name = tier.getSerializedName() + "_bulk_barrel";
        BlockItem item = register(name, props -> new TieredBlockItem(BulkBarrelsBlocks.BULK_BARREL, tier, props), new Item.Properties());
        TIERED_BULK_BARRELS.put(tier, item);
    }

    private static void registerTieredUpgrade(Tier currentTier, Tier previousTier) {
        String name = currentTier.getSerializedName() + "_barrel_upgrade";
        TIERED_UPGRADE_ITEMS.put(currentTier, register(name, TierUpgradeItem.builder(currentTier, previousTier), new Item.Properties()));
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }
}
