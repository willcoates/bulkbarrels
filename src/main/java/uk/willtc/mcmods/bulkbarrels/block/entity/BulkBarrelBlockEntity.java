package uk.willtc.mcmods.bulkbarrels.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlockEntities;

public class BulkBarrelBlockEntity extends BlockEntity {
    public BulkBarrelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BulkBarrelsBlockEntities.BULK_BARREL_BLOCK_ENTITY, blockPos, blockState);
    }
}
