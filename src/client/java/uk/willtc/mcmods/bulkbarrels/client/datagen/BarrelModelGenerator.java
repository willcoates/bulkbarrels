package uk.willtc.mcmods.bulkbarrels.client.datagen;

import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import uk.willtc.mcmods.bulkbarrels.BulkBarrels;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsBlocks;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsItems;
import uk.willtc.mcmods.bulkbarrels.Tier;
import uk.willtc.mcmods.bulkbarrels.block.BulkBarrelBlock;

import java.util.Optional;

public class BarrelModelGenerator extends FabricModelProvider {
    public BarrelModelGenerator(FabricDataOutput output) {
        super(output);
    }

    private static final PropertyDispatch<VariantMutator> FACING_MUTATOR = PropertyDispatch.modify(BulkBarrelBlock.FACING)
            .select(Direction.NORTH, v -> v)
            .select(Direction.EAST, v -> v.withYRot(Quadrant.R90))
            .select(Direction.SOUTH, v -> v.withYRot(Quadrant.R180))
            .select(Direction.WEST, v -> v.withYRot(Quadrant.R270));

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        var barrelTemplateResource = ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID,  "block/base_bulk_barrel");
        var barrelTemplate = new ModelTemplate(Optional.of(barrelTemplateResource), Optional.empty(), TextureSlot.PARTICLE, TextureSlot.EDGE, TextureSlot.FRONT, TextureSlot.BACK);

        // Default to wooden, add VariantMutators for each tier
        var blockState = BlockModelGenerators.plainVariant(ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID, "wooden_bulk_barrel"));
        var tierMutator = PropertyDispatch.modify(BulkBarrelBlock.TIER);

        for (Tier tier : Tier.values()) {
            var modelName = "block/" + tier.name + "_bulk_barrel";
            var modelLocation = ResourceLocation.fromNamespaceAndPath(BulkBarrels.MOD_ID, modelName);
            var barrelBlockItem = BulkBarrelsItems.TIERED_BULK_BARRELS.get(tier);

            TextureMapping textureMapping = new TextureMapping()
                    .put(TextureSlot.PARTICLE, ResourceLocation.fromNamespaceAndPath("bulkbarrels", modelName + "_front"))
                    .put(TextureSlot.EDGE, ResourceLocation.fromNamespaceAndPath("bulkbarrels", modelName + "_edge"))
                    .put(TextureSlot.FRONT, ResourceLocation.fromNamespaceAndPath("bulkbarrels", modelName + "_front"))
                    .put(TextureSlot.BACK, ResourceLocation.fromNamespaceAndPath("bulkbarrels", modelName + "_back"));

            barrelTemplate.create(modelLocation, textureMapping, blockStateModelGenerator.modelOutput);
            tierMutator.select(tier, v -> v.withModel(modelLocation));

            if (barrelBlockItem != null) {
                blockStateModelGenerator.registerSimpleItemModel(barrelBlockItem, modelLocation);
            }
        }

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(BulkBarrelsBlocks.BULK_BARREL, blockState).with(FACING_MUTATOR).with(tierMutator));
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

        for (var tier : Tier.values()) {
            var upgradeItem = BulkBarrelsItems.TIERED_UPGRADE_ITEMS.get(tier);
            if (upgradeItem != null) {
                itemModelGenerator.generateFlatItem(upgradeItem, ModelTemplates.FLAT_ITEM);
            }
        }
    }
}
