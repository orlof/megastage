package org.megastage.server;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.Position;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class RadarManager {
    public static Array<Entity> targets = new Array<>(0);

    public static void update(Array<Entity> entities) {
        targets = entities;
    }

    public static Array<RadarSignal> getRadarSignals(Entity entity) {
        Position pos = Mapper.POSITION.get(entity);
        if(pos == null) {
            return new Array<>(0);
        }

        Vector3d coord = pos.getVector3d();
        
        Array<RadarSignal> signals = new Array<>(100);
        for(Entity target: targets) {
            // System that stores targets checks for Position existence
            Vector3d tcoord = Mapper.POSITION.get(target).getVector3d();
            
            double distanceSquared = coord.distanceSquared(tcoord);
            signals.add(new RadarSignal(target, distanceSquared));
        }

        signals.sort();

        return signals;
    }
    
    public static Entity findBySignature(char signature) {
        for(Entity entity: targets) {
            if(match(entity, signature)) {
                return entity;
            }
        }
        return null;
    }

    public static boolean match(Entity entity, char signature) {
        return (char) (entity.id & 0xffff) == signature;
    }

    public static class RadarSignal implements Comparable {
        public Entity entity;
        public double distanceSquared;

        public RadarSignal(Entity entity, double distanceSquared) {
            this.entity = entity;
            this.distanceSquared = distanceSquared;
        }

        @Override
        public int compareTo(Object o) {
            RadarSignal other = (RadarSignal) o;

            if(distanceSquared < other.distanceSquared) return -1;
            else if(distanceSquared > other.distanceSquared) return 1;
            return 0;
        }
        
        @Override
        public String toString() {
            return ID.get(entity) + distanceSquared;
        }
    }
}
