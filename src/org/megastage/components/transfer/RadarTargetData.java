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
        if(target > 0) {
            Entity targetEntity = ClientGlobals.artemis.toClientEntity(target);
            target = targetEntity.id;
        }
        
        super.receive(pc, entity);
    }
    
    public String toString() {
        return "RadarTargetData(target=" + target + ")";
    }
}
