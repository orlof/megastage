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
import org.megastage.systems.ClientNetworkSystem;


    
/**
 *
 * @author Teppo
 */
public class PlanetGeometry extends EntityComponent {
    public float radius;
    public String generator;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 10.0f);
        generator = getStringValue(element, "generator", "Earth");
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.csms.setupPlanetLikeBody(entity, this);
    }
}
