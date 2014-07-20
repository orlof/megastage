package org.megastage.components;

import com.jme3.math.Vector3f;
import org.megastage.ecs.ReplicatedComponent;

public class Velocity extends ReplicatedComponent {
    private Vector3f vector = new Vector3f();

    public void set(Vector3f vec) {
        vector.set(vec);
    }

    public Vector3f get() {
        return vector;
    }

    public void accelerate(Vector3f acceleration) {
        vector.addLocal(acceleration);
    }
    
    public Vector3f getDisplacement(float dt) {
        return vector.mult(dt);
    }
}
