package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.srv.CollisionType;
import org.megastage.components.Explosion;
import org.megastage.components.srv.Identifier;
import org.megastage.ecs.CompType;

public class CollisionSystem extends Processor {
    public CollisionSystem(World world, long interval) {
        super(world, interval, CompType.CollisionType, CompType.Position);
    }

    @Override
    protected void process() {
        for(group.pairIterator(); group.nextPair(); /**/ ) {
            CollisionType cola = (CollisionType) world.getComponent(group.left, CompType.CollisionType);
            Position posa = (Position) world.getComponent(group.left, CompType.Position);
            
            CollisionType colb = (CollisionType) world.getComponent(group.right, CompType.CollisionType);
            Position posb = (Position) world.getComponent(group.right, CompType.Position);
            
            if(cola.isShip() || colb.isShip()) {
                double dx = (posa.x - posb.x) / 1000.0;
                double dy = (posa.y - posb.y) / 1000.0;
                double dz = (posa.z - posb.z) / 1000.0;

                double range = cola.radius + colb.radius;

                if(range * range > dx*dx + dy*dy + dz*dz) {
                    // we have an impact

                    Identifier ida = (Identifier) world.getComponent(group.left, CompType.Identifier);
                    Identifier idb = (Identifier) world.getComponent(group.right, CompType.Identifier);

                    if(cola.isShip() && !world.hasComponent(group.left, CompType.Explosion)) {
                        world.addComponent(group.left, CompType.Explosion, new Explosion());
                        // TODO damage a
                        Log.info(ida.toString() + " was damaged in collision with " + idb.toString());
                    }

                    if(colb.isShip() && !world.hasComponent(group.right, CompType.Explosion)) {
                        world.addComponent(group.right, CompType.Explosion, new Explosion());
                        // TODO damage b
                        Log.info(idb.toString() + " was damaged in collision with " + ida.toString());
                    }
                }
            }
        }
    }
}
