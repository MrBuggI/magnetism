package ru.buggi.magnetism.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ru.buggi.magnetism.magnet.FallingMagnetManager;
import ru.buggi.magnetism.magnet.ItemMagnetCache;
import ru.buggi.magnetism.magnet.MagnetForces;
import ru.buggi.magnetism.magnet.MagnetSettings;
import ru.buggi.magnetism.registry.ModBlockEntities;

public class MagnetBlockEntity extends BlockEntity {

    private final ItemMagnetCache cache = new ItemMagnetCache();

    public MagnetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGNET.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MagnetBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel) || !state.hasProperty(MagnetBlock.FACING)) {
            return;
        }
        Direction facing = state.getValue(MagnetBlock.FACING);
        Vec3 center = Vec3.atCenterOf(pos);

        MagnetForces.attractItems(serverLevel, center, facing, be.cache);

        boolean sync = (serverLevel.getGameTime() & MagnetSettings.SYNC_MASK) == 0L;
        FallingMagnetManager.pushFalling(serverLevel, center, facing, sync);
    }
}
