package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.components.srv.GravityFieldFlag;
import org.megastage.components.srv.SphereOfInfluence;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

/**
 * Created with IntelliJ IDEA.
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 */
public class SphereOfInfluenceSystem extends EntitySystem {
    private long interval;
    private long acc;

    ComponentMapper<Position> POSITION;
    ComponentMapper<SphereOfInfluence> SPHERE_OF_INFLUENCE;

    public SphereOfInfluenceSystem(long interval) {
        super(Aspect.getAspectForAll(SphereOfInfluence.class, Position.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        
        SPHERE_OF_INFLUENCE = world.getMapper(SphereOfInfluence.class);
        POSITION = world.getMapper(Position.class);
    }

    @Override
    protected boolean checkProcessing() {
        if(Time.value >= acc) {
                acc = Time.value + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        Array<SOIData> soiBag = new Array<>(200);
        
        for(Entity entity: entities) {
            Position pos = POSITION.get(entity);
            SphereOfInfluence soi = SPHERE_OF_INFLUENCE.get(entity);

            soiBag.add(new SOIData(entity, pos, soi));
        }
        
        ServerGlobals.soi = soiBag;
    }

    public static class SOIData {
        public final Entity entity;

        public final double radius;
        public final Vector3d coord;
        
        public SOIData(Entity e, Position position, SphereOfInfluence soi) {
            this.entity = e;
            this.radius = soi.radius;
            this.coord = position.getVector3d();
        }
        
        public boolean contains(Vector3d coord) {
            return coord.distance(this.coord) < radius;
        }
    }
}
