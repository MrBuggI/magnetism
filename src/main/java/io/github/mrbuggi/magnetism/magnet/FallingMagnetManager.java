package io.github.mrbuggi.magnetism.magnet;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import io.github.mrbuggi.magnetism.block.MagnetBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FallingMagnetManager {

    private record Entry(FallingBlockEntity entity, Direction facing, ItemMagnetCache cache) {
    }

    private static final Map<ResourceKey<Level>, List<Entry>> ACTIVE = new HashMap<>();

    public static void add(Level level, FallingBlockEntity entity) {
        Direction facing = facingOf(entity.getBlockState());
        if (facing == null) {
            return;
        }
        ACTIVE.computeIfAbsent(level.dimension(), k -> new ArrayList<>())
                .add(new Entry(entity, facing, new ItemMagnetCache()));
    }

    public static void remove(Level level, FallingBlockEntity entity) {
        List<Entry> list = ACTIVE.get(level.dimension());
        if (list == null) {
            return;
        }
        list.removeIf(e -> e.entity() == entity);
        if (list.isEmpty()) {
            ACTIVE.remove(level.dimension());
        }
    }

    public static void clear(Level level) {
        ACTIVE.remove(level.dimension());
    }

    public static boolean isMagnet(FallingBlockEntity entity) {
        return entity.getBlockState().getBlock() instanceof MagnetBlock;
    }

    private static Direction facingOf(BlockState state) {
        return state.hasProperty(MagnetBlock.FACING) ? state.getValue(MagnetBlock.FACING) : null;
    }


    private static Vec3 centerOf(FallingBlockEntity entity) {
        return entity.getBoundingBox().getCenter();
    }


    public static void tick(ServerLevel level) {
        List<Entry> list = ACTIVE.get(level.dimension());
        if (list == null || list.isEmpty()) {
            return;
        }

        list.removeIf(e -> e.entity().isRemoved());
        int n = list.size();
        if (n == 0) {
            ACTIVE.remove(level.dimension());
            return;
        }

        boolean sync = (level.getGameTime() & MagnetSettings.SYNC_MASK) == 0L;


        for (int i = 0; i < n; i++) {
            Entry e = list.get(i);
            MagnetForces.attractItems(level, centerOf(e.entity()), e.facing(), e.cache());
        }


        if (n < 2) {
            return;
        }
        for (int i = 0; i < n; i++) {
            Entry a = list.get(i);
            Vec3 ac = centerOf(a.entity());
            for (int j = i + 1; j < n; j++) {
                Entry b = list.get(j);
                Vec3 bc = centerOf(b.entity());
                if (ac.distanceToSqr(bc) > MagnetSettings.magnetRadiusSqr()) {
                    continue;
                }
                Vec3 force = MagnetForces.dipoleForce(ac, a.facing(), bc, b.facing());
                a.entity().setDeltaMovement(a.entity().getDeltaMovement().add(force));
                b.entity().setDeltaMovement(b.entity().getDeltaMovement().subtract(force));
                if (sync) {
                    a.entity().hasImpulse = true;
                    b.entity().hasImpulse = true;
                }
            }
        }
    }


    public static void pushFalling(ServerLevel level, Vec3 center, Direction facing, boolean sync) {
        List<Entry> list = ACTIVE.get(level.dimension());
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0, n = list.size(); i < n; i++) {
            Entry e = list.get(i);
            if (e.entity().isRemoved()) {
                continue;
            }
            Vec3 ec = centerOf(e.entity());
            if (ec.distanceToSqr(center) > MagnetSettings.magnetRadiusSqr()) {
                continue;
            }
            Vec3 force = MagnetForces.dipoleForce(ec, e.facing(), center, facing);
            e.entity().setDeltaMovement(e.entity().getDeltaMovement().add(force));
            if (sync) {
                e.entity().hasImpulse = true;
            }
        }
    }

    private FallingMagnetManager() {
    }
}
