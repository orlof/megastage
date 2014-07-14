package org.megastage.client;

import org.megastage.client.controls.ExplosionControl;
import com.cubes.Block;
import com.cubes.BlockTerrainControl;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.AxisRotationControl;
import org.megastage.client.controls.EngineControl;
import org.megastage.client.controls.ForceFieldControl;
import org.megastage.client.controls.GyroscopeControl;
import org.megastage.client.controls.ImposterPositionControl;
import org.megastage.client.controls.LookAtControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RandomSpinnerControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.client.controls.ThermalLaserControl;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.gfx.MonitorGeometry;
import org.megastage.components.gfx.PlanetGeometry;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.components.gfx.EngineGeometry;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.gfx.SunGeometry;
import org.megastage.components.UsableFlag;
import org.megastage.components.gfx.VoidGeometry;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.client.NodeComponent;
import org.megastage.components.gfx.GeometryComponent;
import org.megastage.components.gfx.BatteryGeometry;
import org.megastage.components.gfx.FloppyDriveGeometry;
import org.megastage.components.gfx.ForceFieldGeometry;
import org.megastage.components.gfx.GyroscopeGeometry;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.components.gfx.PPSGeometry;
import org.megastage.components.gfx.PowerPlantGeometry;
import org.megastage.components.gfx.RadarGeometry;
import org.megastage.components.gfx.ThermalLaserGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Cube3dMap.BlockChange;
import org.megastage.util.ID;

public class SpatialManager {
    public static EntityNode getOrCreateNode(int eid) {
        EntityNode node = getNode(eid);
 
        if(node == null) {
            node = createNode(eid);
        }
        
        return node;
    }
    
    private static int getEntity(Node node) {
        if(node instanceof EntityNode) {
            EntityNode eNode = (EntityNode) node;
            return eNode.eid;
        }
        
        return 0;
    }
    
    private static EntityNode getNode(int eid) {
        NodeComponent nodeComponent = (NodeComponent) World.INSTANCE.getComponent(eid, CompType.NodeComponent);
        return nodeComponent.node;
    }
    
    private static EntityNode createNode(int eid) {
        EntityNode node = new EntityNode(eid);
        node.attachChild(new Node("offset"));

        World.INSTANCE.setComponent(eid, CompType.NodeComponent, new NodeComponent(node));
        
        return node;
    }
    

    
    
    
    public static int getUsableEntity(Node node, boolean onlyUsable) {
        int eid = getEntity(node);
        if(eid == 0) {
            return 0;
        }

        if(onlyUsable) {
            boolean hasComponent = World.INSTANCE.hasComponent(eid, CompType.UsableFlag);
            return hasComponent ? eid: 0;
        }
        
        return eid;
    }
    
    public static void changeShip(int ship) {
        leaveShip();
        enterShip(ship);
    }

    private static void leaveShip() {
        int ship = ClientGlobals.playerParentEntity;
        
        if(ship != 0) {
            Log.info(ID.get(ship));

            Node shipNode = getOrCreateNode(ship);
            ClientGlobals.globalRotationNode.attachChild(shipNode);
            ClientGlobals.playerParentEntity = 0;
            
//            Rotation rot = (Rotation) World.INSTANCE.getComponent(ship, CompType.Rotation);
//            rot.setDirty(true);
//
//            Position pos = (Position) World.INSTANCE.getComponent(ship, CompType.Position);
//            pos.setDirty(true);
//            
//            ClientGlobals.playerParentNode.attachChild(ClientGlobals.playerNode);
        }
    }

