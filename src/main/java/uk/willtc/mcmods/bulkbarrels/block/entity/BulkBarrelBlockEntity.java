package uk.willtc.mcmods.bulkbarrels.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.willtc.mcmods.bulkbarrels.BulkBarrels;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlockEntities;
import uk.willtc.mcmods.bulkbarrels.Tier;
import uk.willtc.mcmods.bulkbarrels.block.BulkBarrelBlock;

import java.util.Collections;

public class BulkBarrelBlockEntity extends BlockEntity implements Container {
    public BulkBarrelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BulkBarrelsBlockEntities.BULK_BARREL_BLOCK_ENTITY, blockPos, blockState);

        tier = blockState.getOptionalValue(BulkBarrelBlock.TIER).orElse(Tier.WOODEN);
        inventory = NonNullList.withSize(tier.slots, ItemStack.EMPTY);
    }

    private Tier tier;
    private NonNullList<ItemStack> inventory;

    private Item containedItem = Items.AIR;
    private int itemCount = 0;

    /**
     * Returns the item contained inside the barrel, or {@link Items#AIR} if the barrel is empty.
     * @return the contained item type, or {@code AIR} if no items are present
     */
    public Item getContainedItem() {
        if (level != null && level.isClientSide) {
            return containedItem;
        }

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
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        var containedItem = getContainedItem();
        if (containedItem == Items.AIR) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = ItemStack.EMPTY;
        amount = Math.min(amount, containedItem.getDefaultMaxStackSize());

        for (int i = 0; i < inventory.size() && amount > 0; i++) {
            var containedStack = inventory.get(i);
            if (containedStack.isEmpty()) {
                continue;
            }

            var toRemove = Math.min(amount, containedStack.getCount());

            if (removed.isEmpty()) {
                removed = containedStack.split(toRemove);
            } else if (ItemStack.isSameItemSameComponents(containedStack, removed)) {
                containedStack.shrink(toRemove);
                removed.grow(toRemove);
            } else {
                continue;
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
                containedStack = toStore.split(toTransfer);
                inventory.set(i, containedStack);
                dirty = true;
            } else if (ItemStack.isSameItemSameComponents(toStore, containedStack)) {
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

    /**
     * Attempts to upgrade the barrel's tier, increasing its capacity.
     *
     * @param newTier the tier to upgrade to
     * @return {@code true} if the barrel was successfully upgraded; {@code false} otherwise
     */
    public boolean upgradeTier(Tier newTier) {
        if (tier == newTier) {
            BulkBarrels.LOGGER.warn("Detected new tier {} is same as old tier.", newTier);
            return false;
        }
        if (newTier.slots < tier.slots) {
            BulkBarrels.LOGGER.warn("Detected new tier {} is worse than old tier {}, cowarding out!", newTier, tier);
            return false;
        }
        if (newTier.slots < inventory.size()) {
            BulkBarrels.LOGGER.warn("Detected negative capacity change when upgrading from {} to {}, cowarding out!", tier, newTier);
            return false;
        }

        tier = newTier;

        var newInventory = NonNullList.withSize(newTier.slots, ItemStack.EMPTY);
        Collections.copy(newInventory, inventory);
        inventory = newInventory;
        setChanged();

        return true;
    }

    /**
     * Returns the total number of items stored in the barrel.
     *
     * @return the total count of items in the barrel's inventory
     */
    public int getItemCount() {
        if (level != null && level.isClientSide) {
            return itemCount;
        }

        int sum = 0;
        for (ItemStack itemStack : inventory) {
            sum += itemStack.getCount();
        }
        return sum;
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
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
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        ContainerHelper.saveAllItems(valueOutput, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        var itemCount = valueInput.getInt("itemCount");
        var containedItem  = valueInput.getString("containedItem");
        if (itemCount.isPresent() && containedItem.isPresent()) {
            this.itemCount = itemCount.get();
            this.containedItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(containedItem.get()));
            return;
        }

        inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(valueInput, inventory);
    }

    // I don't like this, but it works for now
    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), BulkBarrelBlock.UPDATE_CLIENTS);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var compound = new CompoundTag();
        compound.putInt("itemCount", getItemCount());
        compound.putString("containedItem", getContainedItem().toString());
        return compound;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
