package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.srv.CollisionType;
import org.megastage.components.Explosion;
import org.megastage.components.Mass;
import org.megastage.components.RadarEchoFlag;
import org.megastage.components.srv.Identifier;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class RadarEchoSystem extends EntitySystem {
    private long interval;
    private long acc;

    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<Mass> MASS;

    public RadarEchoSystem(long interval) {
        super(Aspect.getAspectForAll(Mass.class, Position.class, RadarEchoFlag.class));
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
        Bag<RadarEcho> next = new Bag<>(200);
        
        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            
            Position pos = POSITION.get(entity);
            Mass mass = MASS.get(entity);

            next.add(new RadarEcho(entity.getId(), pos, mass));
        }
        
        ServerGlobals.radarEchoes = next;
    }

    public static class RadarEcho {
        public final int id;
        
        public final Vector pos;
        public final double mass;
        
        public RadarEcho(int id, Position position, Mass mass) {
            this.id = id;
            this.pos = new Vector(position.x / 1000.0, position.y / 1000.0, position.z / 1000.0);
            this.mass = mass.mass;
        }
    }
}
