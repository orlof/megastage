package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
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

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        double x = Math.toRadians(getDoubleValue(element, "x", 0.0));
        double y = Math.toRadians(getDoubleValue(element, "y", 0.0));
        double z = Math.toRadians(getDoubleValue(element, "z", 0.0));

        Quaternion q = rotate(getQuaternion(), Vector3d.UNIT_Y, y);
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
        Rotation rot = entity.getComponent(Rotation.class);
        if(rot == null) {
            entity.addComponent(this);
            entity.changedInWorld();
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

    public Quaternion getQuaternion() {
        return new Quaternion(w, x, y, z);
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
    
    public void set(Rotation rot) {
        x = rot.x;
        y = rot.y;
        z = rot.z;
        w = rot.w;
    }
}
