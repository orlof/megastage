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
import org.megastage.util.ClientGlobals;


    
/**
 *
 * @author Teppo
 */
public class MonitorGeometry extends EntityComponent {
    public float width, height;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        width = getFloatValue(element, "width", 3.0f);
        height = getFloatValue(element, "height", 2.0f);
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        ClientGlobals.spatialManager.setupMonitor(entity, this);
    }
}
