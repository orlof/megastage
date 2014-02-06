/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;

/**
 *
 * @author Orlof
 */
public class DeleteFlag extends BaseComponent {
    @Override
    public void receive(Connection pc, Entity entity) {
        Array<Component> bag = new Array<>(20);
        entity.getComponents(bag);
        for(Component c: bag) {
            if(c instanceof BaseComponent) {
                BaseComponent bc = (BaseComponent) c;
                bc.delete(pc, entity);
            }
        }
        entity.deleteFromWorld();
    }
}
