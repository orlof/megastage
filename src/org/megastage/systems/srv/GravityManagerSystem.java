package org.megastage.systems.srv;

import com.jme3.math.Vector3f;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.components.Velocity;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.server.GravityField;
import org.megastage.util.Bag;
import org.megastage.util.Globals;

public class GravityManagerSystem extends Processor {
    public static GravityManagerSystem INSTANCE;
    
    public GravityManagerSystem(World world, long interval) {
        super(world, interval, CompType.GravityFieldFlag, CompType.Position, CompType.Mass);
        INSTANCE = this;
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    public int findBySignature(char signature) {
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            if(match(eid, signature)) {
                return eid;
            }
        }
        return 0;
    }

    public boolean match(int eid, char signature) {
        return (char) eid == signature;
    }

    public Bag<GravityField> getGravityFields(int ship, boolean sorted) {
        Position pos = (Position) world.getComponent(ship, CompType.Position);
        Bag<GravityField> gfields = new Bag<>(200);
        
        for(int fieldEid = group.iterator(); fieldEid != 0; fieldEid = group.next()) {
            Position fieldPos = (Position) world.getComponent(fieldEid, CompType.Position);
            if(fieldPos == null) continue;
            
            float distanceSquared = fieldPos.get().distanceSquared(pos.get());
            
            double fieldStr = getStandardGravitationalParameter(fieldEid) / distanceSquared;
            gfields.add(new GravityField(fieldEid, fieldStr));
        }

        if(!sorted) {
            return gfields;
        }
        
        gfields.sort();
        return gfields;
    }
    
    public Vector3f getGravitationalAcceleration(int eid) {
        Position pos = (Position) world.getComponent(eid, CompType.Position);

        Vector3f acc = new Vector3f();

        for(int fieldEid = group.iterator(); fieldEid != 0; fieldEid = group.next()) {
            Position p2 = (Position) world.getComponent(fieldEid, CompType.Position);

            Vector3f displacement = p2.get().subtract(pos.get());
            float distanceSquared = displacement.lengthSquared();
            
            float gravitationalField = getStandardGravitationalParameter(fieldEid) / distanceSquared;
            float distance = (float) Math.sqrt(distanceSquared);
            float multiplier = gravitationalField / distance;

            displacement.multLocal(multiplier);
            acc.addLocal(displacement);
        }

        return acc;
    }    

    public float getStandardGravitationalParameter(int eid) {
        Mass mass = (Mass) world.getComponent(eid, CompType.Mass);
        return mass.value * Globals.G;
    }

    public void writeOrbitalStateVectorToMemory(char[] mem, char ptr, int reference, int ship, boolean ieee754) {
        Position shipPos = (Position) world.getComponent(ship, CompType.Position);
        Vector3f shipCoord = shipPos.get();
        Velocity shipVel = (Velocity) world.getComponent(ship, CompType.Velocity);
        Vector3f shipVelVec = shipVel.get();

        Position refPos = (Position) world.getComponent(reference, CompType.Position);
        Vector3f refCoord = refPos.get();
        Velocity refVel = (Velocity) world.getComponent(reference, CompType.Velocity);
        Vector3f refVelVec = refVel.get();
        
        Vector3f coord = shipCoord.subtract(refCoord);
        Vector3f veloc = shipVelVec.subtract(refVelVec);

        if(ieee754) {
            writeOrbitalStateVectorToMemoryFloat(mem, ptr, coord, veloc);
        } else {
            writeOrbitalStateVectorToMemoryInt(mem, ptr, coord, veloc);
        }
    }

    public static void writeOrbitalStateVectorToMemoryInt(char[] mem, char ptr, Vector3f coord, Vector3f veloc) {
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

    public static void writeOrbitalStateVectorToMemoryFloat(char[] mem, char ptr, Vector3f coord, Vector3f veloc) {
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

}
