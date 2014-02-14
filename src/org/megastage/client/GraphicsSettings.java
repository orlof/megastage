package org.megastage.client;

public enum GraphicsSettings {

    HIGH(800, 600, true, true, 32, 32, 32, true),
    MEDIUM(640, 400, true, false, 16, 16, 16, false),
    LOW(800, 600, false, true, 16, 16, 16, false),
    JOKE(320, 200, false, false, 12, 12, 12, false);

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
}
