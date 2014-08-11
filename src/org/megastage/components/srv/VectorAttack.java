package org.megastage.components.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.server.Hit;

public class VectorAttack extends ReplicatedComponent {
    public static Vector3f getVector(int eid) throws ECSException {
        VectorAttack va = (VectorAttack) World.INSTANCE.getComponentOrError(eid, CompType.VectorAttack);
        return va.maxVector;
    }

    public transient Vector3f maxVector;
    public transient float damageRate;

    public boolean enabled;
    public Hit hit;

    public VectorAttack(Vector3f vector) {
        this.maxVector = vector;
    }
}
