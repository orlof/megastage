package org.megastage.server;

import com.cubes.Vector3Int;
import com.jme3.math.Vector3f;

public class ShipStructureHit extends Hit {
    public int eid;
    public final Vector3Int block;
    public final Vector3f coord;

    public ShipStructureHit(Target target, Vector3Int block, Vector3f coord, double distance) {
        super(distance);
        this.eid = target.eid;
        this.block = block;
        this.coord = coord;
    }

    @Override
    public String toString() {
        return "ShipStructureHit(distance=" + distance + ", block=" + block.toString() + ", coord=" + coord.toString() +")";
    }
}
