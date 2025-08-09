package uk.willtc.mcmods.bulkbarrels;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import uk.willtc.mcmods.bulkbarrels.block.entity.BulkBarrelBlockEntity;

public class BulkBarrelsBlockEntities {
    public static final BlockEntityType<BulkBarrelBlockEntity> BULK_BARREL_BLOCK_ENTITY = register("bulk_barrel", BulkBarrelBlockEntity::new, BulkBarrelsBlocks.BULK_BARREL);

    public static void initialize() {
    }

    private static <T extends BlockEntity>BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }
}
