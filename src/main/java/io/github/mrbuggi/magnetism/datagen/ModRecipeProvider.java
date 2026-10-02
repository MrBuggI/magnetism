package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import io.github.mrbuggi.magnetism.registry.ModBlocks;

import java.util.concurrent.CompletableFuture;

final class ModRecipeProvider extends RecipeProvider {

    ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MAGNET_BLOCK_ITEM.get())
                .pattern("III")
                .pattern("IRI")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_redstone_block", has(Items.REDSTONE_BLOCK))
                .save(output);
    }
}
