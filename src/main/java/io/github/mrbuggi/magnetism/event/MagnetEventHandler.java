package io.github.mrbuggi.magnetism.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import io.github.mrbuggi.magnetism.magnet.FallingMagnetManager;

public final class MagnetEventHandler {

    public static void register(IEventBus bus) {
        bus.addListener(MagnetEventHandler::onEntityJoin);
        bus.addListener(MagnetEventHandler::onEntityLeave);
        bus.addListener(MagnetEventHandler::onLevelUnload);
        bus.addListener(MagnetEventHandler::onLevelTick);
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity() instanceof FallingBlockEntity falling && FallingMagnetManager.isMagnet(falling)) {
            FallingMagnetManager.add(event.getLevel(), falling);
        }
    }

    private static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity() instanceof FallingBlockEntity falling) {
            FallingMagnetManager.remove(event.getLevel(), falling);
        }
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            FallingMagnetManager.clear(serverLevel);
        }
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            FallingMagnetManager.tick(serverLevel);
        }
    }

    private MagnetEventHandler() {
    }
}
