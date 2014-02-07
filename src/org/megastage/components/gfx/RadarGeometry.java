package org.megastage.components.gfx;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
    
/**
 *
 * @author Orlof
 */
public class RadarGeometry extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.setupRadar(entity, this);
    }
    
    @Override
    public boolean replicate() {
        return true;
    }
}
