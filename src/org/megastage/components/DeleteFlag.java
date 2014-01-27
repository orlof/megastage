/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;

/**
 *
 * @author teppo
 */
public class DeleteFlag extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
        Bag<Component> bag = entity.getComponents(new Bag<Component>(20));
        for(int i=0; i < bag.size(); i++) {
            Component c = bag.get(i);
            if(c instanceof BaseComponent) {
                BaseComponent bc = (BaseComponent) c;
                bc.delete(pc, entity);
            }
        }
        ClientGlobals.artemis.world.deleteEntity(entity);
    }
}
