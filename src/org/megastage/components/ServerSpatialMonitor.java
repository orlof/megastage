/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Element;


    
/**
 *
 * @author Teppo
 */
public class ServerSpatialMonitor extends BaseComponent {
    public Entity parent;
    
    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        this.parent = parent;
    }
}