    private static void enterShip(int shipEid) {
        Log.info(ID.get(shipEid));

        ClientGlobals.playerParentEntity = shipEid;

        Node shipNode = getOrCreateNode(shipEid);
        ClientGlobals.playerParentNode.attachChild(shipNode);
        //attach(shipNode, ClientGlobals.playerNode, true);
        ClientGlobals.playerNode.setLocalTranslation(0, 0, 0);
    }

    
    
    
    private Geometry createSphere(float radius, ColorRGBA color, boolean shaded) {
        Sphere mesh = new Sphere(
                ClientGlobals.gfxSettings.SPHERE_Z_SAMPLES,
                ClientGlobals.gfxSettings.SPHERE_RADIAL_SAMPLES, 
                radius);
        
        Geometry geom = new Geometry("gfx");
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

    public void setupSunLikeBody(final int eid, final SunGeometry data) {
        ColorRGBA colorRGBA = new ColorRGBA(data.red, data.green, data.blue, data.alpha);

        final Node node = getOrCreateNode(eid);
        node.addControl(new PositionControl(eid));
        node.addControl(new RotationControl(eid));
        
        attach(node, createSphere(data.radius, colorRGBA, false), true);

        final PointLight light = new PointLight();
        light.setColor(colorRGBA);
        light.setRadius(data.lightRadius);

        LightNode lightNode = new LightNode(eid + " LightNode", light);
        attach(node, lightNode, true);

        ClientGlobals.rootNode.addLight(light);
        ClientGlobals.globalRotationNode.attachChild(node);
    }

    public void setupPlanetLikeBody(final int eid, PlanetGeometry data) {
        // Add planet
        final Node node = getOrCreateNode(eid);

        final PositionControl positionControl = new PositionControl(eid);
        final RotationControl rotationControl = new RotationControl(eid);
        
        if(ClientGlobals.gfxSettings.ENABLE_PLANETS) {
            final Planet planet = createPlanet(data);

            attach(node, planet, true);

            PlanetAppState appState = app.getStateManager().getState(PlanetAppState.class);
            if(appState != null) appState.addPlanet(planet);

            node.addControl(positionControl);
            node.addControl(rotationControl);

            if(node.getParent() == null) {
                ClientGlobals.globalRotationNode.attachChild(node);
            }
        } else {
            ColorRGBA color = null;
            try {
                color = (ColorRGBA) ColorRGBA.class.getDeclaredField(data.color).get(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            final Geometry geom = createSphere(data.radius, color, true);
            attach(node, geom, true);

            node.addControl(positionControl);
            node.addControl(rotationControl);

            if(node.getParent() == null) {
                ClientGlobals.globalRotationNode.attachChild(node);
            }
        }
    }
    

    private Planet createPlanet(PlanetGeometry data) {
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


    public void setupExplosion(final int eid) {
        ExplosionNode explosionNode = new ExplosionNode("ExplosionFX");
        explosionNode.addControl(new ExplosionControl(eid));

        Node node = getOrCreateNode(eid);
        attach(node, explosionNode, false);
    }

    
    public void imposter(int eid, boolean gfxVisible) {
        Node node = getOrCreateNode(eid);

        for(Spatial s: node.getChildren()) {
            if(s.getName().equals("imposter")) {
                boolean imposterVisible = !gfxVisible;
                boolean imposterDraw = draw(s);
                if(!imposterVisible && imposterDraw) {
                    s.setCullHint(Spatial.CullHint.Always);
                    s.getParent().getControl(ImposterPositionControl.class).setEnabled(false);
                    s.getParent().getControl(PositionControl.class).setEnabled(true);
                } else if(imposterVisible && !imposterDraw) {
                    s.setCullHint(Spatial.CullHint.Inherit);
                    s.getParent().getControl(ImposterPositionControl.class).setEnabled(true);
                    s.getParent().getControl(PositionControl.class).setEnabled(false);
                }
            } else {
                boolean gfxDraw = draw(s);
                if(gfxVisible && !gfxDraw) {
                    s.setCullHint(Spatial.CullHint.Inherit);
                } else if(!gfxVisible && gfxDraw) {
                    s.setCullHint(Spatial.CullHint.Always);
                }
            }
        }
    }
    
    private boolean draw(Spatial s) {
        return s.getCullHint() != Spatial.CullHint.Always;
    }
    
    private Geometry createImposter(float size, ColorRGBA color) {
        Mesh q = new Mesh();

        Vector3f [] vertices = new Vector3f[] { new Vector3f(0,0,0) };
        q.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

        q.setMode(Mesh.Mode.Points);
        q.setPointSize(size);
        q.updateBound();
        q.setStatic();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
        Geometry geom = new Geometry("imposter", q);
        geom.setMaterial(mat);

        return geom;
    }

    public void setupImposter(final int eid, final ImposterGeometry data) {
        ColorRGBA colorRGBA = new ColorRGBA(data.red, data.green, data.blue, data.alpha);

        final Geometry imposter = createImposter(data.radius, colorRGBA);
        final Node node = getOrCreateNode(eid);
        
        node.addControl(new ImposterPositionControl(eid));
        node.attachChild(imposter);
    }

}
