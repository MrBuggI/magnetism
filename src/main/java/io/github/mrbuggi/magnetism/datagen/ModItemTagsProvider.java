package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import io.github.mrbuggi.magnetism.Magnetism;
import io.github.mrbuggi.magnetism.registry.ModBlocks;
import io.github.mrbuggi.magnetism.registry.ModTags;

import java.util.concurrent.CompletableFuture;

final class ModItemTagsProvider extends ItemTagsProvider {

    ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                        CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper fileHelper) {
        super(output, lookup, blockTags, Magnetism.MOD_ID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var magnetic = tag(ModTags.MAGNETIC);

        // Сырьё
        magnetic.add(Items.IRON_INGOT, Items.IRON_NUGGET, Items.RAW_IRON, Items.IRON_BLOCK, Items.RAW_IRON_BLOCK);

        // Инструменты и броня
        magnetic.add(Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_HOE);
        magnetic.add(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
                Items.IRON_HORSE_ARMOR);
        magnetic.add(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS,
                Items.CHAINMAIL_BOOTS);
        magnetic.add(Items.SHEARS, Items.SHIELD, Items.FLINT_AND_STEEL, Items.COMPASS);
        magnetic.add(Items.BUCKET, Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.MILK_BUCKET,
                Items.POWDER_SNOW_BUCKET);

        // Блоки и механизмы из железа
        magnetic.add(Items.IRON_DOOR, Items.IRON_TRAPDOOR, Items.IRON_BARS, Items.CHAIN);
        magnetic.add(Items.LANTERN, Items.SOUL_LANTERN, Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.TRIPWIRE_HOOK);
        magnetic.add(Items.HOPPER, Items.CAULDRON, Items.ANVIL, Items.CHIPPED_ANVIL, Items.DAMAGED_ANVIL);
        magnetic.add(Items.BLAST_FURNACE, Items.PISTON, Items.STICKY_PISTON);
        magnetic.add(Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL);
        magnetic.add(Items.MINECART, Items.CHEST_MINECART, Items.FURNACE_MINECART, Items.HOPPER_MINECART,
                Items.TNT_MINECART);

        // Магниты притягивают друг друга и как предметы
        magnetic.add(ModBlocks.MAGNET_BLOCK_ITEM.get());

        // Общие теги: железо и сталь из других модов притягиваются без правки этого мода.
        // Ссылки необязательные: тег стали существует не в каждой сборке.
        magnetic.addOptionalTag(common("ingots/iron"))
                .addOptionalTag(common("nuggets/iron"))
                .addOptionalTag(common("raw_materials/iron"))
                .addOptionalTag(common("storage_blocks/iron"))
                .addOptionalTag(common("ingots/steel"))
                .addOptionalTag(common("storage_blocks/steel"));
    }

    private static ResourceLocation common(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }
}
