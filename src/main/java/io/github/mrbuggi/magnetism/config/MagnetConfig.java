package io.github.mrbuggi.magnetism.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import io.github.mrbuggi.magnetism.magnet.MagnetSettings;

/**
 * Серверный конфиг: всё, что влияет на баланс и нагрузку.
 * <p>
 * Значения читаются из {@link ModConfigSpec} один раз при загрузке или перезагрузке конфига
 * и раскладываются в поля {@link MagnetSettings}. Тикеры магнитов работают с обычными
 * полями и не обращаются к конфигу каждый тик.
 */
public final class MagnetConfig {

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.DoubleValue ITEM_RADIUS;
    private static final ModConfigSpec.DoubleValue ITEM_PULL;
    private static final ModConfigSpec.DoubleValue MAGNET_RADIUS;
    private static final ModConfigSpec.DoubleValue MAGNET_FORCE;
    private static final ModConfigSpec.IntValue RESCAN_INTERVAL;
    private static final ModConfigSpec.IntValue IDLE_RESCAN_INTERVAL;
    private static final ModConfigSpec.IntValue MAX_TRACKED_ITEMS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Притяжение предметов").push("items");
        ITEM_RADIUS = builder
                .comment("Радиус, в котором магнит притягивает предметы, в блоках")
                .defineInRange("radius", 6.0, 1.0, 16.0);
        ITEM_PULL = builder
                .comment("Сила притяжения предметов")
                .defineInRange("pull", 0.30, 0.0, 2.0);
        builder.pop();

        builder.comment("Взаимодействие магнитов между собой").push("magnets");
        MAGNET_RADIUS = builder
                .comment("Радиус, в котором падающие магниты действуют друг на друга, в блоках")
                .defineInRange("radius", 8.0, 1.0, 16.0);
        MAGNET_FORCE = builder
                .comment("Сила притяжения и отталкивания полюсов")
                .defineInRange("force", 0.55, 0.0, 4.0);
        builder.pop();

        builder.comment("Производительность").push("performance");
        RESCAN_INTERVAL = builder
                .comment("Раз в сколько тиков магнит заново ищет предметы вокруг себя")
                .defineInRange("rescanInterval", 10, 1, 200);
        IDLE_RESCAN_INTERVAL = builder
                .comment("Интервал поиска, когда рядом с магнитом ничего не нашлось")
                .defineInRange("idleRescanInterval", 40, 1, 600);
        MAX_TRACKED_ITEMS = builder
                .comment("Сколько предметов один магнит притягивает одновременно")
                .defineInRange("maxTrackedItems", 32, 1, 256);
        builder.pop();

        SPEC = builder.build();
    }

    public static void onLoad(ModConfigEvent.Loading event) {
        apply(event);
    }

    public static void onReload(ModConfigEvent.Reloading event) {
        apply(event);
    }

    private static void apply(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        MagnetSettings.setItems(ITEM_RADIUS.get(), ITEM_PULL.get());
        MagnetSettings.setMagnets(MAGNET_RADIUS.get(), MAGNET_FORCE.get());
        MagnetSettings.setPerformance(RESCAN_INTERVAL.get(), IDLE_RESCAN_INTERVAL.get(), MAX_TRACKED_ITEMS.get());
    }

    private MagnetConfig() {
    }
}
