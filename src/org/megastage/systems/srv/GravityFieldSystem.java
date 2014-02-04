package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.srv.GravityFieldFlag;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.util.Globals;
import org.megastage.util.Vector3d;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GravityFieldSystem extends EntitySystem {
    @Mapper ComponentMapper<GravityFieldFlag> GRAVITY_FIELD;
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<Mass> MASS;
    
    private ImmutableBag<Entity> entitiesWithGravityField;

    public GravityFieldSystem() {
        super(Aspect.getAspectForAll(GravityFieldFlag.class, Position.class, Mass.class));
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        entitiesWithGravityField = entities;
        Log.trace("Number of Gravity fields: " + entitiesWithGravityField.size());
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
    
    public Vector3d getGravityField(Position coordinates) {
        Log.trace("Calculating gravity field in position " + coordinates.toString());
        Vector3d acc = new Vector3d();

        for(int i=0; i < entitiesWithGravityField.size(); i++) {
            Entity entity = entitiesWithGravityField.get(i);
            
            Position position = POSITION.get(entity);
            Mass mass = MASS.get(entity);
            
            Log.trace(entity.toString() + " position " + position.toString() + " mass " + mass.toString());

            double dx = (position.x - coordinates.x) / 1000.0;
            double dy = (position.y - coordinates.y) / 1000.0;
            double dz = (position.z - coordinates.z) / 1000.0;
            
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double gravitationalField = Globals.G * mass.mass / distanceSquared;
            double distance = Math.sqrt(distanceSquared);
            double multiplier = gravitationalField / distance;

            acc = acc.add(multiplier * dx, multiplier * dy, multiplier * dz);
            
            Log.trace("Acceleration: " + acc.toString());
        }

        return acc;
    }    
}
