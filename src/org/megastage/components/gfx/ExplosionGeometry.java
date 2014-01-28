/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.gfx;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;

/**
 *
 * @author Orlof
 */
public class ExplosionGeometry extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
        Log.info(entity.toString());
        ClientGlobals.spatialManager.setupExplosion(entity, this);
    }

    private boolean synch = false;
    @Override
    public boolean synchronize() {
        if(!synch) {
            synch = true;
            return true;
        }
        return false;
    }
}
