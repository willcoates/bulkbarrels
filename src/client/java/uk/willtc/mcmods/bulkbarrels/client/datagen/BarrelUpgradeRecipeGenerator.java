package uk.willtc.mcmods.bulkbarrels.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;
import uk.willtc.mcmods.bulkbarrels.BulkBarrelsItems;
import uk.willtc.mcmods.bulkbarrels.Tier;

import java.util.concurrent.CompletableFuture;

public class BarrelUpgradeRecipeGenerator extends FabricRecipeProvider {
    public BarrelUpgradeRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {
                var tiers = Tier.values();
                for (var i = 1; i < tiers.length; i++) {
                    var previousTier = tiers[i - 1];
                    var currentTier = tiers[i];
                    var previousBarrel = BulkBarrelsItems.TIERED_BULK_BARRELS.get(previousTier);
                    var currentBarrel = BulkBarrelsItems.TIERED_BULK_BARRELS.get(currentTier);

                    // Upgrade barrel block
                    shaped(RecipeCategory.REDSTONE, currentBarrel)
                            .pattern("uuu")
                            .pattern("ubu")
                            .pattern("uuu")
                            .define('u', currentTier.craftItem)
                            .define('b', previousBarrel)
                            .unlockedBy("has_previous_barrel", has(previousBarrel))
                            .group("bulk_barrels")
                            .save(recipeOutput);

                    // TODO: Upgrade item
                }
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "Bulk Barrels Upgrade Recipes";
    }
}
