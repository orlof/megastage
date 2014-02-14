package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.Position;
import org.megastage.components.Mass;
import org.megastage.components.RadarEcho;
import org.megastage.components.srv.Velocity;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

/**
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 */
public class RadarEchoSystem extends EntitySystem {
    private long interval;
    private long acc;

    ComponentMapper<Position> POSITION;
    ComponentMapper<Velocity> VELOCITY;
    ComponentMapper<Mass> MASS;
    ComponentMapper<RadarEcho> RADAR_ECHO;

    public RadarEchoSystem(long interval) {
        super(Aspect.getAspectForAll(Mass.class, Position.class, RadarEcho.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        RADAR_ECHO = world.getMapper(RadarEcho.class);
        POSITION = world.getMapper(Position.class);
        VELOCITY = world.getMapper(Velocity.class);
        MASS = world.getMapper(Mass.class);
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
        Array<RadarData> next = new Array<>(200);
        
        for(Entity entity: entities) {
            Position pos = POSITION.get(entity);
            Velocity vel = VELOCITY.get(entity);
            Mass mass = MASS.get(entity);
            RadarEcho echo = RADAR_ECHO.get(entity);

            next.add(new RadarData(entity.id, pos, vel, mass, echo));
        }
        
        ServerGlobals.radarEchoes = next;
    }

    public static class RadarData {
        public final int id;

        public final int echo;
        public final Vector3d coord;
        public final Vector3d veloc;
        public final double mass;
        
        public RadarData(int id, Position pos, Velocity vel, Mass mass, RadarEcho echo) {
            this.id = id;
            this.echo = echo.type;
            this.coord = new Vector3d(pos.x / 1000.0, pos.y / 1000.0, pos.z / 1000.0);
            this.veloc = new Vector3d(vel.vector);
            this.mass = mass.mass;
        }

        public boolean match(char b) {
            return (char) (id & 0xffff) == b;
        }
        
        public String toString() {
            return "RadarData(id="+id+", echo="+echo+", coord="+coord.toString()+", mass="+mass;
        }
    }
}
