package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import org.megastage.components.Acceleration;
import org.megastage.components.GravityAcceleration;
import org.megastage.components.GravityField;
import org.megastage.components.Position;
import org.megastage.util.Globals;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GravityFieldSystem extends EntitySystem {
    @Mapper ComponentMapper<GravityField> GRAVITY_FIELD;
    @Mapper ComponentMapper<Position> POSITION;
    
    private ImmutableBag<Entity> gravityFieldEntities;

    public GravityFieldSystem() {
        super(Aspect.getAspectForAll(GravityField.class, Position.class));
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entityImmutableBag) {
        gravityFieldEntities = entityImmutableBag;
    }

    @Override
    protected boolean checkProcessing() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    public Vector getGravityField(Position coordinates) {
        Vector acceleration = new Vector();

        for(int i=0; i < gravityFieldEntities.size(); i++) {
            Entity entity = gravityFieldEntities.get(i);
            
            GravityField gravityField = GRAVITY_FIELD.get(entity);
            Position position = POSITION.get(entity);

            double dx = position.x - coordinates.x;
            double dy = position.y - coordinates.y;
            double dz = position.z - coordinates.z;

            double distanceSquared = dx*dx + dy*dy + dz*dz;

            double celestialMass = gravityField.mass;
            double gravitationalField = Globals.G * celestialMass / distanceSquared;

            double distance = Math.sqrt(distanceSquared);

            double multiplier = gravitationalField / distance;
            acceleration.add(multiplier * dx, multiplier * dy, multiplier * dz);
        }

        return acceleration;
    }    
}
