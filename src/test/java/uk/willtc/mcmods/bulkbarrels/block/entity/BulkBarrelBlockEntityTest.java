package uk.willtc.mcmods.bulkbarrels.block.entity;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlocks;
import uk.willtc.mcmods.bulkbarrels.Tier;
import uk.willtc.mcmods.bulkbarrels.block.BulkBarrelBlock;

import static org.junit.jupiter.api.Assertions.*;
import static uk.willtc.mcmods.bulkbarrels.ItemStackAssertions.assertItemStackMatches;

public class BulkBarrelBlockEntityTest {
    @BeforeAll
    static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    BulkBarrelBlockEntity createBarrel() {
        return createBarrel(Tier.WOODEN);
    }

    BulkBarrelBlockEntity createBarrel(Tier tier) {
        var properties = new Reference2ObjectArrayMap<Property<?>, Comparable<?>>();
        properties.put(BulkBarrelBlock.TIER, tier);
        properties.put(BulkBarrelBlock.FACING, Direction.NORTH);
        var blockState = new BlockState(BulkBarrelsBlocks.BULK_BARREL, properties, null);
        return new BulkBarrelBlockEntity(new BlockPos(0, 0, 0), blockState);
    }

    @Test
    void getItemShouldReturnEmptyWhenSlotIsEmpty() {
        var barrel = createBarrel();
        assertEquals(ItemStack.EMPTY, barrel.getItem(0));
    }

    @Test
    void getItemShouldReturnStackWhenSlotHasItems() {
        var barrel = createBarrel();
        var stack = itemStack(Items.DIAMOND, 32);

        barrel.setItem(0, stack);

        assertItemStackMatches(stack, barrel.getItem(0));
    }

    @Test
    void getContainedItemShouldReturnAirWhenBarrelIsEmpty() {
        var barrel = createBarrel();
        assertEquals(Items.AIR, barrel.getContainedItem());
    }

    @Test
    void getContainedItemShouldReturnItemWhenBarrelHasItems() {
        var barrel = createBarrel();
        var item = Items.DIAMOND;

        barrel.setItem(0, itemStack(item, 12));

        assertEquals(item, barrel.getContainedItem());
    }

    @Test
    void canPlaceItemShouldReturnTrueWhenBarrelIsEmpty() {
        var barrel = createBarrel();
        assertTrue(barrel.canPlaceItem(0, itemStack(Items.DIAMOND, 1)));
    }

    @Test
    void canPlaceItemShouldReturnFalseWhenItemDoesNotMatchWhatIsInBarrel() {
        var barrel = createBarrel();

        barrel.setItem(1, itemStack(Items.COAL, 1));

        assertFalse(barrel.canPlaceItem(0, itemStack(Items.DIAMOND, 1)));
    }

    @Test
    void canPlaceItemShouldReturnTrueWhenItemMatchesWhatIsInBarrel() {
        var barrel = createBarrel();

        barrel.setItem(1, itemStack(Items.DIAMOND, 1));

        assertTrue(barrel.canPlaceItem(0, itemStack(Items.DIAMOND, 1)));
    }

