/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.EntityComponent;
import org.megastage.systems.ClientNetworkSystem;


    
/**
 *
 * @author Teppo
 */
public class ShipGeometry extends EntityComponent {
    public int size;

    public int entry_x;
    public int entry_y;
    public int entry_z;
    
    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        size = getIntegerValue(element, "size", 16);
        
        entry_x = getIntegerValue(element, "entry_x", 8);
        entry_y = getIntegerValue(element, "entry_y", 2);
        entry_z = getIntegerValue(element, "entry_z", 8);
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.cems.setComponent(entity, this);
        system.csms.setupShip(entity, this);
    }
    
    public String toString() {
        return "ShipGeometry()";
    }
}
