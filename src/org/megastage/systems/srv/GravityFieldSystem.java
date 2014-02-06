package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.DeleteFlag;
import org.megastage.components.srv.GravityFieldFlag;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.components.srv.UninitializedFlag;
import org.megastage.util.Globals;
import org.megastage.util.Vector3d;

public class GravityFieldSystem extends EntitySystem {
    ComponentMapper<GravityFieldFlag> GRAVITY_FIELD;
    ComponentMapper<Position> POSITION;
    ComponentMapper<Mass> MASS;
    
    private Array<Entity> entitiesWithGravityField;

    public GravityFieldSystem() {
        super(Aspect.getAspectForAll(GravityFieldFlag.class, Position.class, Mass.class));
    }

    @Override
    public void initialize() {
        
        GRAVITY_FIELD = world.getMapper(GravityFieldFlag.class);
        POSITION = world.getMapper(Position.class);
        MASS = world.getMapper(Mass.class);
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        entitiesWithGravityField = entities;
        Log.trace("Number of Gravity fields: " + entitiesWithGravityField.size);
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
    
    public Vector3d getGravityField(Position coordinates) {
        Log.trace("Calculating gravity field in position " + coordinates.toString());
        Vector3d acc = new Vector3d();

        for(Entity entity: entitiesWithGravityField) {
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
