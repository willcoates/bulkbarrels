package uk.willtc.mcmods.bulkbarrels.item;

import net.minecraft.world.item.Item;
import uk.willtc.mcmods.bulkbarrels.Tier;

import java.util.Set;
import java.util.function.Function;

public final class TierUpgradeItem extends Item {
    private TierUpgradeItem(Tier targetTier, Set<Tier> usableTiers, Properties properties) {
        super(properties);
        this.targetTier = targetTier;
        this.usableTiers = usableTiers;
    }

    /**
     * Creates a new factory for creating a barrel tier upgrade item.
     * @param targetTier tier to upgrade the barrel to
     * @param usableTier tiers of barrel the upgrade can be used on
     * @return factory for creating the item
     */
    public static Function<Properties, TierUpgradeItem> builder(Tier targetTier, Tier... usableTier) {
        return properties -> new TierUpgradeItem(targetTier, Set.of(usableTier), properties);
    }

    private final Tier targetTier;
    private final Set<Tier> usableTiers;

    /**
     * Gets the tier this item will upgrade to.
     * @return the tier to upgrade to
     */
    public Tier getTargetTier() {
        return targetTier;
    }

    /**
     * Determines if this upgrade can be used on the provided tier of barrel.
     * @param tier the current tier of the barrel
     * @return {@code true} if the item can be used on the provided tier of barrel; {@code false} otherwise
     */
    public boolean isUsableOnTier(Tier tier) {
        return usableTiers.contains(tier);
    }
}
