package org.megastage.systems.srv;

import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.components.Velocity;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.server.GravityField;
import org.megastage.util.Bag;
import org.megastage.util.Globals;
import org.megastage.util.Vector3d;

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
            
            double dx = (fieldPos.x - pos.x) / 1000.0;
            double dy = (fieldPos.y - pos.y) / 1000.0;
            double dz = (fieldPos.z - pos.z) / 1000.0;
            
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double fieldStr = getStandardGravitationalParameter(fieldEid) / distanceSquared;
            gfields.add(new GravityField(fieldEid, fieldStr));
        }

        if(!sorted) {
            return gfields;
        }
        
        gfields.sort();
        return gfields;
    }
    
    public Vector3d getGravitationalAcceleration(int eid) {
        Position pos = (Position) world.getComponent(eid, CompType.Position);

        Vector3d acc = new Vector3d();

        for(int fieldEid = group.iterator(); fieldEid != 0; fieldEid = group.next()) {
            Position p2 = (Position) world.getComponent(fieldEid, CompType.Position);
            
            double dx = (p2.x - pos.x) / 1000.0;
            double dy = (p2.y - pos.y) / 1000.0;
            double dz = (p2.z - pos.z) / 1000.0;
            
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double gravitationalField = getStandardGravitationalParameter(fieldEid) / distanceSquared;
            double distance = Math.sqrt(distanceSquared);
            double multiplier = gravitationalField / distance;

            acc = acc.add(multiplier * dx, multiplier * dy, multiplier * dz);
        }

        return acc;
    }    

    public double getStandardGravitationalParameter(int eid) {
        Mass mass = (Mass) world.getComponent(eid, CompType.Mass);
        return mass.mass * Globals.G;
    }

    public void writeOrbitalStateVectorToMemory(char[] mem, char ptr, int reference, int ship, boolean ieee754) {
        Position shipPos = (Position) world.getComponent(ship, CompType.Position);
        Vector3d shipCoord = shipPos.getVector3d();
        Velocity shipVel = (Velocity) world.getComponent(ship, CompType.Velocity);
        Vector3d shipVelVec = shipVel.vector;

        Position refPos = (Position) world.getComponent(reference, CompType.Position);
        Vector3d refCoord = refPos.getVector3d();
        Velocity refVel = (Velocity) world.getComponent(reference, CompType.Velocity);
        Vector3d refVelVec = refVel.vector;
        
        Vector3d coord = shipCoord.sub(refCoord);
        Vector3d veloc = shipVelVec.sub(refVelVec);

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

}
