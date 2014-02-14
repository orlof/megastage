/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.util.ID;

public class RadarTargetData extends BaseComponent {
    public int target;

    @Override
    public void receive(Connection pc, Entity entity) {
        Entity targetEntity = ClientGlobals.artemis.toClientEntity(target);

        target = targetEntity.id;

        super.receive(pc, entity);
    }
    
    public String toString() {
        return "RadarTargetData(target=" + target + ")";
    }
}
