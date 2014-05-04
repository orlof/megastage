package org.megastage.server;

import com.cubes.Vector3Int;
import org.megastage.util.Vector3d;

public class ShipStructureHit extends Hit {
    public int eid;
    public final Vector3Int block;
    public final Vector3d coord;

    public ShipStructureHit(Target target, Vector3Int block, Vector3d coord, double distance) {
        super(distance);
        this.eid = target.eid;
        this.block = block;
        this.coord = coord;
    }

    public String toString() {
        return "ShipStructureHit(distance=" + distance + ", block=" + block.toString() + ", coord=" + coord.toString() +")";
    }
}
