package uk.willtc.mcmods.bulkbarrels.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
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
        var stack = new ItemStack(Items.DIAMOND, 32);
        entity.setItem(0, stack);

        assertEquals(stack, entity.getItem(0));
    }

    @Test
    void getContainedItemShouldReturnAirWhenBarrelIsEmpty() {
        assertEquals(Items.AIR, entity.getContainedItem());
    }

    @Test
    void getContainedItemShouldReturnItemWhenBarrelHasItems() {
        var item = Items.DIAMOND;
        entity.setItem(0, new ItemStack(item, 12));

        assertEquals(item, entity.getContainedItem());
    }

    @Test
    void canPlaceItemShouldReturnTrueWhenBarrelIsEmpty() {
        assertTrue(entity.canPlaceItem(0, new ItemStack(Items.DIAMOND, 1)));
    }

    @Test
    void canPlaceItemShouldReturnFalseWhenItemDoesNotMatchWhatIsInBarrel() {
        entity.setItem(1, new ItemStack(Items.COAL, 1));
        assertFalse(entity.canPlaceItem(0, new ItemStack(Items.DIAMOND, 1)));
    }

    @Test
    void canPlaceItemShouldReturnTrueWhenItemMatchesWhatIsInBarrel() {
        entity.setItem(1, new ItemStack(Items.DIAMOND, 1));
        assertTrue(entity.canPlaceItem(0, new ItemStack(Items.DIAMOND, 1)));
    }

    @Test
    void setItemShouldNotAddItemWhenItemDoesNotMatchWhatIsInBarrel() {
        entity.setItem(0, new ItemStack(Items.COAL, 1));

        entity.setItem(1, new ItemStack(Items.DIAMOND, 1));

        assertEquals(ItemStack.EMPTY, entity.getItem(1));
    }

    @Test
    void setItemShouldEmptySlotWhenItemStackIsEmpty() {
        entity.setItem(0, new ItemStack(Items.COAL, 1));
        entity.setItem(1, new ItemStack(Items.COAL, 1));

        entity.setItem(1, ItemStack.EMPTY);

        assertEquals(ItemStack.EMPTY, entity.getItem(1));
    }

    @Test
    void isEmptyShouldReturnTrueWhenBarrelIsEmpty() {
        assertTrue(entity.isEmpty());
    }

    @Test
    void isEmptyShouldReturnFalseWhenBarrelHasItems() {
        entity.setItem(0, new ItemStack(Items.DIAMOND, 32));
        assertFalse(entity.isEmpty());
    }

    @Test
    void clearContentShouldEmptySlotsWhenBarrelHasItems() {
        entity.setItem(0, new ItemStack(Items.DIAMOND, 32));
        entity.setItem(1, new ItemStack(Items.DIAMOND, 16));
        entity.clearContent();
        for (int i = 0; i < entity.getContainerSize(); i++) {
            assertEquals(ItemStack.EMPTY, entity.getItem(i));
        }
    }

    @Test
    void removeItemNoUpdateShouldRemoveStackFromSlotWhenSlotHasItems() {
        var itemStack = new ItemStack(Items.DIAMOND, 32);
        entity.setItem(0, itemStack);

        var returnedStack = entity.removeItemNoUpdate(0);

        assertEquals(itemStack, returnedStack);
        assertEquals(ItemStack.EMPTY, entity.getItem(0));
    }

    @Test
    void removeItemShouldRemoveItemsFromStackWhenSlotHasItems() {
        entity.setItem(0, new ItemStack(Items.DIAMOND, 32));

        var returnedStack = entity.removeItem(0, 8);

        assertItemStackMatches(new ItemStack(Items.DIAMOND, 8), returnedStack);
        assertItemStackMatches(new ItemStack(Items.DIAMOND, 24), entity.getItem(0));
    }
}