    @Test
    void setItemShouldNotAddItemWhenItemDoesNotMatchWhatIsInBarrel() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.COAL, 1));

        barrel.setItem(1, itemStack(Items.DIAMOND, 1));

        assertItemStackMatches(ItemStack.EMPTY, barrel.getItem(1));
    }

    @Test
    void setItemShouldEmptySlotWhenItemStackIsEmpty() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.COAL, 1));
        barrel.setItem(1, itemStack(Items.COAL, 1));

        barrel.setItem(1, ItemStack.EMPTY);

        assertItemStackMatches(ItemStack.EMPTY, barrel.getItem(1));
    }

    @Test
    void isEmptyShouldReturnTrueWhenBarrelIsEmpty() {
        var barrel = createBarrel();
        assertTrue(barrel.isEmpty());
    }

    @Test
    void isEmptyShouldReturnFalseWhenBarrelHasItems() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.DIAMOND, 32));
        assertFalse(barrel.isEmpty());
    }

    @Test
    void clearContentShouldEmptySlotsWhenBarrelHasItems() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.DIAMOND, 32));
        barrel.setItem(1, itemStack(Items.DIAMOND, 16));

        barrel.clearContent();

        for (int i = 0; i < barrel.getContainerSize(); i++) {
            assertItemStackMatches(ItemStack.EMPTY, barrel.getItem(i));
        }
    }

    @Test
    void removeItemNoUpdateShouldRemoveStackFromSlotWhenSlotHasItems() {
        var barrel = createBarrel();
        var itemStack = itemStack(Items.DIAMOND, 32);
        barrel.setItem(0, itemStack);

        var returnedStack = barrel.removeItemNoUpdate(0);

        assertEquals(itemStack, returnedStack);
        assertEquals(ItemStack.EMPTY, barrel.getItem(0));
    }

    @Test
    void removeItemShouldRemoveItemsFromStackWhenSlotHasItems() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.DIAMOND, 32));

        var returnedStack = barrel.removeItem(0, 8);

        assertItemStackMatches(itemStack(Items.DIAMOND, 8), returnedStack);
        assertItemStackMatches(itemStack(Items.DIAMOND, 24), barrel.getItem(0));
    }

    @Test
    void takeItemsShouldReturnEmptyWhenBarrelIsEmpty() {
        var barrel = createBarrel();
        var removedStack = barrel.takeItems(32);
        assertItemStackMatches(ItemStack.EMPTY, removedStack);
    }

    @Test
    void takeItemsShouldRemoveItemsWhenBarrelHasItems() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.DIAMOND, 10));
        barrel.setItem(1, itemStack(Items.DIAMOND, 12));

        var removedStack = barrel.takeItems(32);

        assertItemStackMatches(itemStack(Items.DIAMOND, 22), removedStack);
        assertItemStackMatches(ItemStack.EMPTY, barrel.getItem(0));
        assertItemStackMatches(ItemStack.EMPTY, barrel.getItem(1));
    }

    @Test
    void takeItemsShouldNotExceedAmountWhenBarrelHasMoreItems() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.DIAMOND, 10));
        barrel.setItem(1, itemStack(Items.DIAMOND, 12));

        var removedStack = barrel.takeItems(16);

        assertItemStackMatches(itemStack(Items.DIAMOND, 16), removedStack);
        assertItemStackMatches(ItemStack.EMPTY, barrel.getItem(0));
        assertItemStackMatches(itemStack(Items.DIAMOND, 6), barrel.getItem(1));
    }

    @Test
    void takeItemsShouldNotExceedMaxStackSizeWhenBarrelHasMultipleStacksOfItems() {
        var barrel = createBarrel();
        for (var i = 0; i < barrel.getContainerSize(); i++) {
            barrel.setItem(i, itemStack(Items.DIAMOND, 32));
        }

        var removedStack = barrel.takeItems(1000000);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), removedStack);
    }

    @Test
    void takeItemsShouldReturnEmptyWhenAmountIsNegative() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStack(Items.DIAMOND, 32));

        var removedStack = barrel.takeItems(-10);

        assertItemStackMatches(ItemStack.EMPTY, removedStack);
    }

    @Test
    void takeItemShouldKeepDataComponentsWhenItemsHaveComponents() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStackWithDamage(Items.DIAMOND, 16, 1));
        barrel.setItem(1, itemStackWithDamage(Items.DIAMOND, 16, 2));
        barrel.setItem(2, itemStackWithDamage(Items.DIAMOND, 16, 1));
        barrel.setItem(3, itemStackWithDamage(Items.DIAMOND, 16, 2));

        var removedStack = barrel.takeItems(64);

        var expected = itemStackWithDamage(Items.DIAMOND, 32, 1);
        assertItemStackMatches(expected, removedStack);
    }

    @Test
    void storeItemsShouldStoreItemsWhenBarrelIsEmpty() {
        var barrel = createBarrel();
        var stack = itemStack(Items.DIAMOND, 64);

        barrel.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), barrel.getItem(0));
        assertItemStackMatches(ItemStack.EMPTY, stack);
    }

    @Test
    void storeItemsShouldFillMultipleSlotsWhenAmountIsMoreThanAStack() {
        var barrel = createBarrel();
        var stack = itemStack(Items.DIAMOND, 100);

        barrel.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), barrel.getItem(0));
        assertItemStackMatches(itemStack(Items.DIAMOND, 36), barrel.getItem(1));
        assertItemStackMatches(ItemStack.EMPTY, stack);
    }

    @Test
    void storeItemsShouldNotStoreItemWhenBarrelContainsDifferentItemType() {
        var barrel = createBarrel();
        var stack = itemStack(Items.DIAMOND, 64);
        barrel.setItem(0, itemStack(Items.COAL, 1));

        barrel.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 64), stack);
        for (var i = 0; i < barrel.getContainerSize(); i++) {
            assertNotEquals(Items.DIAMOND, barrel.getItem(i).getItem());
        }
    }

    @Test
    void storeItemsShouldNotRemoveItemsFromStackWhenTheBarrelIsFull() {
        var barrel = createBarrel();
        var stack = itemStack(Items.DIAMOND, 64);

        for (var i = 0; i < 32; i++) {
            barrel.setItem(i * 2, itemStack(Items.DIAMOND, 64));
            barrel.setItem(i * 2 + 1, itemStack(Items.DIAMOND, 63));
        }

        barrel.storeItems(stack);

        assertItemStackMatches(itemStack(Items.DIAMOND, 32), stack);
    }

    @Test
    void storeItemsShouldKeepComponentsWhenItemHasComponents() {
        var barrel = createBarrel();
        barrel.storeItems(itemStackWithDamage(Items.DIAMOND, 16, 1));

        var storedStack = barrel.getItem(0);
        var expected = itemStackWithDamage(Items.DIAMOND, 16, 1);
        assertItemStackMatches(expected, storedStack);
    }

    @Test
    void storeItemsShouldNotMergeStacksWhenItemsHaveDifferentComponents() {
        var barrel = createBarrel();
        barrel.setItem(0, itemStackWithDamage(Items.DIAMOND, 16, 1));

        barrel.storeItems(itemStackWithDamage(Items.DIAMOND, 16, 2));

        assertItemStackMatches(itemStackWithDamage(Items.DIAMOND, 16, 1), barrel.getItem(0));
        assertItemStackMatches(itemStackWithDamage(Items.DIAMOND, 16, 2), barrel.getItem(1));
    }

    @ParameterizedTest
    @EnumSource(Tier.class)
    void getContainerSizeShouldIncreaseInSizeWhenTierIsHigher(Tier tier) {
        var barrel = createBarrel(tier);

        var size = barrel.getContainerSize();

        assertEquals(tier.slots, size);
    }

    private static @NotNull ItemStack itemStack(Item item, int count) {
        return new ItemStack(item, count);
    }

    private static @NotNull ItemStack itemStackWithDamage(Item item, int quantity, int damage) {
        return new ItemStack(Holder.direct(item), quantity, DataComponentPatch.builder().set(DataComponents.DAMAGE, damage).build());
    }
}
