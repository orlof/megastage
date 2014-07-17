package org.megastage.client;

import org.megastage.util.Log;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.megastage.client.controls.ImposterPositionControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.client.NodeComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class SpatialManager {
    public static EntityNode getOrCreateNode(int eid) {
        NodeComponent comp = (NodeComponent) World.INSTANCE.getOrCreateComponent(eid, CompType.NodeComponent, NodeComponent.class);
 
        if(comp.node == null) {
            comp.node = new EntityNode(eid);
        }
        
        return comp.node;
    }
    
    private static int getEntity(Node node) {
        if(node instanceof EntityNode) {
            EntityNode eNode = (EntityNode) node;
            return eNode.eid;
        }
        
        return 0;
    }
    

    // NOT VERIFIED
    
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
        
        if(ship != 0 && ship != ClientGlobals.playerEntity) {
            Log.info(ID.get(ship));

            // ship node
            EntityNode shipNode = getOrCreateNode(ship);
            ClientGlobals.globalRotationNode.attachChild(shipNode);
            shipNode.getControl(RotationControl.class).setEnabled(true);

            ClientGlobals.playerParentEntity = ClientGlobals.playerEntity;
            ClientGlobals.rootNode.attachChild(ClientGlobals.playerNode);

//            Rotation rot = (Rotation) World.INSTANCE.getComponent(ship, CompType.Rotation);
//            rot.setDirty(true);
//
//            Position pos = (Position) World.INSTANCE.getComponent(ship, CompType.Position);
//            pos.setDirty(true);
        }
    }

    private static void enterShip(int shipEid) {
        Log.info(ID.get(shipEid));

        ClientGlobals.playerParentEntity = shipEid;

        EntityNode shipNode = getOrCreateNode(shipEid);
        ClientGlobals.rootNode.attachChild(shipNode);
        shipNode.offset.attachChild(ClientGlobals.playerNode);
    }

    public static void imposter(int eid, boolean gfxVisible) {
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
    
    private static boolean draw(Spatial s) {
        return s.getCullHint() != Spatial.CullHint.Always;
    }
}
