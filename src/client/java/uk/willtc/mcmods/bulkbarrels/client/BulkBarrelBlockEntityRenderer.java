package uk.willtc.mcmods.bulkbarrels.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import uk.willtc.mcmods.bulkbarrels.Tier;
import uk.willtc.mcmods.bulkbarrels.block.BulkBarrelBlock;
import uk.willtc.mcmods.bulkbarrels.block.entity.BulkBarrelBlockEntity;

/**
 * Renders the barrel's contents on the front facing side of the block.
 */
public class BulkBarrelBlockEntityRenderer implements BlockEntityRenderer<BulkBarrelBlockEntity> {
    private final Font font;
    private final ItemRenderer itemRenderer;

    public BulkBarrelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.font = context.getFont();
    }

    @Override
    public void render(BulkBarrelBlockEntity blockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay, Vec3 vec3) {
        poseStack.pushPose();
        var facing = blockEntity.getBlockState().getValueOrElse(BulkBarrelBlock.FACING, Direction.NORTH);
        var tier = blockEntity.getBlockState().getValueOrElse(BulkBarrelBlock.TIER, Tier.WOODEN);
        var level = blockEntity.getLevel();
        var blockPos = blockEntity.getBlockPos();
        int displayLight = LevelRenderer.getLightColor(level, blockPos.relative(facing));
        String count = Integer.toString(blockEntity.getItemCount());

        setupBaseTranslation(poseStack, facing);
        renderItem(blockEntity, poseStack, multiBufferSource, overlay, displayLight);
        setupItemCountTranslation(poseStack);
        renderItemCount(poseStack, tier.textColor, multiBufferSource, count, displayLight);

        poseStack.popPose();
    }

    private static void setupBaseTranslation(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH:
                poseStack.translate(0.5, 0.5, 0.0);
                break;
            case SOUTH:
                poseStack.translate(0.5, 0.5, 1.0);
                break;
            case WEST:
                poseStack.translate(0.0, 0.5, 0.5);
                break;
            case EAST:
                poseStack.translate(1.0, 0.5, 0.5);
                break;
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
    }

    private void renderItem(BulkBarrelBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int overlay, int displayLight) {
        var item = blockEntity.getContainedItem();

        itemRenderer.renderStatic(new ItemStack(item), ItemDisplayContext.GROUND, displayLight, overlay, poseStack, multiBufferSource, blockEntity.getLevel(), 0);
    }

    private static void setupItemCountTranslation(PoseStack poseStack) {
        poseStack.translate(0.0f, -0.2f, 0.01f);
        poseStack.scale(1.0f/96.0f, -1.0f/96.0f, 1.0f/96.0f);
    }

    private void renderItemCount(PoseStack poseStack, int textColor, MultiBufferSource multiBufferSource, String count, int displayLight) {
        float width = font.width(count);

        font.drawInBatch(count, -width / 2.0f, 0.0f, textColor, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, displayLight);
    }
}
