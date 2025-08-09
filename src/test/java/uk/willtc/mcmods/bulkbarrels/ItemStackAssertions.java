package uk.willtc.mcmods.bulkbarrels;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

public class ItemStackAssertions {
    private ItemStackAssertions() {
    }

    public static void assertItemStackMatches(@Nullable ItemStack expected, @Nullable ItemStack actual) {
        if (expected == actual) {
            return;
        }

        if (expected == null || actual == null) {
            assertionFailure().expected(expected).actual(actual).buildAndThrow();
            return;
        }

        if (!ItemStack.matches(expected, actual)) {
            assertionFailure().expected(expected).actual(actual).buildAndThrow();
        }
    }
}
