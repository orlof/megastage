/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;

public class RadarTargetData extends BaseComponent {
    public int target;

    @Override
    public void receive(Connection pc, Entity entity) {
        target = ClientGlobals.artemis.toClientEntity(target).id;

        super.receive(pc, entity);
    }
    
    
}
