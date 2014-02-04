package org.megastage.components;

import org.megastage.util.Vector3d;

public class PrevPosition extends BaseComponent {
    public long x, y, z, time;
    
    public PrevPosition() {
        super();
    }

    public Vector3d getVelocity(Vector3d cur) {
        double dt = 0.001 * time;
        return new Vector3d(dt * (cur.x - x), dt * (cur.y - y), dt * (cur.z - z));
    }
    
    @Override
    public String toString() {
        return "PrevPosition(" + x + ", " + y + ", " + z + ")";
    }
}
