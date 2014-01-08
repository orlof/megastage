/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.util.ClientGlobals;

public class EngineData extends EntityComponent {
    public char power = 0;

    @Override
    public void receive(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.updateEngine(entity, this);
    }
    
}
