package uk.willtc.mcmods.bulkbarrels.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity baseEntity = level.getBlockEntity(blockPos);
        if (!(baseEntity instanceof BulkBarrelBlockEntity entity)) {
            return InteractionResult.FAIL;
        }

        if (itemStack.isEmpty()) {
            ItemStack removedItems = entity.takeItems(player.isShiftKeyDown() ? 1 : 64);
            player.getInventory().placeItemBackInInventory(removedItems);
        } else {
            entity.storeItems(itemStack);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity baseEntity = level.getBlockEntity(blockPos);
        if (!(baseEntity instanceof BulkBarrelBlockEntity entity)) {
            return InteractionResult.FAIL;
        }

        ItemStack removedItems = entity.takeItems(player.isShiftKeyDown() ? 1 : 64);
        ItemEntity droppedItemEntity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), removedItems);
        level.addFreshEntity(droppedItemEntity);

        return InteractionResult.PASS;
    }
}
