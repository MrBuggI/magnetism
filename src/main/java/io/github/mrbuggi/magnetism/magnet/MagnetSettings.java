package io.github.mrbuggi.magnetism.magnet;


public final class MagnetSettings {


    public static final double ITEM_RADIUS = 6.0;
    public static final double ITEM_RADIUS_SQR = ITEM_RADIUS * ITEM_RADIUS;
    public static final double ITEM_PULL = 0.30;
    public static final double ITEM_MAX_STEP = 0.35;
    public static final double ITEM_DAMP_DIST_SQR = 2.25;
    public static final double ITEM_DAMP = 0.75;


    public static final double MAGNET_RADIUS = 8.0;
    public static final double MAGNET_RADIUS_SQR = MAGNET_RADIUS * MAGNET_RADIUS;
    public static final double MAGNET_FORCE = 0.55;
    public static final double MAGNET_MAX_STEP = 0.22;


    public static final double MIN_DIST_SQR = 0.25;


    public static final double POLE_OFFSET = 0.5;

    public static final int RESCAN_INTERVAL = 10;
    public static final int IDLE_RESCAN_INTERVAL = 40;
    public static final int MAX_TRACKED_ITEMS = 32;
    public static final long SYNC_MASK = 3L;

    private MagnetSettings() {
    }
}
