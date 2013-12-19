/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.artemis.Entity;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.blocks.Block_Wood;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.Callable;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.server.MonitorGeometry;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.CharacterGeometry;
import org.megastage.components.server.EngineGeometry;
import org.megastage.components.server.ShipGeometry;
import org.megastage.components.server.SunGeometry;
import org.megastage.components.server.VoidGeometry;
import org.megastage.util.ClientGlobals;

/**
 *
 * @author Orlof
 */
public class SpatialManager {

    private final SimpleApplication app;
    private final AssetManager assetManager;

    private HashMap<Integer, Node> nodes = new HashMap<>();
    
    public SpatialManager(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
    }

    private Node getNode(Entity entity) {
        int id = entity.getId();
        Node node = nodes.get(id);
 
        if(node == null) {
            node = new Node(entity.toString());
            nodes.put(id, node);
        }
        
        return node;
    }
    
    public void changeShip(final Entity shipEntity) {
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                leaveShip();
                enterShip(shipEntity);
                return null;
            }
        });
    }
    
    public void bindTo(final Entity parentEntity, final Entity childEntity) {
        final Node parentNode = getNode(parentEntity);
        final Node childNode = getNode(childEntity);
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                parentNode.attachChild(childNode);
                Log.info("Attach " + parentNode.getName() + " <- " + childNode.getName());
                return null;
            }
        });
    }
    
    private Node createUserNode(Entity entity) {
        // DON'T use for system nodes
        Node node = getNode(entity);
 
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        return node;
     }
    
    private Geometry createSphere(float radius, ColorRGBA color, boolean shaded) {
        Sphere mesh = new Sphere(
                ClientGlobals.gfxQuality.SPHERE_Z_SAMPLES,
                ClientGlobals.gfxQuality.SPHERE_RADIAL_SAMPLES, 
                radius);
        
        Geometry geom = new Geometry();
        geom.setMesh(mesh);

        if(shaded) {
            Material mat = material(color, true);
            mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/rock.jpg")); // with Lighting.j3md
            geom.setMaterial(mat);               // Use new material on this Geometry.
        } else {
            geom.setMaterial(material(color, false));
        }

        return geom;
    }
    
    public void setupSunLikeBody(final Entity entity, final SunGeometry data) {
        ColorRGBA colorRGBA = new ColorRGBA(data.red, data.green, data.blue, data.alpha);

        final Node node = createUserNode(entity);
        node.attachChild(createSphere(data.radius, colorRGBA, false));

        final PointLight light = new PointLight();
        light.setColor(colorRGBA);
        light.setRadius(data.lightRadius);

        LightNode lightNode = new LightNode(entity.toString() + " LightNode", light);
        node.attachChild(lightNode);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                ClientGlobals.rootNode.addLight(light);
                ClientGlobals.sysMovNode.attachChild(node);
                return null;
            }
        });
    }

    public void setupPlanetLikeBody(Entity entity, PlanetGeometry data) {
        // Add planet
        final Node node = createUserNode(entity);
        
        if(ClientGlobals.gfxQuality.ENABLE_PLANETS) {
            node.attachChild(createPlanet(data));

            app.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    PlanetAppState appState = app.getStateManager().getState(PlanetAppState.class);
                    if(appState != null) appState.addPlanet((Planet) node.getChild(0));

                    if(node.getParent() == null) {
                        ClientGlobals.sysMovNode.attachChild(node);
                    }
                    return null;
                }
            });
        } else {
            ColorRGBA color = null;
            try {
                color = (ColorRGBA) ColorRGBA.class.getDeclaredField(data.color).get(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            node.attachChild(createSphere(data.radius, color, true));
            app.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    if(node.getParent() == null) {
                        ClientGlobals.sysMovNode.attachChild(node);
                    }
                    return null;
                }
            });
        }
    }
    
    public void setupVoidNode(Entity entity, VoidGeometry data) {
        // Add planet
        final Node node = createUserNode(entity);
    }
    
    public void setupShip(Entity entity, ShipGeometry data) {
        int chunkSize = data.size / 16 + 1;
        
        BlockTerrainControl shipBlockControl = new BlockTerrainControl(ClientGlobals.cubesSettings, new Vector3Int(chunkSize, chunkSize, chunkSize));
        shipBlockControl.setBlockArea(new Vector3Int(0,0,0), new Vector3Int(data.size, 1, data.size), Block_Wood.class);

        Node shipNode = new Node("main");
        shipNode.addControl(shipBlockControl);
        //shipNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final Node node = createUserNode(entity);
        node.attachChild(shipNode);
        shipNode.setLocalTranslation(-data.size, -1, -data.size);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                if(node.getParent() == null) {
                    ClientGlobals.sysMovNode.attachChild(node);
                }
                return null;
            }
        });
    }

    public void setupEngine(Entity entity, EngineGeometry data) {
        Node engine = createUserNode(entity);

        engine.attachChild(assetManager.loadModel("Scenes/testScene.j3o"));

        Geometry geom = new Geometry("", new Cylinder(16, 16, 1, 1, true));
        geom.setMaterial(material(ColorRGBA.Gray, true));
        
        engine.attachChild(geom);
        ((ParticleEmitter) engine.getChild("Emitter")).setEnabled(true);
    }
    
    public void setupMonitor(Entity entity, MonitorGeometry data) {
        Geometry geom = new Geometry(entity.toString(), new Quad(8, 6, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        Node node = createUserNode(entity);
        node.attachChild(geom);

        ClientRaster rasterComponent = ClientGlobals.artemis.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    public void setupCharacter(Entity entity, CharacterGeometry data) {
        Geometry base = new Geometry(entity.toString(), new Box(0.5f, 1.5f, 0.5f));

        Geometry nose = new Geometry(entity.toString(), new Box(0.5f, 0.5f, 0.7f));
        nose.setLocalTranslation(0, 2, 0.2f);
        
        Material mat = material(new ColorRGBA(data.red, data.green, data.blue, data.alpha), true);
        base.setMaterial(mat);
        nose.setMaterial(mat);
        
        Node node = createUserNode(entity);
        node.attachChild(base);
        node.attachChild(nose);
    }

    private Material material(ColorRGBA color, boolean lighting) {
        if(lighting) {
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
            mat.setBoolean("UseMaterialColors",true);
            mat.setColor("Ambient", color);
            mat.setColor("Diffuse", color);
            return mat;
        } else {
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", color);
            return mat;           
        }
    }
    
    private Planet createPlanet(PlanetGeometry data) {
        Log.info(data.toString());
        if(data.generator.equalsIgnoreCase("Earth")) {
            FractalDataSource planetDataSource = new FractalDataSource(4);
            planetDataSource.setHeightScale(data.radius / 100f);
            return Utility.createEarthLikePlanet(assetManager, data.radius, null, planetDataSource);
        } else if(data.generator.equalsIgnoreCase("Moon")) {
            FractalDataSource planetDataSource = new FractalDataSource(4);
            planetDataSource.setHeightScale(data.radius / 20f);
            return Utility.createMoonLikePlanet(assetManager, data.radius, planetDataSource);
        } else if(data.generator.equalsIgnoreCase("Water")) {
            return Utility.createWaterPlanet(assetManager, data.radius, null);
        } 

        throw new RuntimeException("Unknown planet generator: " + data.generator);
    }

    private void leaveShip() {
        Entity shipEntity = ClientGlobals.shipEntity;
        
        if(shipEntity != null) {
            Log.debug(shipEntity.toString());

            ClientGlobals.shipEntity = null;

            Node shipNode = getNode(shipEntity);
            ClientGlobals.sysMovNode.attachChild(shipNode);
        }
    }
    
    private void enterShip(Entity shipEntity) {
        Log.debug(shipEntity.toString());

        ClientGlobals.shipEntity = shipEntity;

        Node shipNode = getNode(shipEntity);
        ClientGlobals.fixedNode.attachChild(shipNode);
    }

    public void setupPlayer(final Entity entity) {
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                ClientGlobals.playerNode.addControl(new PositionControl(entity));
                ClientGlobals.playerNode.addControl(new RotationControl(entity));
                return null;
            }
        });
    }
}
