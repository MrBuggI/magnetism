package ru.buggi.magnetism.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.buggi.magnetism.Magnetism;
import ru.buggi.magnetism.block.MagnetBlock;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Magnetism.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Magnetism.MOD_ID);

    public static final DeferredBlock<MagnetBlock> MAGNET_BLOCK = BLOCKS.registerBlock(
            "magnet_block",
            MagnetBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredItem<BlockItem> MAGNET_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(MAGNET_BLOCK);

    private ModBlocks() {
    }
}
