/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;

public class EngineData extends BaseComponent {
    public char power = 0;

    public void receive(Connection pc, Entity entity) {
        Log.info("" +(int) power);
        entity.addComponent(this);
    }

}
