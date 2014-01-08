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
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
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
import org.megastage.components.EngineData;
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
        Node tmp = getNode(parentEntity);
        Node main = (Node) tmp.getChild("offset");
        final Node parentNode = main == null ? tmp: main; 
        
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
        int chunkSize = data.getChunkSize();

        float cx = 0, cy = 0, cz = 0, bc = 0;
        
        BlockTerrainControl blockControl = new BlockTerrainControl(ClientGlobals.cubesSettings, new Vector3Int(chunkSize, chunkSize, chunkSize));
        for(int x = 0; x <= data.maxx; x++) {
            for(int y = 0; y <= data.maxy; y++) {
                for(int z = 0; z <= data.maxz; z++) {
                    if(data.data[x][y][z]) {
                        cx += x; cy += y; cz += z; bc++;
                        blockControl.setBlock(x, y, z, Block_Wood.class);
                    }
                }
            }
        }
        
        cx /= bc; cy /= bc; cz /= bc;
        cx += 0.5; cy += 0.5; cz += 0.5;
        
        Node offset = new Node("offset");
        offset.addControl(blockControl);
        //shipNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final Node node = createUserNode(entity);
        node.attachChild(offset);
        offset.setLocalTranslation(-cx, -cy, -cz);

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
        Node main = new Node("main");
        engine.attachChild(main);
        main.setLocalTranslation(0.5f, 0.5f, 0.5f);

        main.attachChild(assetManager.loadModel("Scenes/testScene.j3o"));

        Geometry geom = new Geometry("", new Cylinder(16, 16, 0.5f, 1, true));
        geom.setMaterial(material(ColorRGBA.Gray, true));
        
        main.attachChild(geom);
        ((ParticleEmitter) main.getChild("Emitter")).setEnabled(true);
    }
    
    public void setupMonitor(Entity entity, MonitorGeometry data) {
        Geometry geom = new Geometry(entity.toString(), new Quad(data.width, data.height, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        Node node = createUserNode(entity);
        Node main = new Node("main");
        node.attachChild(main);
        main.setLocalTranslation(0.5f, 0.0f, 0.5f);
        
        main.attachChild(geom);
        geom.setLocalTranslation(-0.5f, 0, 0f);

        ClientRaster rasterComponent = ClientGlobals.artemis.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    public void setupCharacter(Entity entity, CharacterGeometry data) {
        Material mat = material(new ColorRGBA(data.red, data.green, data.blue, data.alpha), true);

        Geometry body = new Geometry(entity.toString(), new Box(0.25f, 0.5f, 0.25f));
        body.setMaterial(mat);
        body.setLocalTranslation(0.5f, 0.5f, 0.5f);

        Geometry head = new Geometry(entity.toString(), new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(mat);
        head.setLocalTranslation(0.5f, 1.5f, 0.5f);
        
        Node node = createUserNode(entity);
        node.attachChild(body);
        node.attachChild(head);
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
            ClientGlobals.fixedNode.attachChild(ClientGlobals.playerNode);
        }
    }

    public void updateEngine(Entity engineEntity, EngineData data) {
        Node engineNode = getNode(engineEntity);
        Node main = (Node) engineNode.getChild("main");
        ParticleEmitter emitter = ((ParticleEmitter) main.getChild("Emitter"));
        if(data.power == 0) {
            emitter.setEnabled(false);
        } else {
            emitter.setEnabled(true);
            float high = (float) (0.1 + 0.9 * data.power / Character.MAX_VALUE);
            float low = (float) (0.05 + 0.095 * data.power / Character.MAX_VALUE);
            emitter.setHighLife(high);
            emitter.setLowLife(low);
            emitter.setStartSize(low);
            emitter.setEndSize(high);
        }
        
    }
    
    private void enterShip(Entity shipEntity) {
        Log.info(shipEntity.toString());

        ClientGlobals.shipEntity = shipEntity;

        Node shipNode = getNode(shipEntity);
        ClientGlobals.fixedNode.attachChild(shipNode);
        ((Node) shipNode.getChild("offset")).attachChild(ClientGlobals.playerNode);
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
