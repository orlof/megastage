package org.megastage.components;

import com.jme3.math.Vector3f;
import org.megastage.ecs.ReplicatedComponent;

public class Velocity extends ReplicatedComponent {
    public Vector3f vector = Vector3f.ZERO;
}
