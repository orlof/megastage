package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.GravityField;
import org.megastage.components.Mass;
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
    @Mapper ComponentMapper<Mass> MASS;
    
    private ImmutableBag<Entity> gravityFieldEntities;

    public GravityFieldSystem() {
        super(Aspect.getAspectForAll(GravityField.class, Position.class, Mass.class));
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entityImmutableBag) {
        gravityFieldEntities = entityImmutableBag;
        Log.debug("Number of Gravity fields: " + gravityFieldEntities.size());
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
    
    public Vector getGravityField(Position coordinates) {
        Log.debug("Calculating gravity field in position " + coordinates.toString());
        Vector acc = new Vector();

        for(int i=0; i < gravityFieldEntities.size(); i++) {
            Entity entity = gravityFieldEntities.get(i);
            
            Position position = POSITION.get(entity);
            Mass mass = MASS.get(entity);
            
            Log.debug(entity.toString() + " position " + position.toString() + " mass " + mass.toString());

            double dx = (position.x - coordinates.x) / 1000.0;
            double dy = (position.y - coordinates.y) / 1000.0;
            double dz = (position.z - coordinates.z) / 1000.0;
            
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double gravitationalField = Globals.GRAVITY_G * mass.mass / distanceSquared;
            double distance = Math.sqrt(distanceSquared);
            double multiplier = gravitationalField / distance;

            acc = acc.add(multiplier * dx, multiplier * dy, multiplier * dz);
            
            Log.debug("Acceleration: " + acc.toString());
        }

        return acc;
    }    
}
