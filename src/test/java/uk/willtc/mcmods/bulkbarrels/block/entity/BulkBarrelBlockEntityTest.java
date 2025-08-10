package uk.willtc.mcmods.bulkbarrels.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlocks;

import static org.junit.jupiter.api.Assertions.*;
import static uk.willtc.mcmods.bulkbarrels.ItemStackAssertions.assertItemStackMatches;

public class BulkBarrelBlockEntityTest {
    private BulkBarrelBlockEntity entity;

    @BeforeAll
    static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @BeforeEach
    void beforeEach() {
        entity = new BulkBarrelBlockEntity(new BlockPos(0, 0, 0), new BlockState(BulkBarrelsBlocks.BULK_BARREL, null, null));
    }

    @Test
    void getItemShouldReturnEmptyWhenSlotIsEmpty() {
        assertEquals(ItemStack.EMPTY, entity.getItem(0));
    }

    @Test
    void getItemShouldReturnStackWhenSlotHasItems() {
        var stack = itemStack(Items.DIAMOND, 32);
        entity.setItem(0, stack);

        assertItemStackMatches(stack, entity.getItem(0));
    }

    @Test
    void getContainedItemShouldReturnAirWhenBarrelIsEmpty() {
        assertEquals(Items.AIR, entity.getContainedItem());
    }

    @Test
    void getContainedItemShouldReturnItemWhenBarrelHasItems() {
        var item = Items.DIAMOND;
        entity.setItem(0, itemStack(item, 12));

        assertEquals(item, entity.getContainedItem());
    }

    @Test
    void canPlaceItemShouldReturnTrueWhenBarrelIsEmpty() {
        assertTrue(entity.canPlaceItem(0, itemStack(Items.DIAMOND, 1)));
    }

    @Test
    void canPlaceItemShouldReturnFalseWhenItemDoesNotMatchWhatIsInBarrel() {
        entity.setItem(1, itemStack(Items.COAL, 1));
        assertFalse(entity.canPlaceItem(0, itemStack(Items.DIAMOND, 1)));
    }

    @Test
    void canPlaceItemShouldReturnTrueWhenItemMatchesWhatIsInBarrel() {
        entity.setItem(1, itemStack(Items.DIAMOND, 1));
        assertTrue(entity.canPlaceItem(0, itemStack(Items.DIAMOND, 1)));
    }

    @Test
    void setItemShouldNotAddItemWhenItemDoesNotMatchWhatIsInBarrel() {
        entity.setItem(0, itemStack(Items.COAL, 1));

        entity.setItem(1, itemStack(Items.DIAMOND, 1));

        assertItemStackMatches(ItemStack.EMPTY, entity.getItem(1));
    }

    @Test
    void setItemShouldEmptySlotWhenItemStackIsEmpty() {
        entity.setItem(0, itemStack(Items.COAL, 1));
        entity.setItem(1, itemStack(Items.COAL, 1));

        entity.setItem(1, ItemStack.EMPTY);

        assertItemStackMatches(ItemStack.EMPTY, entity.getItem(1));
    }

    @Test
    void isEmptyShouldReturnTrueWhenBarrelIsEmpty() {
        assertTrue(entity.isEmpty());
    }

    @Test
    void isEmptyShouldReturnFalseWhenBarrelHasItems() {
        entity.setItem(0, itemStack(Items.DIAMOND, 32));
        assertFalse(entity.isEmpty());
    }

    @Test
    void clearContentShouldEmptySlotsWhenBarrelHasItems() {
        entity.setItem(0, itemStack(Items.DIAMOND, 32));
        entity.setItem(1, itemStack(Items.DIAMOND, 16));
        entity.clearContent();
        for (int i = 0; i < entity.getContainerSize(); i++) {
            assertItemStackMatches(ItemStack.EMPTY, entity.getItem(i));
        }
    }

    @Test
    void removeItemNoUpdateShouldRemoveStackFromSlotWhenSlotHasItems() {
        var itemStack = itemStack(Items.DIAMOND, 32);
        entity.setItem(0, itemStack);

        var returnedStack = entity.removeItemNoUpdate(0);

        assertEquals(itemStack, returnedStack);
        assertEquals(ItemStack.EMPTY, entity.getItem(0));
    }

    @Test
    void removeItemShouldRemoveItemsFromStackWhenSlotHasItems() {
        entity.setItem(0, itemStack(Items.DIAMOND, 32));

        var returnedStack = entity.removeItem(0, 8);

        assertItemStackMatches(itemStack(Items.DIAMOND, 8), returnedStack);
        assertItemStackMatches(itemStack(Items.DIAMOND, 24), entity.getItem(0));
    }

    @Test
    void takeItemsShouldReturnEmptyWhenBarrelIsEmpty() {
        var removedStack = entity.takeItems(32);
        assertItemStackMatches(ItemStack.EMPTY, removedStack);
    }

