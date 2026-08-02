package ru.buggi.magnetism;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import ru.buggi.magnetism.event.MagnetEventHandler;
import ru.buggi.magnetism.registry.ModBlockEntities;
import ru.buggi.magnetism.registry.ModBlocks;

@Mod(Magnetism.MOD_ID)
public final class Magnetism {

    public static final String MOD_ID = "magnetism";

    public Magnetism(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);

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
