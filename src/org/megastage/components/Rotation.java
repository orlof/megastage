package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Rotation extends BaseComponent {
    public double x=0, y=0, z=0, w=1;
    public boolean dirty = true;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        double x = Math.toRadians(getDoubleValue(element, "x", 0.0));
        double y = Math.toRadians(getDoubleValue(element, "y", 0.0));
        double z = Math.toRadians(getDoubleValue(element, "z", 0.0));

        Quaternion q = rotate(getQuaternion4d(), Vector3d.UNIT_Y, y);
        q = rotate(q, Vector3d.UNIT_Z, z);
        q = rotate(q, Vector3d.UNIT_X, x);
        set(q);
        
        return null;
    }

    public boolean synchronize() {
        return true;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        Rotation rot = Mapper.ROTATION.get(entity);
        if(rot == null) {
            super.receive(pc, entity);
            return;
        }

        if(entity == ClientGlobals.playerEntity) {
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
        x = q.x;
        y = q.y;
        z = q.z;
        w = q.w;
    }
    
    public void set(com.jme3.math.Quaternion q) {
        x = q.getX();
        y = q.getY();
        z = q.getZ();
        w = q.getW();

        dirty = true;
    }
    
    public void set(Rotation rot) {
        if(x != rot.x || y != rot.y || z != rot.z || w != rot.w) {
            x = rot.x;
            y = rot.y;
            z = rot.z;
            w = rot.w;

            dirty = true;
        }
    }
}
