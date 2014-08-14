package org.megastage.server;

import com.cubes.Vector3Int;
import com.jme3.math.Vector3f;

public class ShipStructureHit extends Hit {
    public int eid;
    public final Vector3Int block;
    public final Vector3f loc;

    public ShipStructureHit(Vector3Int block, float distance) {
        super(distance);
        this.block = block;
        this.loc = null;
    }

    public ShipStructureHit(Vector3f loc, float distance) {
        super(distance);
        this.block = null;
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "ShipStructureHit(distance=" + distance + ", block=" + block.toString() + ")";
    }
}
