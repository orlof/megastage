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
public class VoidGeometry extends EntityComponent {

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        ClientGlobals.spatialManager.setupVoidNode(entity, this);
    }
    
    public String toString() {
        return "VoidGeometry()";
    }
}
