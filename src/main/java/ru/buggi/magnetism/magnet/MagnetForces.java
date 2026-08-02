package ru.buggi.magnetism.magnet;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;


public final class MagnetForces {

    public static Vec3 pole(Vec3 center, Direction facing, boolean north) {
        double s = north ? MagnetSettings.POLE_OFFSET : -MagnetSettings.POLE_OFFSET;
        return center.add(facing.getStepX() * s, facing.getStepY() * s, facing.getStepZ() * s);
    }

    public static Vec3 dipoleForce(Vec3 aCenter, Direction aFacing, Vec3 bCenter, Direction bFacing) {
        Vec3 total = Vec3.ZERO;
        for (int i = 0; i < 2; i++) {
            boolean aNorth = i == 0;
            Vec3 pa = pole(aCenter, aFacing, aNorth);
            double qa = aNorth ? 1.0 : -1.0;
            for (int j = 0; j < 2; j++) {
                boolean bNorth = j == 0;
                Vec3 pb = pole(bCenter, bFacing, bNorth);
                double qb = bNorth ? 1.0 : -1.0;

                Vec3 d = pa.subtract(pb);
                double len2 = Math.max(d.lengthSqr(), MagnetSettings.MIN_DIST_SQR);
                double scalar = (qa * qb * MagnetSettings.MAGNET_FORCE) / (len2 * Math.sqrt(len2));
                total = total.add(d.scale(scalar));
            }
        }
        return clamp(total, MagnetSettings.MAGNET_MAX_STEP);
    }

    public static Vec3 clamp(Vec3 v, double max) {
        double len2 = v.lengthSqr();
        if (len2 <= max * max || len2 < 1.0E-12) {
            return v;
        }
        return v.scale(max / Math.sqrt(len2));
    }

    public static void attractItems(ServerLevel level, Vec3 center, Direction facing, ItemMagnetCache cache) {
        cache.tick(level, center);
        int[] ids = cache.ids();
        if (ids.length == 0) {
            return;
        }
        boolean sync = (level.getGameTime() & MagnetSettings.SYNC_MASK) == 0L;
        int alive = 0;
        for (int id : ids) {
            Entity entity = level.getEntity(id);
            if (!(entity instanceof ItemEntity item) || !item.isAlive()) {
                continue;
            }
            if (pull(item, center, facing, sync)) {
                alive++;
            }
        }
        if (alive == 0) {
            cache.invalidate();
        }
    }

    private static boolean pull(ItemEntity item, Vec3 center, Direction facing, boolean sync) {
        Vec3 itemPos = item.position();
        Vec3 north = pole(center, facing, true);
        Vec3 south = pole(center, facing, false);
        Vec3 target = north.distanceToSqr(itemPos) <= south.distanceToSqr(itemPos) ? north : south;

        Vec3 delta = target.subtract(itemPos);
        double dist2 = delta.lengthSqr();
        if (dist2 > MagnetSettings.ITEM_RADIUS_SQR) {
            return false;
        }
        if (dist2 < 1.0E-4) {
            return true;
        }

        double soft = Math.max(dist2, MagnetSettings.MIN_DIST_SQR);
        double scalar = MagnetSettings.ITEM_PULL / (soft * Math.sqrt(soft));
        Vec3 impulse = clamp(delta.scale(scalar), MagnetSettings.ITEM_MAX_STEP);

        Vec3 motion = item.getDeltaMovement().add(impulse);
        if (dist2 < MagnetSettings.ITEM_DAMP_DIST_SQR) {
            motion = motion.scale(MagnetSettings.ITEM_DAMP);
        }
        item.setDeltaMovement(motion);
        if (sync) {
            item.hasImpulse = true;
        }
        return true;
    }

    private MagnetForces() {
    }
}
