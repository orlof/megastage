/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

/**
 *
 * @author Teppo
 */
public class BindTo extends BaseComponent {
    public Entity ship; 
    
    public BindTo(Entity ship) {
        this.ship = ship;
    }
}
