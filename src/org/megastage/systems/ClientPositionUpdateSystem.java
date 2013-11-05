/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.ClientSpatial;

/**
 *
 * @author Teppo
 */
public class ClientPositionUpdateSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<ClientSpatial> spatialComponentMapper;

    public ClientPositionUpdateSystem() {
        super(Aspect.getAspectForAll(Position.class, ClientSpatial.class));
    }

    @Override    
    protected void process(Entity entity) {
        Position position = positionMapper.get(entity);
        ClientSpatial spatialComponent = spatialComponentMapper.get(entity);
        spatialComponent.geom.setLocalTranslation(position.x, position.y, position.z);
    }

}
