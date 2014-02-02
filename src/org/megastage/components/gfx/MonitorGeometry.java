/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.gfx;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;


    
/**
 *
 * @author Orlof
 */
public class MonitorGeometry extends BaseComponent {
    public float width, height;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        width = getFloatValue(element, "width", 3.0f);
        height = getFloatValue(element, "height", 2.0f);
        
        return null;
    }

    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.setupMonitor(entity, this);
    }
}
