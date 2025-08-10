package uk.willtc.mcmods.bulkbarrels;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkBarrels implements ModInitializer {
    public static final String MOD_ID = "bulkbarrels";
    public static final String MOD_NAME = "Bulk Barrels";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BulkBarrelsBlocks.initialize();
        BulkBarrelsBlockEntities.initialize();
        BulkBarrelsItems.initialize();
    }
}
