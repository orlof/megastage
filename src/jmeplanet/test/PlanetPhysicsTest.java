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

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.system.AppSettings;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.CameraControl.ControlDirection;

import jmeplanet.Planet;
import jmeplanet.FractalDataSource;
import jmeplanet.PlanetAppState;
import jmeplanet.PlanetCollisionShape;

/**
 * PlanetPhysicsTest
 * 
 */
public class PlanetPhysicsTest extends SimpleApplication {
    
    private BulletAppState bulletAppState;
    private PlanetAppState planetAppState;
    private CameraNode cameraNode;
    private RigidBodyControl cameraNodePhysicsControl;
    private boolean physicsDebug = false;
    private float linearSpeed = 10000f;
    private float angularSpeed = 50f;
    
    public static void main(String[] args){
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024,768);
        PlanetPhysicsTest app = new PlanetPhysicsTest();
        
        app.setSettings(settings);
        app.showSettings = true;
        app.start();
    }
    
    public PlanetPhysicsTest() {
        super( new StatsAppState(), new DebugKeysAppState() );
    }
 
    @Override
    public void simpleInitApp() {
        // Only show severe errors in log
        java.util.logging.Logger.getLogger("com.jme3").setLevel(java.util.logging.Level.SEVERE);
        
        // setup physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
        
        // setup input
        setupInput();
        
        // setup camera and camera node
        CameraControl cameraControl = new CameraControl(this.getCamera(),ControlDirection.SpatialToCamera);
        cameraNode = new CameraNode("Camera", cameraControl);
        cameraNode.setLocalTranslation(new Vector3f(-50000f, 0f, 150000f));
        cameraNode.rotate(0, FastMath.PI, 0);
        cameraNodePhysicsControl = new RigidBodyControl(new SphereCollisionShape(2.5f), 1f);
        cameraNode.addControl(cameraNodePhysicsControl);
        rootNode.attachChild(cameraNode);
        bulletAppState.getPhysicsSpace().add(cameraNode);
        cameraNodePhysicsControl.setAngularFactor(0);
        cameraNodePhysicsControl.setLinearDamping(0.8f);
        cameraNodePhysicsControl.setAngularDamping(0.99f);
        
        // Add sun
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(0.45f,0.45f,0.35f,1.0f));
        sun.setDirection(new Vector3f(1f, -1f, 0f));
        rootNode.addLight(sun);
        
        // Add sky
        Node sceneNode = new Node("Scene");
        sceneNode.attachChild(Utility.createSkyBox(this.getAssetManager(), "Textures/blue-glow-1024.dds"));
        rootNode.attachChild(sceneNode);
        
        // Add planet app state
        planetAppState = new PlanetAppState(rootNode, sun);
        stateManager.attach(planetAppState);
        
        // Add planet
        FractalDataSource planetDataSource = new FractalDataSource(4);
        planetDataSource.setHeightScale(800f);
        Planet planet = Utility.createEarthLikePlanet(getAssetManager(), 63710.0f, null, planetDataSource);
        planet.addControl(new RigidBodyControl(new PlanetCollisionShape(planet.getLocalTranslation(), planet.getRadius(), planetDataSource), 0f));
        planetAppState.addPlanet(planet);
        rootNode.attachChild(planet);
        bulletAppState.getPhysicsSpace().add(planet);
        
        // Add moon
        FractalDataSource moonDataSource = new FractalDataSource(5);
        moonDataSource.setHeightScale(300f);
        Planet moon = Utility.createMoonLikePlanet(getAssetManager(), 10000, moonDataSource);
        moon.setLocalTranslation(-100000f, 0f, 0f);
        RigidBodyControl moonPhysicsControl = new RigidBodyControl(new PlanetCollisionShape(moon.getLocalTranslation(), moon.getRadius(), moonDataSource), 0f);
        moon.addControl(moonPhysicsControl);   
        planetAppState.addPlanet(moon);
        rootNode.attachChild(moon);
        bulletAppState.getPhysicsSpace().add(moon);  
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        Planet planet = planetAppState.getNearestPlanet();
        if (planet != null && planet.getPlanetToCamera() != null) {
            cameraNodePhysicsControl.setGravity(planet.getPlanetToCamera().normalize().mult(-100f));
        }   
    }
    
    private void setupInput() {
        // Toggle mouse cursor
        inputManager.addMapping("TOGGLE_CURSOR", 
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT),
                new KeyTrigger(KeyInput.KEY_SPACE));
        // Toggle wireframe
        inputManager.addMapping("TOGGLE_WIREFRAME", new KeyTrigger(KeyInput.KEY_T));
        // Toggle physics view
        inputManager.addMapping("TOGGLE_PHYSICS_DEBUG", new KeyTrigger(KeyInput.KEY_P));
        // Linear speed
        inputManager.addMapping("SpeedUp", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("SpeedDown", new KeyTrigger(KeyInput.KEY_PGDN));
        // Movement keys
        inputManager.addMapping("RotateLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true),
                                               new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("RotateRight", new MouseAxisTrigger(MouseInput.AXIS_X, false),
                                                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("RotateDown", new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                                             new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("RotateUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                                               new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("SpinLeft", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("SpinRight", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("RotateUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                                               new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("MoveForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("MoveBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(actionListener, "TOGGLE_WIREFRAME", "TOGGLE_CURSOR", "TOGGLE_PHYSICS_DEBUG","SpeedUp","SpeedDown");
        inputManager.addListener(analogListener, "MoveLeft","MoveRight","MoveForward","MoveBackward","RotateLeft","RotateRight","RotateUp","RotateDown","SpinLeft","SpinRight" );          
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
            if (name.equals("TOGGLE_PHYSICS_DEBUG") && !pressed) {
                if (!physicsDebug) {
                    bulletAppState.getPhysicsSpace().enableDebug(getAssetManager());
                    for (Planet planet: planetAppState.getPlanets()) {
                        planet.setVisiblity(false);
                    }
                    physicsDebug = true;
                }
                else {
                    bulletAppState.getPhysicsSpace().disableDebug();
                    for (Planet planet: planetAppState.getPlanets()) {
                       planet.setVisiblity(true);
                    }
                    physicsDebug = false;
                }
            }
            if (name.equals("SpeedUp") && !pressed) {
                if (linearSpeed >= 1000) {
                    linearSpeed += 1000;
                } else {
                    linearSpeed += 100;
                }
                if (linearSpeed > 10000) {
                    linearSpeed = 10000;
                }
                System.out.println("Speed:" + linearSpeed);
            }
            if (name.equals("SpeedDown") && !pressed) {
                if (linearSpeed <= 1000) {  
                    linearSpeed -= 100;
                } else {
                    linearSpeed -= 1000;
                }
                if (linearSpeed < 100) {
                    linearSpeed = 100;
                }
                System.out.println("Speed:" + linearSpeed);
            }  
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            
            if (name.equals("MoveLeft"))
                cameraNodePhysicsControl.applyCentralForce(getCamera().getLeft().mult(linearSpeed));
            if (name.equals("MoveRight"))
                cameraNodePhysicsControl.applyCentralForce(getCamera().getLeft().mult(-linearSpeed));
            if (name.equals("MoveForward"))
                cameraNodePhysicsControl.applyCentralForce(getCamera().getDirection().mult(linearSpeed));
            if (name.equals("MoveBackward"))
                cameraNodePhysicsControl.applyCentralForce(getCamera().getDirection().mult(-linearSpeed));
            
            Vector3f xRotation = cameraNodePhysicsControl.getPhysicsRotation().getRotationColumn(0).normalize();
            Vector3f yRotation = cameraNodePhysicsControl.getPhysicsRotation().getRotationColumn(1).normalize();
            Vector3f zRotation = cameraNodePhysicsControl.getPhysicsRotation().getRotationColumn(2).normalize();

            if (name.equals("RotateLeft"))
                cameraNodePhysicsControl.applyTorque(yRotation.mult(angularSpeed));
            if (name.equals("RotateRight"))
                cameraNodePhysicsControl.applyTorque(yRotation.mult(-angularSpeed));
            if (name.equals("RotateUp"))
                cameraNodePhysicsControl.applyTorque(xRotation.mult(angularSpeed));
            if (name.equals("RotateDown"))
                cameraNodePhysicsControl.applyTorque(xRotation.mult(-angularSpeed));
            if (name.equals("SpinLeft"))
                cameraNodePhysicsControl.applyTorque(zRotation.mult(-angularSpeed));
            if (name.equals("SpinRight"))
                cameraNodePhysicsControl.applyTorque(zRotation.mult(angularSpeed));

        }   
    };
    
}