package io.github.mrbuggi.magnetism.magnet;

/**
 * Параметры физики магнитов.
 * <p>
 * Верхняя группа настраивается через серверный конфиг: {@code MagnetConfig} записывает сюда
 * значения при загрузке и перезагрузке конфига. Значения по умолчанию совпадают с
 * умолчаниями конфига. Нижняя группа - константы модели, менять их без правки кода нельзя.
 */
public final class MagnetSettings {

    // --- настраивается конфигом ---

    private static double itemRadius = 6.0;
    private static double itemRadiusSqr = itemRadius * itemRadius;
    private static double itemPull = 0.30;

    private static double magnetRadiusSqr = 8.0 * 8.0;
    private static double magnetForce = 0.55;

    private static int rescanInterval = 10;
    private static int idleRescanInterval = 40;
    private static int maxTrackedItems = 32;

    // --- константы модели ---

    public static final double ITEM_MAX_STEP = 0.35;
    public static final double ITEM_DAMP_DIST_SQR = 2.25;
    public static final double ITEM_DAMP = 0.75;

    public static final double MAGNET_MAX_STEP = 0.22;

    /** Нижняя граница квадрата расстояния: не даёт силе уйти в бесконечность вплотную к полюсу. */
    public static final double MIN_DIST_SQR = 0.25;

    /** Смещение полюса от центра блока вдоль оси магнита. */
    public static final double POLE_OFFSET = 0.5;

    /** Движение синхронизируется с клиентом раз в 4 тика. */
    public static final long SYNC_MASK = 3L;

    public static double itemRadius() {
        return itemRadius;
    }

    public static double itemRadiusSqr() {
        return itemRadiusSqr;
    }

    public static double itemPull() {
        return itemPull;
    }

    public static double magnetRadiusSqr() {
        return magnetRadiusSqr;
    }

    public static double magnetForce() {
        return magnetForce;
    }

    public static int rescanInterval() {
        return rescanInterval;
    }

    public static int idleRescanInterval() {
        return idleRescanInterval;
    }

    public static int maxTrackedItems() {
        return maxTrackedItems;
    }

    public static void setItems(double radius, double pull) {
        itemRadius = radius;
        itemRadiusSqr = radius * radius;
        itemPull = pull;
    }

    public static void setMagnets(double radius, double force) {
        magnetRadiusSqr = radius * radius;
        magnetForce = force;
    }

    public static void setPerformance(int rescan, int idleRescan, int maxItems) {
        rescanInterval = rescan;
        idleRescanInterval = idleRescan;
        maxTrackedItems = maxItems;
    }

    private MagnetSettings() {
    }
}
