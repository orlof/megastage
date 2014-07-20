/*
Copyright (c) 2012 Aaron Perkins

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package jmeplanet;

import org.megastage.util.Log;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.shadow.AbstractShadowRenderer;
import java.util.ArrayList;
import java.util.List;
import org.megastage.client.ClientGlobals;

/**
 * PlanetAppState
 * 
 */
public class PlanetAppState extends AbstractAppState {
    
    protected Application app;
    protected List<Planet> planets;
    protected Planet nearestPlanet;
    
    protected FilterPostProcessor farFilter;
    protected FogFilter farFog;
    protected BloomFilter farBloom;
    
    protected Spatial scene;
    protected ViewPort farViewPort;
    protected Camera farCam;
    
    protected boolean shadowsEnabled;
    
    protected Light sun;
     
    public PlanetAppState(Spatial scene, Light sun) {
        this.scene = scene;
        this.planets = new ArrayList<>();
        
        this.sun = sun;
    }
    
    public PlanetAppState(Spatial scene) {
        this.scene = scene;
        this.planets = new ArrayList<>(); 
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        
        // Init 1st viewport (far)
        farViewPort = app.getViewPort();
        farCam = app.getCamera();

        float aspect = (float) farCam.getWidth() / farCam.getHeight();
        farCam.setFrustumPerspective(45f, aspect, 1f, 40000f);

        if(ClientGlobals.gfxSettings.ENABLE_PLANET_FAR_FILTER) {
            farFilter=new FilterPostProcessor(app.getAssetManager());
            farViewPort.addProcessor(farFilter);

            farFog = new FogFilter();
            farFilter.addFilter(farFog);

            farBloom=new BloomFilter();
            farBloom.setDownSamplingFactor(4);
            farBloom.setBlurScale(1.37f);
            farBloom.setExposurePower(3.30f);
            farBloom.setExposureCutOff(0.1f);
            farBloom.setBloomIntensity(1.45f);
            farFilter.addFilter(farBloom);

            BloomFilter bloom= new BloomFilter(BloomFilter.GlowMode.Objects);        
            farFilter.addFilter(bloom);        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
    
    @Override
    public void update(float tpf) {
        this.nearestPlanet = findNearestPlanet();
        
        for (Planet planet: this.planets ) {
            planet.setCameraPosition(this.app.getCamera().getLocation());
        }
        
        if(ClientGlobals.gfxSettings.ENABLE_PLANET_FAR_FILTER) {
            updateFogAndBloom();
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    public void addPlanet(Planet planet) {
        this.planets.add(planet);
    }
    
    public List<Planet> getPlanets() {
        return this.planets;
    }
    
    public Planet getNearestPlanet() {
        return this.nearestPlanet;
    }
    
    public Vector3f getGravity() {
        Planet planet = getNearestPlanet();
        if (planet != null && planet.getPlanetToCamera() != null) {
            return planet.getPlanetToCamera().normalize().mult(-9.81f);
        } 
        return Vector3f.ZERO;
    }
    
    protected Planet findNearestPlanet() {
        Planet cPlanet = null;
        for (Planet planet: this.planets ) {
            if (cPlanet == null || cPlanet.getDistanceToCamera() > planet.getDistanceToCamera()) {
                cPlanet = planet;
            }
        }
        return cPlanet;
    }
    
    protected void updateFogAndBloom() {
        if (this.nearestPlanet == null) {
            return;
        }
        Planet planet = this.nearestPlanet;
        if (planet.getIsInOcean()) {
            farFog.setFogColor(planet.getUnderwaterFogColor());
            farFog.setFogDistance(planet.getUnderwaterFogDistance());
            farFog.setFogDensity(planet.getUnderwaterFogDensity());                        
            farFog.setEnabled(true);
            farBloom.setEnabled(true);
        } else {
            if (planet.getIsInAtmosphere()) {
                // turn on atomosphere fogging
                farFog.setFogColor(planet.getAtmosphereFogColor());
                farFog.setFogDistance(planet.getAtmosphereFogDistance());
                farFog.setFogDensity(planet.getAtmosphereFogDensity()); 
                farFog.setEnabled(true);
                farBloom.setEnabled(false);  
            } else {
                // in space
                farFog.setEnabled(false);
                farBloom.setEnabled(true);
            }   
        }
    }
  
}
