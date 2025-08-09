package uk.willtc.mcmods.bulkbarrels.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.willtc.mcmods.bulkbarrels.block.entity.BulkBarrelBlockEntity;

public class BulkBarrelBlock extends BaseEntityBlock {
    public BulkBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BulkBarrelBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BulkBarrelBlockEntity(blockPos, blockState);
    }
}
