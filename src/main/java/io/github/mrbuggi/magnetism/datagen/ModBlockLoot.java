package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import io.github.mrbuggi.magnetism.registry.ModBlocks;

import java.util.Set;

final class ModBlockLoot extends BlockLootSubProvider {

    ModBlockLoot(HolderLookup.Provider lookup) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.MAGNET_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value).map(Block.class::cast).toList();
    }
}
