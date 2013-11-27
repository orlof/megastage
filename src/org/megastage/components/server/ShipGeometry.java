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
    public String hull;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        size = getIntegerValue(element, "size", 20);
        hull = getStringValue(element, "hull", "cube");
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        // center = system.cems.get(center).getId();
        System.out.println("RECEIVED SHIP GEOM");
        system.csms.setupShip(entity, this);
    }
    
    public String toString() {
        return "ShipGeometry(hull='" + hull + "', size=" + size + ")";
    }
}
