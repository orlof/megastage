package org.megastage.client;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class EntityNode extends Node {
    public int eid = 0;
    public Node offset = null;

    public EntityNode(int eid) {
        super(ID.get(eid));
        this.eid = eid;
        
        attachOffset();
    }
    
    public void reset() {
        while(getNumControls() > 0) {
            removeControl(getControl(0));
        }
        
        detachAllChildren();
        attachOffset();
    }
    
    private void attachOffset() {
        offset = new Node("[" + eid + "] offset");
        attachChild(offset);
    }
    
    public void setOffset(Vector3f val) {
        offset.setLocalTranslation(val.negateLocal());
    }

    public boolean isUsable() {
        return World.INSTANCE.hasComponent(eid, CompType.UsableFlag);
    }

    public boolean isShip() {
        return World.INSTANCE.hasComponent(eid, CompType.ShipGeometry);
    }

    public boolean isPlayerBase() {
        return eid == ClientGlobals.baseEntity;
    }

    public <T extends Control> T getControl(Class<T> type) {
        return getControl(this, type);
    }
    
    private static <T extends Control> T getControl(Spatial spatial, Class<T> type) {
        for(int i = spatial.getNumControls(); i >= 0; i--) {
            Control c = spatial.getControl(i);
            if(type.isInstance(c)) {
                return type.cast(c);
            }
        }

        if(spatial instanceof Node) {
            Node node = (Node) spatial;
            
            for(Spatial child: node.getChildren()) {
                Control c = getControl(child, type);
                if(c != null) {
                    return type.cast(c);
                }
            }
        }

        return null;
    }
}
