/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.EntityComponent;
import org.megastage.protocol.Network;
import org.megastage.systems.ClientNetworkSystem;


    
/**
 *
 * @author Teppo
 */
public class SunGeometry extends EntityComponent {
    public float radius;
    public int color;
    public float lightRadius;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 10.0f);
        lightRadius = getFloatValue(element, "light_radius", 2000000.0f);
        color = getIntegerValue(element, "color_rgba", 0xffffff00); 
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.csms.setupSunLikeBody(entity, this);
    }
    
    public String toString() {
        return "SunGeometry(" + radius + ", " + color + ", " + lightRadius + ")";
    }
}
