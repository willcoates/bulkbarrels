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

    /**
     * Returns the item contained inside the barrel, or {@link Items#AIR} if the barrel is empty.
     * @return the contained item type, or {@code AIR} if no items are present
     */
    public Item getContainedItem() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) {
                return itemStack.getItem();
            }
        }

        return Items.AIR;
    }

    /**
     * Removes up to {@code amount} items from the container, returning the ItemStack containing removed items. The
     * returned stack size will not exceed the maximum stack size for the item.
     * @param amount Maximum number of items to remove.
     * @return An ItemStack items removed from the container, or {@link ItemStack#EMPTY} if none were removed.
     */
    public ItemStack takeItems(int amount) {
        ItemStack removed = ItemStack.EMPTY;

        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        var containedItem = getContainedItem();

        if (containedItem == Items.AIR) {
            return ItemStack.EMPTY;
        }

        amount = Math.min(amount, containedItem.getDefaultMaxStackSize());

        for (int i = 0; i < inventory.size() && amount > 0; i++) {
            var containedStack = inventory.get(i);
            if (containedStack.isEmpty()) {
                continue;
            }

            var toRemove = Math.min(amount, containedStack.getCount());

            if (removed.isEmpty()) {
                removed = containedStack.split(toRemove);
            } else {
                containedStack.shrink(toRemove);
                removed.grow(toRemove);
            }

            amount -= toRemove;
        }

        if (!removed.isEmpty()) {
            setChanged();
        }

        return removed;
    }

    /**
     * Adds items from the given {@code toStore} stack into the barrel, if the barrel currently stores
     * the same item or is empty. Removes items from {@code toStore} as they are stored.
     * @param toStore The ItemStack containing items to add to the barrel.
     */
    public void storeItems(ItemStack toStore) {
        if (toStore.isEmpty()) {
            return;
        }

        var containedItem = getContainedItem();
        var dirty = false;

        if (!toStore.is(containedItem) && containedItem != Items.AIR) {
            return;
        }

        for (var i = 0; i < inventory.size() && !toStore.isEmpty(); i++) {
            var containedStack = inventory.get(i);

            if (containedStack.isEmpty()) {
                // Don't trust whoever is giving us items, assume someone will give us more than a stack of items eventually...
                var toTransfer = Math.min(toStore.getCount(), toStore.getMaxStackSize());
                containedStack = new ItemStack(toStore.getItem(), toTransfer);
                toStore.shrink(toTransfer);
                inventory.set(i, containedStack);
                dirty = true;
            } else {
                var toTransfer = Math.min(toStore.getCount(), containedStack.getMaxStackSize() - containedStack.getCount());
                toStore.shrink(toTransfer);
                containedStack.grow(toTransfer);
                dirty = true;
            }
        }

        if (dirty) {
            setChanged();
        }
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
