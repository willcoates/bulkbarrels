package uk.willtc.mcmods.bulkbarrels;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public enum Tier implements StringRepresentable {
    WOODEN("wooden", 64, Items.OAK_PLANKS, false),
    STONE("stone", 128, Items.STONE, false),
    IRON("iron", 256, Items.IRON_INGOT, false),
    GOLD("golden", 512, Items.GOLD_INGOT, false),
    DIAMOND("diamond", 1024, Items.DIAMOND, false),
    OBSIDIAN("obsidian", 2048, Items.OBSIDIAN, true);

    public static final Codec<Tier> CODEC = StringRepresentable.fromEnum(Tier::values);

    public final int slots;
    public final String name;
    public final Item craftItem;
    public final boolean isBlastResistant;

    Tier(String name, int slots, Item craftItem, boolean isBlastResistant) {
        this.name = name;
        this.slots = slots;
        this.craftItem = craftItem;
        this.isBlastResistant = isBlastResistant;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
