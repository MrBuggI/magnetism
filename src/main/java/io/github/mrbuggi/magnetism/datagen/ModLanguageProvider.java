package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import io.github.mrbuggi.magnetism.Magnetism;
import io.github.mrbuggi.magnetism.registry.ModBlocks;

final class ModLanguageProvider {

    static final class English extends LanguageProvider {
        English(PackOutput output) {
            super(output, Magnetism.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addBlock(ModBlocks.MAGNET_BLOCK, "Magnet Block");
        }
    }

    static final class Russian extends LanguageProvider {
        Russian(PackOutput output) {
            super(output, Magnetism.MOD_ID, "ru_ru");
        }

        @Override
        protected void addTranslations() {
            addBlock(ModBlocks.MAGNET_BLOCK, "Магнитный блок");
        }
    }

    private ModLanguageProvider() {
    }
}
