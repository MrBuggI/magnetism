package io.github.mrbuggi.magnetism.magnet;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import io.github.mrbuggi.magnetism.registry.ModTags;

import java.util.List;

public final class ItemMagnetCache {

    private static final int[] EMPTY = new int[0];

    private int[] ids = EMPTY;
    private int cooldown = 0;

    public int[] ids() {
        return ids;
    }

    public void invalidate() {
        ids = EMPTY;
        cooldown = 0;
    }

    public void tick(ServerLevel level, Vec3 center) {
        if (cooldown-- > 0) {
            return;
        }
        rescan(level, center);
    }

    private void rescan(ServerLevel level, Vec3 center) {
        double d = MagnetSettings.ITEM_RADIUS * 2.0;
        AABB box = AABB.ofSize(center, d, d, d);

        List<ItemEntity> found = level.getEntitiesOfClass(
                ItemEntity.class,
                box,
                item -> item.isAlive() && item.getItem().is(ModTags.MAGNETIC)
        );

        if (found.isEmpty()) {
            ids = EMPTY;
            cooldown = MagnetSettings.IDLE_RESCAN_INTERVAL;
            return;
        }

        int count = Math.min(found.size(), MagnetSettings.MAX_TRACKED_ITEMS);
        if (ids.length != count) {
            ids = new int[count];
        }
        for (int i = 0; i < count; i++) {
            ids[i] = found.get(i).getId();
        }
        cooldown = MagnetSettings.RESCAN_INTERVAL;
    }
}
