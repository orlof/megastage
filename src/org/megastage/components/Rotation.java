package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.systems.ClientNetworkSystem;

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
    
    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.cems.setComponent(entity, this);
    }
    
    public String toString() {
        return "Rotation(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

}
