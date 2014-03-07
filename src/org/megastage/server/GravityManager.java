package org.megastage.server;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import static org.megastage.server.RadarManager.match;
import static org.megastage.server.RadarManager.targets;
import org.megastage.util.Globals;
import org.megastage.util.Mapper;
import org.megastage.util.EntityComponentSystemException;
import org.megastage.util.Vector3d;

public class GravityManager {
    public static Array<Entity> gravityFields = new Array<>(0);

    public static void update(Array<Entity> entities) {
        gravityFields = entities;
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
        return (char) entity.id == signature;
    }

    public static Array<GravityField> getGravityFields(Entity ship, boolean sorted) {
        Position shipPos = Mapper.POSITION.get(ship);
        Array<GravityField> gfields = new Array<>(200);
        
        for(Entity gfield: gravityFields) {
            Position gfieldPos = Mapper.POSITION.get(gfield);
            if(gfieldPos == null) continue;
            
            double dx = (gfieldPos.x - shipPos.x) / 1000.0;
            double dy = (gfieldPos.y - shipPos.y) / 1000.0;
            double dz = (gfieldPos.z - shipPos.z) / 1000.0;
            
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double gfieldStr = getStandardGravitationalParameter(gfield) / distanceSquared;
            gfields.add(new GravityField(gfield, gfieldStr));
        }

        if(!sorted) {
            return gfields;
        }
        
        gfields.sort();
        return gfields;
    }
    
    public static Vector3d getGravitationalAcceleration(Entity entity) {
        Position pos = Mapper.POSITION.get(entity);

        Vector3d acc = new Vector3d();

        for(Entity e: gravityFields) {
            Position p2 = Mapper.POSITION.get(e);
            
            double dx = (p2.x - pos.x) / 1000.0;
            double dy = (p2.y - pos.y) / 1000.0;
            double dz = (p2.z - pos.z) / 1000.0;
            
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double gravitationalField = getStandardGravitationalParameter(e) / distanceSquared;
            double distance = Math.sqrt(distanceSquared);
            double multiplier = gravitationalField / distance;

            acc = acc.add(multiplier * dx, multiplier * dy, multiplier * dz);
        }

        return acc;
    }    

    public static double getStandardGravitationalParameter(Entity entity) {
        return Mapper.MASS.get(entity).mass * Globals.G;
    }

    public static void writeOrbitalStateVectorToMemory(char[] mem, char ptr, Entity reference, Entity ship, boolean ieee754) {
        Vector3d shipCoord = Mapper.POSITION.get(ship).getVector3d();
        Vector3d shipVeloc = Mapper.VELOCITY.get(ship).vector;

        Vector3d refCoord = Mapper.POSITION.get(reference).getVector3d();
        Vector3d refVeloc = Mapper.VELOCITY.get(ship).vector;
        
        Vector3d coord = shipCoord.sub(refCoord);
        Vector3d veloc = shipVeloc.sub(refVeloc);

        if(ieee754) {
            writeOrbitalStateVectorToMemoryFloat(mem, ptr, coord, veloc);
        } else {
            writeOrbitalStateVectorToMemoryInt(mem, ptr, coord, veloc);
        }
    }

    public static void writeOrbitalStateVectorToMemoryInt(char[] mem, char ptr, Vector3d coord, Vector3d veloc) {
        int x = (int) (coord.x / 100.0);
        mem[ptr++] = (char) (x >> 16);
        mem[ptr++] = (char) x;
        //Log.info(""+x);

        int y = (int) (coord.y / 100.0);
        mem[ptr++] = (char) (y >> 16);
        mem[ptr++] = (char) y;
        //Log.info(""+y);

        int z = (int) (coord.z / 100.0);
        mem[ptr++] = (char) (z >> 16);
        mem[ptr++] = (char) z;
        //Log.info(""+z);

        int dx = (int) veloc.x;
        mem[ptr++] = (char) (dx >> 16);
        mem[ptr++] = (char) dx;
        //Log.info(""+dx);

        int dy = (int) veloc.y;
        mem[ptr++] = (char) (dy >> 16);
        mem[ptr++] = (char) dy;
        //Log.info(""+dy);

        int dz = (int) veloc.z;
        mem[ptr++] = (char) (dz >> 16);
        mem[ptr++] = (char) dz;
        //Log.info(""+dz);
    }

    public static void writeOrbitalStateVectorToMemoryFloat(char[] mem, char ptr, Vector3d coord, Vector3d veloc) {
        ptr = writeFloatToMemory(mem, ptr, (float) coord.x);
        ptr = writeFloatToMemory(mem, ptr, (float) coord.y);
        ptr = writeFloatToMemory(mem, ptr, (float) coord.z);

        ptr = writeFloatToMemory(mem, ptr, (float) veloc.x);
        ptr = writeFloatToMemory(mem, ptr, (float) veloc.y);
        ptr = writeFloatToMemory(mem, ptr, (float) veloc.z);
    }

    private static char writeFloatToMemory(char[] mem, char ptr, float val) {
        int bits = Float.floatToIntBits(val);

        mem[ptr++] = (char) (bits >> 16);
        mem[ptr++] = (char) bits;
        
        return ptr;
    }

    public static class GravityField implements Comparable {
        public final Entity entity;
        public final double field;

        private GravityField(Entity entity, double gravitationalField) {
            this.entity = entity;
            this.field = gravitationalField;
        }

        @Override
        public int compareTo(Object o) {
            double other = ((GravityField) o).field;
            return field == other ? 0: field < other ? -1: +1;
        }
    }
}
