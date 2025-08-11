package uk.willtc.mcmods.bulkbarrels.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlockEntities;

public class BulkBarrelsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(BulkBarrelsBlockEntities.BULK_BARREL_BLOCK_ENTITY, BulkBarrelBlockEntityRenderer::new);
    }
}
