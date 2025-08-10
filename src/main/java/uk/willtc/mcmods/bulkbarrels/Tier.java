package uk.willtc.mcmods.bulkbarrels;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Tier implements StringRepresentable {
    WOODEN("wooden", 64),
    STONE("stone", 128);

    public final int slots;
    public final String name;

    Tier(String name, int slots) {
        this.name = name;
        this.slots = slots;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
