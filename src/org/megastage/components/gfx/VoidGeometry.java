/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.gfx;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;


    
/**
 *
 * @author Orlof
 */
public class VoidGeometry extends BaseComponent {

    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.setupVoidNode(entity, this);
    }
    
    public String toString() {
        return "VoidGeometry()";
    }
}
