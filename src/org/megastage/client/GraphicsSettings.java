package org.megastage.client;

public enum GraphicsSettings {

    HIGH(true, true, 800, 600, true, false, 32, 32, 32, true, true),
    NO_PLANETS(false, false, 480, 300, false, false, 24, 24, 24, true, true),
    MEDIUM(true, false, 640, 400, true, false, 16, 16, 16, false, true),
    LOW(false, false, 800, 600, false, true, 16, 16, 16, false, false),
    JOKE(false, false, 320, 200, false, false, 12, 12, 12, false, false);

    public final boolean SHOW_SETTINGS;
    public final boolean FULL_SCREEN;
    public final int SCREEN_WIDTH;
    public final int SCREEN_HEIGHT;
    public final boolean ENABLE_PLANETS;
    public final boolean ENABLE_PLANET_FAR_FILTER;
    public final int PLANET_PLANAR_QUADS_PER_PATCH;
    public final int SPHERE_Z_SAMPLES;
    public final int SPHERE_RADIAL_SAMPLES;
    public final boolean ENABLE_LEM_BLINKING;
    public final boolean ENABLE_SHIP_SHADOWS;

    GraphicsSettings(
            boolean showSettings,
            boolean fullScreen,
            int screenWidth,
            int screenHeight,
            boolean enablePlanets,
            boolean enablePlanetFarFilter,
            int planetPlanarQuadsPerPatch,
            int sphereZSamples,
            int sphereRadialSamples,
            boolean enableLEMBlinking,
            boolean enableShipShadows) {
        SHOW_SETTINGS = showSettings;
        FULL_SCREEN = fullScreen;
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;
        ENABLE_PLANETS = enablePlanets;
        ENABLE_PLANET_FAR_FILTER = enablePlanetFarFilter;
        PLANET_PLANAR_QUADS_PER_PATCH = planetPlanarQuadsPerPatch;
        SPHERE_Z_SAMPLES = sphereZSamples;
        SPHERE_RADIAL_SAMPLES = sphereRadialSamples;
        ENABLE_LEM_BLINKING = enableLEMBlinking;
        ENABLE_SHIP_SHADOWS = enableShipShadows;
    }
}
