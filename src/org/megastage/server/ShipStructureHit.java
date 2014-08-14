package org.megastage.server;

import com.cubes.Vector3Int;

public class ShipStructureHit extends Hit {
    public int eid;
    public final Vector3Int block;

    public ShipStructureHit(Vector3Int block, float distance) {
        super(distance);
        this.block = block;
    }

    @Override
    public String toString() {
        return String.format("ShipStructureHit(distance=%s, block=%s)", distance, block);
    }
}
