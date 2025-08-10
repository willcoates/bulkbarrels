package uk.willtc.mcmods.bulkbarrels.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import uk.willtc.mcmods.bulkbarrels.client.datagen.BarrelLootTableGenerator;
import uk.willtc.mcmods.bulkbarrels.client.datagen.BarrelModelGenerator;
import uk.willtc.mcmods.bulkbarrels.client.datagen.BarrelUpgradeRecipeGenerator;

public class BulkBarrelsDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(BarrelModelGenerator::new);
        pack.addProvider(BarrelUpgradeRecipeGenerator::new);
        pack.addProvider(BarrelLootTableGenerator::new);
    }
}
