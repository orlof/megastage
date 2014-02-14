package org.megastage.components.gfx;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
    
/**
 *
 * @author Orlof
 */
public class EngineGeometry extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
//        if(entity != ClientGlobals.playerEntity) {
//            ClientGlobals.spatialManager.setupEngine(entity, this);
//        }
        ClientGlobals.spatialManager.setupEngine(entity, this);
    }
    
    @Override
    public boolean replicate() {
        return true;
    }
}
