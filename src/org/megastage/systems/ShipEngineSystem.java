package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Acceleration;
import org.megastage.components.Physical;
import org.megastage.components.ShipEngine;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShipEngineSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<ShipEngine> shipEngineMapper;
    @Mapper ComponentMapper<Physical> physicalMapper;
    @Mapper ComponentMapper<Acceleration> accelerationMapper;

    public ShipEngineSystem() {
        super(Aspect.getAspectForAll(ShipEngine.class));
    }

    @Override
    protected void process(Entity entity) {
        ShipEngine engine = shipEngineMapper.get(entity);

        if(engine.power != 0.0d) {
            double shipMass = physicalMapper.get(engine.ship).mass;
            
            Vector acceleration = engine.getAcceleration(shipMass);

            accelerationMapper.get(engine.ship).add(acceleration);
        }
    }
}
