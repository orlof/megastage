/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

public class EngineData extends EntityComponent {
    public char power = 0;

    public void receive(Connection pc, Entity entity) {
        entity.addComponent(this);
    }
}
