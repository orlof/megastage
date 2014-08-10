package org.megastage.components.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.World;

public class VectorAttack extends BaseComponent {
    public Vector3f vector;
    public boolean enabled;

    public float damageRate;

    public VectorAttack(Vector3f vector) {
        this.vector = vector;
    }
    
    public static Vector3f getVector(int eid) throws ECSException {
        VectorAttack va = (VectorAttack) World.INSTANCE.getComponentOrError(eid, CompType.VectorAttack);
        return va.vector;
    }

    public boolean isEnabled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
