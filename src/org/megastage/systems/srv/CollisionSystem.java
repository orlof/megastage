package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.srv.CollisionType;
import org.megastage.components.Explosion;
import org.megastage.components.srv.Identifier;
import org.megastage.util.Time;

public class CollisionSystem extends EntitySystem {
    private long interval;
    private long acc;

    ComponentMapper<CollisionType> COLLISION_TYPE;
    ComponentMapper<Position> POSITION;
    ComponentMapper<Explosion> EXPLOSION;
    
    public CollisionSystem(long interval) {
        super(Aspect.getAspectForAll(CollisionType.class, Position.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        
        COLLISION_TYPE = world.getMapper(CollisionType.class);
        POSITION = world.getMapper(Position.class);
        EXPLOSION = world.getMapper(Explosion.class);
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
        for(int i=0; i < entities.size; i++) {
            Entity a = entities.get(i);
            
            CollisionType cola = COLLISION_TYPE.get(a);
            Position posa = POSITION.get(a);
            
            for(int j=i+1; j < entities.size; j++) {
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

                        if(cola.isShip() && !EXPLOSION.has(a)) {
                            a.addComponent(new Explosion());
                            a.changedInWorld();
                            // TODO damage a
                            Log.info(ida.toString() + " was damaged in collision with " + idb.toString());
                        }
                        
                        if(colb.isShip() && !EXPLOSION.has(b)) {
                            b.addComponent(new Explosion());
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
