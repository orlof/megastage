package org.megastage.client;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class EntityNode extends Node {
    public int eid = 0;
    public Node offset = null;

    public EntityNode(int eid) {
        super(ID.get(eid));
        this.eid = eid;
        
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

}
