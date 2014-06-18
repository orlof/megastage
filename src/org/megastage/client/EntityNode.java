package org.megastage.client;

import com.jme3.scene.Node;
import org.megastage.util.ID;

public class EntityNode extends Node {
    public int eid = 0;

    public EntityNode(int eid) {
        super(ID.get(eid));
        this.eid = eid;
    }
}
