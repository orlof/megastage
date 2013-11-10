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
package jmeplanet.test;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.system.AppSettings;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.water.SimpleWaterProcessor;
import jmeplanet.FractalDataSource;

import jmeplanet.Planet;
import jmeplanet.PlanetAppState;

/**
 * PlanetSimpleTest
 * 
 */
public class PlanetSimpleTest extends SimpleApplication {
    
    private PlanetAppState planetAppState;
    private Geometry mark;
    
    Camera cam2;
    
    public static void main(String[] args){
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024,768);
        PlanetSimpleTest app = new PlanetSimpleTest();
        
        app.setSettings(settings);
        app.showSettings = true;
        app.start();
    }
 
    @Override
    public void simpleInitApp() {
        // Only show severe errors in log
        java.util.logging.Logger.getLogger("com.jme3").setLevel(java.util.logging.Level.SEVERE);
        
        // Toggle mouse cursor
        inputManager.addMapping("TOGGLE_CURSOR", 
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT),
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "TOGGLE_CURSOR"); 
        // Toggle wireframe
        inputManager.addMapping("TOGGLE_WIREFRAME", 
            new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "TOGGLE_WIREFRAME");
        // Collision test
        inputManager.addMapping("COLLISION_TEST", 
            new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "COLLISION_TEST"); 
        
        // Setup camera
             
        // In orbit
        this.getCamera().setLocation(new Vector3f(0f, 0f, 180000f));
        
        // On surface
        //this.getCamera().setLocation(new Vector3f(-6657.5254f, 27401.822f, 57199.777f));
        //this.getCamera().lookAtDirection(new Vector3f(0.06276598f, 0.94458306f, -0.3222158f), Vector3f.UNIT_Y);
        
        // Add sun
        //PointLight sun = new PointLight();
        //sun.setPosition(new Vector3f(-100000f,0,180000f));
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-.1f, 0f, -1f));
        sun.setColor(new ColorRGBA(0.75f,0.75f,0.75f,1.0f));      
        rootNode.addLight(sun);
        
        // Add sky
        Node sceneNode = new Node("Scene");
        sceneNode.attachChild(Utility.createSkyBox(this.getAssetManager(), "Textures/blue-glow-1024.dds"));
        rootNode.attachChild(sceneNode);
        
        // Create collision test mark
        Sphere sphere = new Sphere(30, 30, 5f);
        mark = new Geometry("mark", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        
        // Add planet app state
        planetAppState = new PlanetAppState(rootNode, sun);
        stateManager.attach(planetAppState);
        
        // Add planet
        FractalDataSource planetDataSource = new FractalDataSource(4);
        planetDataSource.setHeightScale(900f);
        Planet planet = Utility.createEarthLikePlanet(getAssetManager(), 63710.0f, null, planetDataSource);
        planetAppState.addPlanet(planet);
        rootNode.attachChild(planet);
        
        // Add moon
        FractalDataSource moonDataSource = new FractalDataSource(5);
        moonDataSource.setHeightScale(300f);
        Planet moon = Utility.createMoonLikePlanet(getAssetManager(), 20000, moonDataSource);
        planetAppState.addPlanet(moon);
        rootNode.attachChild(moon);
        moon.setLocalTranslation(-150000f, 0f, 0f);
        
    }
    
    @Override
    public void simpleUpdate(float tpf) {        
        // slow camera down as we approach a planet
        Planet planet = planetAppState.getNearestPlanet();
        if (planet != null && planet.getPlanetToCamera() != null) {
            this.getFlyByCamera().setMoveSpeed(
                    FastMath.clamp(planet.getDistanceToCamera(), 100, 100000));
        }     
    }
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean pressed, float tpf){     
            if (name.equals("TOGGLE_CURSOR") && !pressed) {
                if (inputManager.isCursorVisible()) {
                    inputManager.setCursorVisible(false);
                } else {
                    inputManager.setCursorVisible(true);
                }
            }
            if (name.equals("TOGGLE_WIREFRAME") && !pressed) {
                for (Planet planet: planetAppState.getPlanets()) {
                    planet.toogleWireframe();
                }
            }
            if (name.equals("COLLISION_TEST") && !pressed) {
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                
                // Test collision with closest planet's terrain only
                planetAppState.getNearestPlanet().getTerrainNode().collideWith(ray, results);

                System.out.println("----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                  // For each hit, we know distance, impact point, name of geometry.
                  float dist = results.getCollision(i).getDistance();
                  Vector3f pt = results.getCollision(i).getContactPoint();
                  String hit = results.getCollision(i).getGeometry().getName();
                  System.out.println("* Collision #" + i);
                  System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                }

                if (results.size() > 0) {
                  // The closest collision point is what was truly hit:
                  CollisionResult closest = results.getClosestCollision();
                  // Let's interact - we mark the hit with a red dot.
                  mark.setLocalTranslation(closest.getContactPoint());
                  rootNode.attachChild(mark);
                } else {
                  // No hits? Then remove the red mark.
                  rootNode.detachChild(mark);
                }
            }  
        }
    }; 
     
}