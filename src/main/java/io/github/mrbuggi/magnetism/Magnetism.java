package io.github.mrbuggi.magnetism;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import io.github.mrbuggi.magnetism.config.MagnetConfig;
import io.github.mrbuggi.magnetism.event.MagnetEventHandler;
import io.github.mrbuggi.magnetism.registry.ModBlockEntities;
import io.github.mrbuggi.magnetism.registry.ModBlocks;

@Mod(Magnetism.MOD_ID)
public final class Magnetism {

    public static final String MOD_ID = "magnetism";

    public Magnetism(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);

        // Серверный конфиг: хранится в папке мира и приходит клиенту с сервера.
        container.registerConfig(ModConfig.Type.SERVER, MagnetConfig.SPEC);
        modBus.addListener(MagnetConfig::onLoad);
        modBus.addListener(MagnetConfig::onReload);

        modBus.addListener(Magnetism::onBuildCreativeTabs);

        MagnetEventHandler.register(NeoForge.EVENT_BUS);
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.MAGNET_BLOCK_ITEM.get());
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
