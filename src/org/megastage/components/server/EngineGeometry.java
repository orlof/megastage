/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;


    
/**
 *
 * @author Orlof
 */
public class EngineGeometry extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
        if(entity != ClientGlobals.playerEntity) {
            ClientGlobals.spatialManager.setupEngine(entity, this);
        }
    }
    
    @Override
    public boolean replicate() {
        return true;
    }
}
