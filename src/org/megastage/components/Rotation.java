package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Quaternion;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Rotation extends EntityComponent {
    public double x=0.0, y=0.0, z=0.0, w=1.0;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
    }

    public boolean isUpdated() {
        return true;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        if(entity == ClientGlobals.playerEntity) {
            if(entity.getComponent(Rotation.class) == null) {
                entity.addComponent(this);
            }
            return;
        }
        entity.addComponent(this);
    }
    
    public String toString() {
        return "Rotation(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    public Quaternion getQuaternion() {
        return new Quaternion(w, x, y, z);
    }
}
