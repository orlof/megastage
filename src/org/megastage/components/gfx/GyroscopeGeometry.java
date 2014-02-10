package org.megastage.components.gfx;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
    
/**
 *
 * @author Orlof
 */
public class GyroscopeGeometry extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.setupGyroscope(entity, this);
    }
    
    @Override
    public boolean replicate() {
        return true;
    }
}
