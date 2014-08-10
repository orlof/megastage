package org.megastage.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.ecs.BaseComponent;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Rotation extends ReplicatedComponent {

    public static Quaternion getWorldRotation(int eid) throws ECSException {
        Rotation rot = (Rotation) World.INSTANCE.getComponentOrError(eid, CompType.Rotation);
        return rot.getWorldRot(eid);
    }
    private Quaternion value = new Quaternion();

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        float x = (float) Math.toRadians(getFloatValue(element, "x", 0.0f));
        float y = (float) Math.toRadians(getFloatValue(element, "y", 0.0f));
        float z = (float) Math.toRadians(getFloatValue(element, "z", 0.0f));

        value = new Quaternion().fromAngles(x, y, z);
        
        return null;
    }

    @Override
    public void receive(int eid) {
        if(eid == ClientGlobals.playerEntity) {
            if(World.INSTANCE.hasComponent(eid, CompType.Rotation)) {
                return;
            }
        }

        World.INSTANCE.setComponent(eid, CompType.Rotation, this);
    }

    public float[] toAngles(float[] angles) {
        return value.toAngles(angles);
    }

    public void fromAngles(float[] eulerAngles) {
        value.fromAngles(eulerAngles);
        setDirty(true);
    }

    public Quaternion get() {
        return value;
    }

    public Vector3f rotateLocal(Vector3f vec) {
        return value.multLocal(vec);
    }

    public void set(Quaternion rotation) {
        if(!value.equals(rotation)) {
            value.set(rotation);
            setDirty(true);
        }
    }

    public Vector3f rotate(Vector3f vec) {
        return value.mult(vec);
    }

    public void add(Quaternion rot) {
        value = rot.multLocal(value).normalizeLocal();
        setDirty(true);
    }
    
    public Quaternion getWorldRot(int eid) throws ECSException {
        Quaternion q = value.clone();
        
        BindTo bindTo = (BindTo) World.INSTANCE.getComponent(eid, CompType.BindTo);
        while(bindTo != null) {
            assert bindTo.parent > 0;
            
            ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(bindTo.parent, CompType.ShipGeometry);
            if(sg != null) {
                Rotation shipRot = (Rotation) World.INSTANCE.getComponentOrError(bindTo.parent, CompType.Rotation);
                q.multLocal(shipRot.value);
                return q;
            }
            
            bindTo = (BindTo) World.INSTANCE.getComponent(bindTo.parent, CompType.BindTo);
        }
        
        return q;
    }
}
