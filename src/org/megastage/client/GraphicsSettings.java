package org.megastage.client;

public enum GraphicsSettings {

    HIGH(32, 32, 32, true, 800, 600, true, true),
    MEDIUM(16, 16, 16, false, 640, 400, false, true),
    LOW(16, 16, 4, true, 640, 400, false, false);

    public final int SCREEN_WIDTH;
    public final int SCREEN_HEIGHT;
    public final boolean ENABLE_PLANETS;
    public final boolean ENABLE_PLANET_FAR_FILTER;
    public final int PLANET_PLANAR_QUADS_PER_PATCH;
    public final int SPHERE_Z_SAMPLES;
    public final int SPHERE_RADIAL_SAMPLES;
    public final boolean ENABLE_LEM_BLINKING;

    GraphicsSettings(
            int screenWidth,
            int screenHeight,
            boolean enablePlanets,
            boolean enablePlanetFarFilter,
            int planetPlanarQuadsPerPatch,
            int sphereZSamples,
            int sphereRadialSamples,
            boolean enableLEMBlinking) {
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;
        ENABLE_PLANETS = enablePlanets;
        ENABLE_PLANET_FAR_FILTER = enablePlanetFarFilter;
        PLANET_PLANAR_QUADS_PER_PATCH = planetPlanarQuadsPerPatch;
        SPHERE_Z_SAMPLES = sphereZSamples;
        SPHERE_RADIAL_SAMPLES = sphereRadialSamples;
        ENABLE_LEM_BLINKING = enableLEMBlinking;
    }
    
    public static class GFXVeryLowQuality extends GFXQuality {
        public static int Z1 = SPHERE_Z_SAMPLES = 16;
        public static int Z2 = SPHERE_RADIAL_SAMPLES = 16;
        public static boolean Z9 = ENABLE_PLANETS = false;
        public static int Z3 = PLANET_PLANAR_QUADS_PER_PATCH = 4;
        public static boolean Z8 = PLANET_FAR_FILTER_ENABLED = true;
        public static int Z5 = SCREEN_WIDTH = 640;
        public static int Z6 = SCREEN_HEIGHT = 400;
        public static boolean Z7 = ENABLE_LEM_BLINKING = false;
    }    
}
