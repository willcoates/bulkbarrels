package uk.willtc.mcmods.bulkbarrels;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import uk.willtc.mcmods.bulkbarrels.block.BulkBarrelBlock;

import java.util.function.Function;

public class BulkBarrelsBlocks {
    public static final BulkBarrelBlock BULK_BARREL = register("bulk_barrel", BulkBarrelBlock::new, BlockBehaviour.Properties.of());

    public static void initialize() {
    }

    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID, name));
        return Registry.register(BuiltInRegistries.BLOCK, blockKey, blockFactory.apply(settings.setId(blockKey)));
    }
}
