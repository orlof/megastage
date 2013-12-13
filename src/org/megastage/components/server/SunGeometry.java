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
    public float red, green, blue, alpha;
    public float lightRadius;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 10.0f);
        lightRadius = getFloatValue(element, "light_radius", 2000000.0f);
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.csms.setupSunLikeBody(entity, this);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunGeometry(");
        sb.append("radius=").append(radius);
        sb.append(", light_radius=").append(lightRadius);
        sb.append(", red=").append(red);
        sb.append(", green=").append(green);
        sb.append(", blue=").append(blue);
        sb.append(", alpha=").append(alpha);
        sb.append(")");
        return sb.toString();
    }
}
