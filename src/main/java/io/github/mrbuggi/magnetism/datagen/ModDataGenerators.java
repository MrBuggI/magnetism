package io.github.mrbuggi.magnetism.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Точка входа датагена: {@code ./gradlew runData} пишет результат в {@code src/generated/resources}.
 * JSON блокстейта, моделей, лута, рецепта, тегов и переводов руками не редактируются.
 */
public final class ModDataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, fileHelper));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.English(output));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.Russian(output));

        ModBlockTagsProvider blockTags = generator.addProvider(event.includeServer(),
                new ModBlockTagsProvider(output, lookup, fileHelper));
        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(output, lookup, blockTags.contentsGetter(), fileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookup));
        generator.addProvider(event.includeServer(), new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)),
                lookup));
    }

    private ModDataGenerators() {
    }
}
