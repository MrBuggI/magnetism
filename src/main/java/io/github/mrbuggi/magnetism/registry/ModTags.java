package io.github.mrbuggi.magnetism.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import io.github.mrbuggi.magnetism.Magnetism;

public final class ModTags {

    public static final TagKey<Item> MAGNETIC = TagKey.create(Registries.ITEM, Magnetism.id("magnetic"));

    private ModTags() {
    }
}