    @Test
    void takeItemsShouldRemoveItemsWhenBarrelHasItems() {
        entity.setItem(0, itemStack(Items.DIAMOND, 10));
        entity.setItem(1, itemStack(Items.DIAMOND, 12));

        var removedStack = entity.takeItems(32);

        assertItemStackMatches(itemStack(Items.DIAMOND, 22), removedStack);
        assertItemStackMatches(ItemStack.EMPTY, entity.getItem(0));
        assertItemStackMatches(ItemStack.EMPTY, entity.getItem(1));
    }

    @Test
    void takeItemsShouldNotExceedAmountWhenBarrelHasMoreItems() {
        entity.setItem(0, itemStack(Items.DIAMOND, 10));
        entity.setItem(1, itemStack(Items.DIAMOND, 12));

        var removedStack = entity.takeItems(16);

        assertItemStackMatches(itemStack(Items.DIAMOND, 16), removedStack);
        assertItemStackMatches(ItemStack.EMPTY, entity.getItem(0));
        assertItemStackMatches(itemStack(Items.DIAMOND, 6), entity.getItem(1));
    }

    @Test
    void takeItemsShouldNotExceedMaxStackSizeWhenBarrelHasMultipleStacksOfItems() {
        for (var i = 0; i < entity.getContainerSize(); i++) {
            entity.setItem(i, itemStack(Items.DIAMOND, 32));
        }

        var removedStack = entity.takeItems(1000000);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), removedStack);
    }

    @Test
    void takeItemsShouldReturnEmptyWhenAmountIsNegative() {
        entity.setItem(0, itemStack(Items.DIAMOND, 32));

        var removedStack = entity.takeItems(-10);

        assertItemStackMatches(ItemStack.EMPTY, removedStack);
    }

    @Test
    void takeItemShouldKeepDataComponentsWhenItemsHaveComponents() {
        entity.setItem(0, itemStackWithDamage(Items.DIAMOND, 16, 1));
        entity.setItem(1, itemStackWithDamage(Items.DIAMOND, 16, 2));
        entity.setItem(2, itemStackWithDamage(Items.DIAMOND, 16, 1));
        entity.setItem(3, itemStackWithDamage(Items.DIAMOND, 16, 2));

        var removedStack = entity.takeItems(64);

        var expected = itemStackWithDamage(Items.DIAMOND, 32, 1);
        assertItemStackMatches(expected, removedStack);
    }

    @Test
    void storeItemsShouldStoreItemsWhenBarrelIsEmpty() {
        var stack = itemStack(Items.DIAMOND, 64);

        entity.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), entity.getItem(0));
        assertItemStackMatches(ItemStack.EMPTY, stack);
    }

    @Test
    void storeItemsShouldFillMultipleSlotsWhenAmountIsMoreThanAStack() {
        var stack = itemStack(Items.DIAMOND, 100);

        entity.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), entity.getItem(0));
        assertItemStackMatches(itemStack(Items.DIAMOND, 36), entity.getItem(1));
        assertItemStackMatches(ItemStack.EMPTY, stack);
    }

    @Test
    void storeItemsShouldNotStoreItemWhenBarrelContainsDifferentItemType() {
        var stack = itemStack(Items.DIAMOND, 64);

        entity.setItem(0, itemStack(Items.COAL, 1));

        entity.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), stack);
        for (var i = 0; i < entity.getContainerSize(); i++) {
            assertNotEquals(Items.DIAMOND, entity.getItem(i).getItem());
        }
    }

    @Test
    void storeItemsShouldNotRemoveItemsFromStackWhenTheBarrelIsFull() {
        var stack = itemStack(Items.DIAMOND, 64);

        for (var i = 0; i < 32; i++) {
            entity.setItem(i * 2, itemStack(Items.DIAMOND, 64));
            entity.setItem(i * 2 + 1, itemStack(Items.DIAMOND, 63));
        }

        entity.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 32), stack);
    }

    @Test
    void storeItemsShouldKeepComponentsWhenItemHasComponents() {
        entity.storeItems(itemStackWithDamage(Items.DIAMOND, 16, 1));

        var storedStack = entity.getItem(0);
        var expected = itemStackWithDamage(Items.DIAMOND, 16, 1);
        assertItemStackMatches(expected, storedStack);
    }

    @Test
    void storeItemsShouldNotMergeStacksWhenItemsHaveDifferentComponents() {
        entity.setItem(0, itemStackWithDamage(Items.DIAMOND, 16, 1));

        entity.storeItems(itemStackWithDamage(Items.DIAMOND, 16, 2));

        assertItemStackMatches(itemStackWithDamage(Items.DIAMOND, 16, 1), entity.getItem(0));
        assertItemStackMatches(itemStackWithDamage(Items.DIAMOND, 16, 2), entity.getItem(1));
    }

    private static @NotNull ItemStack itemStack(Item item, int count) {
        return new ItemStack(item, count);
    }

    private static @NotNull ItemStack itemStackWithDamage(Item item, int quantity, int damage) {
        return new ItemStack(Holder.direct(item), quantity, DataComponentPatch.builder().set(DataComponents.DAMAGE, damage).build());
    }
}
