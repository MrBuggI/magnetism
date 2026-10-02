package io.github.mrbuggi.magnetism.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import io.github.mrbuggi.magnetism.Magnetism;
import io.github.mrbuggi.magnetism.block.MagnetBlockEntity;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Magnetism.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagnetBlockEntity>> MAGNET =
            BLOCK_ENTITIES.register("magnet", () ->
                    BlockEntityType.Builder.of(MagnetBlockEntity::new, ModBlocks.MAGNET_BLOCK.get()).build(null));

    private ModBlockEntities() {
    }
}
