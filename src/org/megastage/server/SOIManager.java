package org.megastage.server;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.srv.SphereOfInfluence;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class SOIManager {
    private static Array<SOIData> soiData = new Array<>(0);

    public static void update(Array<Entity> entities) {
        Array<SOIData> tmpData = new Array<>(entities.size);
        
        for(Entity entity: entities) {
            tmpData.add(new SOIData(entity));
        }
        
        tmpData.sort();
        
        soiData = tmpData;
        
    }
    
    public static SOIData getSOI(Vector3d coord) {
        for(SOIData data: soiData) {
            if(data.contains(coord)) {
                return data;
            }
        }
        return null;
    }

    public static class SOIData implements Comparable<SOIData> {
        public final Entity entity;

        public final double radius;
        public final int priority;
        public final Vector3d coord;
        
        public SOIData(Entity entity) {
            this.entity = entity;
            this.coord = Mapper.POSITION.get(entity).getVector3d();

            SphereOfInfluence soi = Mapper.SPHERE_OF_INFLUENCE.get(entity);
            this.radius = soi.radius;
            this.priority = soi.priority;
        }
        
        public boolean contains(Vector3d coord) {
            return priority == -1 || coord.distance(this.coord) < radius;
        }
        
        @Override
        public String toString() {
            return "SOIData(entity="+ID.get(entity)+", coord=" + coord.toString() + ", radius=" + radius +", priority=" + priority +")";
        }

        @Override
        public int compareTo(SOIData o) {
            return o.priority - priority;
        }
    }
    
}
