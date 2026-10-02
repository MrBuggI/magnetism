package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import io.github.mrbuggi.magnetism.Magnetism;
import io.github.mrbuggi.magnetism.block.MagnetBlock;
import io.github.mrbuggi.magnetism.registry.ModBlocks;

final class ModBlockStateProvider extends BlockStateProvider {

    ModBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, Magnetism.MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Модель смотрит на север: северная грань - полюс, южная - железное основание.
        ModelFile model = models()
                .cube("magnet_block",
                        mcLoc("block/lodestone_side"),
                        mcLoc("block/lodestone_side"),
                        mcLoc("block/lodestone_top"),
                        mcLoc("block/iron_block"),
                        mcLoc("block/lodestone_side"),
                        mcLoc("block/lodestone_side"))
                .texture("particle", mcLoc("block/lodestone_side"));

        getVariantBuilder(ModBlocks.MAGNET_BLOCK.get()).forAllStates(state -> {
            Direction facing = state.getValue(MagnetBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(rotationX(facing))
                    .rotationY(rotationY(facing))
                    .build();
        });

        simpleBlockItem(ModBlocks.MAGNET_BLOCK.get(), model);
    }

    private static int rotationX(Direction facing) {
        return switch (facing) {
            case DOWN -> 90;
            case UP -> 270;
            default -> 0;
        };
    }

    private static int rotationY(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
