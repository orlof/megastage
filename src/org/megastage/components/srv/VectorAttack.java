package org.megastage.components.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class VectorAttack extends ReplicatedComponent {
    public static Vector3f getVector(int eid) throws ECSException {
        VectorAttack va = (VectorAttack) World.INSTANCE.getComponentOrError(eid, CompType.VectorAttack);
        return va.vector;
    }

    public transient Vector3f maxVector;
    public transient boolean enabled;
    public transient float damageRate;

    public Vector3f vector;

    public VectorAttack(Vector3f vector) {
        this.maxVector = vector;
    }
    
    public void setVector(Vector3f vector) {
        if(!this.vector.equals(vector)) {
            this.vector = vector;
            setDirty(dirty);
        }
    }
}
