package org.megastage.client;

import org.megastage.client.controls.ExplosionControl;
import com.esotericsoftware.minlog.Log;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.ImposterPositionControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.gfx.PlanetGeometry;
import org.megastage.components.gfx.SunGeometry;
import org.megastage.components.client.NodeComponent;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
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
    
}
