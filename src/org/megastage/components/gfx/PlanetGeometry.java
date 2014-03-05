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
import org.megastage.client.ClientGlobals;
import org.megastage.protocol.Message;


    
/**
 *
 * @author Orlof
 */
public class PlanetGeometry extends BaseComponent {
    public int center;
    public float radius;
    public String generator;
    public String color;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        center = parent.id;

        radius = getFloatValue(element, "radius", 10.0f);
        generator = getStringValue(element, "generator", "Earth");
        color = getStringValue(element, "color", "red");
        
        return null;
    }

    @Override
    public Message replicate(Entity entity) {
        return always(entity);
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        // center = ClientGlobals.artemis.get(center).getId();
        ClientGlobals.spatialManager.setupPlanetLikeBody(entity, this);
    }
    
    @Override
    public void delete(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.deleteEntity(entity);
        entity.deleteFromWorld();
    }
    
    public String toString() {
        return "PlanetGeometry(center=" + center + ", generator='" + generator + "', radius=" + radius + ")";
    }
}
