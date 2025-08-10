package uk.willtc.mcmods.bulkbarrels.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlocks;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsItems;
import uk.willtc.mcmods.bulkbarrels.Tier;
import uk.willtc.mcmods.bulkbarrels.block.BulkBarrelBlock;

import java.util.concurrent.CompletableFuture;

public class BarrelLootTableGenerator extends FabricBlockLootTableProvider {
    public BarrelLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        add(BulkBarrelsBlocks.BULK_BARREL, block -> {
            var builder = new LootTable.Builder();

            for (var tier : Tier.values()) {
                var barrelItem = BulkBarrelsItems.TIERED_BULK_BARRELS.get(tier);
                var pool = LootPool.lootPool();

                pool.conditionally(
                        new LootItemBlockStatePropertyCondition.Builder(BulkBarrelsBlocks.BULK_BARREL)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BulkBarrelBlock.TIER, tier))
                        .build()
                );

                pool.add(LootItem.lootTableItem(barrelItem));

                builder.pool(pool.build());
            }

            return builder;
        });
    }
}
