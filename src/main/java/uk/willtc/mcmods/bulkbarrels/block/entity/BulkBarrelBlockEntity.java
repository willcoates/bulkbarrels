package uk.willtc.mcmods.bulkbarrels.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlockEntities;

public class BulkBarrelBlockEntity extends BlockEntity implements Container {
    private static final int BARREL_SLOTS = 64; // TODO: Load this from an enum in BlockState

    public BulkBarrelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BulkBarrelsBlockEntities.BULK_BARREL_BLOCK_ENTITY, blockPos, blockState);

        inventory = NonNullList.withSize(BARREL_SLOTS, ItemStack.EMPTY);
    }

    private NonNullList<ItemStack> inventory;

    public Item getContainedItem() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) {
                return itemStack.getItem();
            }
        }

        return Items.AIR;
    }

    @Override
    public int getContainerSize() {
        return BARREL_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int count) {
        var removedStack = ContainerHelper.removeItem(inventory, slot, count);
        setChanged();
        return removedStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        var containedItem = getContainedItem();

        if (!itemStack.is(containedItem) && containedItem != Items.AIR && !itemStack.isEmpty()) {
            return;
        }

        inventory.set(slot, itemStack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        var containedItem = getContainedItem();
        return itemStack.is(containedItem) || containedItem == Items.AIR;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(valueInput, inventory);
    }
}
