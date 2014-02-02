/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import org.megastage.client.controls.ExplosionControl;
import com.artemis.Entity;
import com.cubes.BlockTerrainControl;
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
import com.jme3.texture.Texture;
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
import org.megastage.client.controls.EngineControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.gfx.MonitorGeometry;
import org.megastage.components.gfx.PlanetGeometry;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.components.gfx.EngineGeometry;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.gfx.SunGeometry;
import org.megastage.components.UsableFlag;
import org.megastage.components.gfx.VoidGeometry;
import org.megastage.components.Explosion;

/**
 *
 * @author Orlof
 */
public class SpatialManager {

    private final SimpleApplication app;
    private final AssetManager assetManager;

    private HashMap<Integer, Node> nodes = new HashMap<>();
    private HashMap<Node, Entity> entities = new HashMap<>();
    
    public SpatialManager(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
        
        ExplosionNode.initialize(assetManager);
    }

    public void deleteEntity(Entity entity) {
        int id = entity.getId();
        final Node node = nodes.get(id);

        if(node != null) {
            app.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    node.removeFromParent();
                    //TODO remove lights
                    return null;
                }
            });
        }
    }
    
    public Entity getEntity(Node node) {
        Entity entity = entities.get(node);
        if(entity == null) {
            return null;
        }

        UsableFlag use = entity.getComponent(UsableFlag.class);
        if(use != null) {
            return entity;
        }
        
        return null;
    }
    
    private Node getNode(Entity entity) {
        int id = entity.getId();
        Node node = nodes.get(id);
 
        if(node == null) {
            node = new Node(entity.toString());
            nodes.put(id, node);
            entities.put(node, entity);
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
                Log.info("BindTo " + childNode.getName() + " -> " + parentNode.getName());
                return null;
            }
        });
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

        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

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
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        
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
        getNode(entity);
    }
    
    public void setupShip(Entity entity, ShipGeometry data) {
        Log.info("" + entity.toString());

        BlockTerrainControl blockControl = new BlockTerrainControl(ClientGlobals.cubesSettings, data.map.getChunkSizes());
        for(int x = 0; x <= data.map.xsize; x++) {
            for(int y = 0; y <= data.map.ysize; y++) {
                for(int z = 0; z <= data.map.zsize; z++) {
                    if(data.map.get(x, y, z) == '#') {
                        blockControl.setBlock(x, y, z, Block_Wood.class);
                    }
                }
            }
        }
        
        Node offset = new Node("offset");
        offset.addControl(blockControl);
        //shipNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        node.attachChild(offset);
        offset.setLocalTranslation(data.map.getCenter().negateLocal());

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
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        Node burn = (Node) assetManager.loadModel("Scenes/testScene.j3o"); 
        ParticleEmitter emitter = (ParticleEmitter) burn.getChild("Emitter");
        emitter.addControl(new EngineControl(entity));
        emitter.setEnabled(true);
        node.attachChild(burn);

        Geometry geom = new Geometry("", new Cylinder(16, 16, 0.5f, 1, true));
        geom.setMaterial(material(ColorRGBA.Gray, true));
        
        node.attachChild(geom);
    }
    
    public void setupMonitor(Entity entity, MonitorGeometry data) {
        Geometry geom = new Geometry(entity.toString(), new Quad(data.width, data.height, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);
//        tex.setMagFilter(Texture.MagFilter.Nearest);
//        tex.setMinFilter(Texture.MinFilter.Trilinear);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        node.attachChild(geom);
        geom.setLocalTranslation(-0.5f, -0.5f, 0f);

        ClientRaster rasterComponent = ClientGlobals.artemis.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    public void setupCharacter(Entity entity, CharacterGeometry data) {
        Material mat = material(new ColorRGBA(data.red, data.green, data.blue, data.alpha), true);

        Geometry body = new Geometry(entity.toString(), new Box(0.25f, 0.5f, 0.25f));
        body.setMaterial(mat);

        Geometry head = new Geometry(entity.toString(), new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(mat);
        head.setLocalTranslation(0, 1.0f, 0);
        
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

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

    public void setupExplosion(final Entity entity, final Explosion explosion) {
        final ExplosionNode node = new ExplosionNode("ExplosionFX");
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.info("Attached explosion node " + entity + " " + node);
                getNode(entity).attachChild(node);
                node.addControl(new ExplosionControl(explosion, node));
                return null;
            }
        });
    }
}
