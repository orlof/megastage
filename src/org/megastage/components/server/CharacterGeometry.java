/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.EntityComponent;
import org.megastage.util.ClientGlobals;


    
/**
 *
 * @author Orlof
 */
public class CharacterGeometry extends EntityComponent {
    public float red, green, blue, alpha;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        if(entity != ClientGlobals.playerEntity) {
            ClientGlobals.spatialManager.setupCharacter(entity, this);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayerGeometry(");
        sb.append("red=").append(red);
        sb.append(", green=").append(green);
        sb.append(", blue=").append(blue);
        sb.append(", alpha=").append(alpha);
        sb.append(")");
        return sb.toString();
    }
}
