package org.megastage.components.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network;
import org.megastage.server.Hit;

class VectorAttackData extends BaseComponent {
    public boolean enabled;
    public Hit hit;
    
    public static VectorAttackData create(VectorAttack va) {
        VectorAttackData vad = new VectorAttackData();
        vad.enabled = va.enabled;
        vad.hit = va.hit;
        return vad;
    }
}


public class VectorAttack extends VectorAttackData {
    public static Vector3f getVector(int eid) throws ECSException {
        VectorAttack va = (VectorAttack) World.INSTANCE.getComponentOrError(eid, CompType.VectorAttack);
        return va.maxVector;
    }

    public Vector3f maxVector;
    public float damageRate;

    public VectorAttack(Vector3f vector) {
        this.maxVector = vector;
    }

    @Override
    public Message synchronize(int eid) {
        VectorAttackData vad = new VectorAttackData();
        return new Network.ComponentMessage(eid, VectorAttackData.create(this));
    }
}
