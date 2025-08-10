package uk.willtc.mcmods.bulkbarrels.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import uk.willtc.mcmods.bulkbarrels.Tier;

public class TieredBlockItem extends BlockItem {
    public TieredBlockItem(Block block, Tier tier, Properties properties) {
        super(block, properties);
        this.tier = tier;
    }

    private final Tier tier;

    public @NotNull Tier getTier() {
        return tier;
    }
}
