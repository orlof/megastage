package org.megastage.components.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.BaseComponent;

public class Acceleration extends BaseComponent {
    private Vector3f vector = new Vector3f();

    public void jerk(Vector3f acc) {
        vector.addLocal(acc);
    }

    public void zero() {
        vector.zero();
    }

    public Vector3f getDeltaV(float dt) {
        return vector.mult(dt);
    }
}
