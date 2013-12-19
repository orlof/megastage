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
import org.megastage.util.ClientGlobals;


    
/**
 *
 * @author Orlof
 */
public class EngineGeometry extends EntityComponent {
    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        if(entity != ClientGlobals.playerEntity) {
            ClientGlobals.spatialManager.setupEngine(entity, this);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayerGeometry()");
        return sb.toString();
    }
}
