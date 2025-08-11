package uk.willtc.mcmods.bulkbarrels;

import com.mojang.serialization.Codec;
import net.minecraft.util.ARGB;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public enum Tier implements StringRepresentable {
    WOODEN("wooden", 64, Items.OAK_PLANKS, false, Colors.WHITE),
    STONE("stone", 128, Items.STONE, false, Colors.WHITE),
    IRON("iron", 256, Items.IRON_INGOT, false, Colors.BLACK),
    GOLD("golden", 512, Items.GOLD_INGOT, false, Colors.BLACK),
    DIAMOND("diamond", 1024, Items.DIAMOND, false, Colors.BLACK),
    OBSIDIAN("obsidian", 2048, Items.OBSIDIAN, true, Colors.WHITE);


    public static final Codec<Tier> CODEC = StringRepresentable.fromEnum(Tier::values);

    public final int slots;
    public final String name;
    public final Item craftItem;
    public final boolean isBlastResistant;
    public final int textColor;

    Tier(String name, int slots, Item craftItem, boolean isBlastResistant, int textColor) {
        this.name = name;
        this.slots = slots;
        this.craftItem = craftItem;
        this.isBlastResistant = isBlastResistant;
        this.textColor = textColor;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
