package uk.willtc.mcmods.bulkbarrels.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.willtc.mcmods.bulkbarrels.Tier;
import uk.willtc.mcmods.bulkbarrels.block.entity.BulkBarrelBlockEntity;
import uk.willtc.mcmods.bulkbarrels.item.TierUpgradeItem;
import uk.willtc.mcmods.bulkbarrels.item.TieredBlockItem;

public class BulkBarrelBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Tier> TIER = EnumProperty.create("tier", Tier.class);

    public BulkBarrelBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(TIER, Tier.WOODEN)
        );
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TIER);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        var item = blockPlaceContext.getItemInHand().getItem();
        var tier = Tier.WOODEN;

        if (item instanceof TieredBlockItem tieredBlockItem) {
            tier = tieredBlockItem.getTier();
        }

        return this.defaultBlockState()
                .setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite())
                .setValue(TIER, tier);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity baseEntity = level.getBlockEntity(blockPos);
        if (!(baseEntity instanceof BulkBarrelBlockEntity entity)) {
            return InteractionResult.FAIL;
        }

        if (!itemStack.isEmpty()) {
            if (itemStack.getItem() instanceof TierUpgradeItem upgrade) {
                doUpgrade(upgrade, blockState, entity, level, blockPos, itemStack);
            } else {
                entity.storeItems(itemStack);
            }
        } else {
            // TODO: Determine how we want to take all items of type from the player inventory
            ItemStack removedItems = entity.takeItems(player.isShiftKeyDown() ? 1 : 64);
            player.getInventory().placeItemBackInInventory(removedItems);
        }

        return InteractionResult.SUCCESS;
    }

    private void doUpgrade(TierUpgradeItem upgrade, BlockState blockState, BulkBarrelBlockEntity entity, Level level, BlockPos blockPos, ItemStack itemStack) {
        var currentTier = blockState.getValueOrElse(TIER, Tier.WOODEN);
        if (!upgrade.isUsableOnTier(currentTier)) {
            return;
        }

        if (entity.upgradeTier(upgrade.getTargetTier())) {
            level.setBlock(blockPos, blockState.setValue(TIER, upgrade.getTargetTier()), Block.UPDATE_ALL);
            itemStack.shrink(1);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity baseEntity = level.getBlockEntity(blockPos);
        if (!(baseEntity instanceof BulkBarrelBlockEntity entity)) {
            return InteractionResult.FAIL;
        }

        ItemStack removedItems = entity.takeItems(player.isShiftKeyDown() ? 1 : 64);
        ItemEntity droppedItemEntity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), removedItems);
        level.addFreshEntity(droppedItemEntity);

        return InteractionResult.SUCCESS;
    }
}
