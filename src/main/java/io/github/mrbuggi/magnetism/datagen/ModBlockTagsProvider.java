package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import io.github.mrbuggi.magnetism.Magnetism;
import io.github.mrbuggi.magnetism.registry.ModBlocks;

import java.util.concurrent.CompletableFuture;

final class ModBlockTagsProvider extends BlockTagsProvider {

    ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                         ExistingFileHelper fileHelper) {
        super(output, lookup, Magnetism.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Блок требует инструмент для дропа. Без этого тега ни один инструмент
        // не считается подходящим, и в выживании блок не выпадает вообще.
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MAGNET_BLOCK.get());
    }
}
