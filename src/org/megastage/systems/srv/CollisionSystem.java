package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.gfx.ExplosionGeometry;
import org.megastage.components.srv.CollisionType;
import org.megastage.components.srv.Identifier;
import org.megastage.util.Time;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollisionSystem extends EntitySystem {
    private long interval;
    private long acc;

    @Mapper ComponentMapper<CollisionType> COLLISION_TYPE;
    @Mapper ComponentMapper<Position> POSITION;
    
    public CollisionSystem(long interval) {
        super(Aspect.getAspectForAll(CollisionType.class, Position.class));
        this.interval = interval;
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
    protected void processEntities(ImmutableBag<Entity> entities) {
        for(int i=0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            
            CollisionType cola = COLLISION_TYPE.get(a);
            Position posa = POSITION.get(a);
            
            for(int j=i+1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                CollisionType colb = COLLISION_TYPE.get(b);
                Position posb = POSITION.get(b);

                if(cola.isShip() || colb.isShip()) {
                    double dx = (posa.x - posb.x) / 1000.0;
                    double dy = (posa.y - posb.y) / 1000.0;
                    double dz = (posa.z - posb.z) / 1000.0;

                    double range = cola.radius + colb.radius;
                    
                    if(range * range > dx*dx + dy*dy + dz*dz) {
                        // we have an impact
                        
                        Identifier ida = a.getComponent(Identifier.class);
                        Identifier idb = b.getComponent(Identifier.class);

                        if(cola.isShip()) {
                            a.addComponent(new ExplosionGeometry());
                            a.changedInWorld();
                            // TODO damage a
                            Log.info(ida.toString() + " was damaged in collision with " + idb.toString());
                        }
                        
                        if(colb.isShip()) {
                            b.addComponent(new ExplosionGeometry());
                            b.changedInWorld();
                            // TODO damage b
                            Log.info(idb.toString() + " was damaged in collision with " + ida.toString());
                        }
                    }
                }
            }
        }
    }
}
