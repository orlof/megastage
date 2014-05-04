package org.megastage.components;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class Rotation extends BaseComponent {
    public double x=0, y=0, z=0, w=1;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        double x = Math.toRadians(getDoubleValue(element, "x", 0.0));
        double y = Math.toRadians(getDoubleValue(element, "y", 0.0));
        double z = Math.toRadians(getDoubleValue(element, "z", 0.0));

        Quaternion q = rotate(getQuaternion4d(), Vector3d.UNIT_Y, y);
        q = rotate(q, Vector3d.UNIT_Z, z);
        q = rotate(q, Vector3d.UNIT_X, x);
        set(q);
        
        return null;
    }

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }

    @Override
    public Message synchronize(int eid) {
        return ifDirty(eid);
    }
    
    @Override
    public void receive(World world, Connection pc, int eid) {
        Rotation rot = (Rotation) world.getComponent(eid, CompType.Rotation);
        if(rot == null) {
            super.receive(world, pc, eid);
            return;
        }

        if(eid == ClientGlobals.playerEntity) {
            return;
        }

        rot.set(this);
    }
    
    public String toString() {
        return "Rotation(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    public Quaternion getQuaternion4d() {
        return new Quaternion(w, x, y, z);
    }
    
    public com.jme3.math.Quaternion getQuaternion3f() {
        return new com.jme3.math.Quaternion((float) x, (float) y, (float) z, (float) w);
    }
    
    public static Quaternion rotate(Quaternion q, Vector3d axis, double radians) {
        if(radians == 0.0) {
            return q;
        } 
        return q.localRotation(axis, radians).normalize();
    }
    
    public void set(Quaternion q) {
        set(q.x, q.y, q.z, q.w);
    }
    
    public void set(com.jme3.math.Quaternion q) {
        set(q.getX(), q.getY(), q.getZ(), q.getW());
    }
    
    public void set(Rotation rot) {
        set(rot.x, rot.y, rot.z, rot.w);
    }
    
    public void set(double x, double y, double z, double w) {
        if(this.x != x || this.y != y || this.z != z || this.w != w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            dirty = true;
        }
    }
}
