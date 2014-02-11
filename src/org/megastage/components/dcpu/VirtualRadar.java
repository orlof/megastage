package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import java.util.Arrays;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Position;
import org.megastage.components.PrevPosition;
import org.megastage.components.RadarEcho;
import org.megastage.components.Rotation;
import org.megastage.components.srv.Velocity;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.protocol.Network;
import org.megastage.systems.srv.RadarEchoSystem.RadarData;
import org.megastage.systems.srv.SphereOfInfluenceSystem.SOIData;
import org.megastage.util.Globals;
import org.megastage.util.ID;
import org.megastage.util.Quaternion;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector3d;

public class VirtualRadar extends DCPUHardware {
    private static final double RANGE = 10e8;
    private static final double RANGE_SQUARED = RANGE * RANGE;
    
    public int target = 0;
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_RADAR;
        revision = 0x90;
        manufactorer = MANUFACTORER_ENDER_INNOVATIONS;

        super.init(world, parent, element);
        
        return null;
    }

    @Override
    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        Log.info("a=" + Integer.toHexString(a) + ", b=" + Integer.toHexString(b));

        if (a == 0) {
            // GET LIST OF SIGNATURES
            LocalRadarEcho[] echoes = getSignatures();
            storeSignatures(echoes);
            dcpu.cycles += 16;

        } else if(a == 1) {
            // SET_TRACKING_TARGET
            RadarData echo = findRadarEcho(b);
            if(setTrackingTarget(echo)) {
                dcpu.registers[2] = (char) 0xffff;
            } else {
                dcpu.registers[2] = (char) 0x0000;
            }

        } else if(a == 2) {
            // STORE_TARGET_DATA
            RadarData echo = getRadarData(target);
            if(echo != null && storeTargetDataToArray(echo, dcpu.ram, b)) {
                dcpu.cycles += 7;
                dcpu.registers[2] = (char) 0xffff;
            } else {
                dcpu.registers[2] = (char) 0x0000;
            }
        } else if(a == 3) {
            // GET_ORBITAL_STATE_VECTOR
            RadarData echo = getRadarData(target);
            if(echo != null && storeTargetDataToArray(echo, dcpu.ram, b)) {
                Vector3d ownCoord = ship.getComponent(Position.class).getVector3d();

                SOIData soi = getSOI(ownCoord);
                Log.info("SOI: " + ID.get(soi.entity));

                storeOrbitalStateVectorToArray(soi, dcpu.ram, b);
                dcpu.cycles += 12;
            }
        }
    }

    private SOIData getSOI(Vector3d coord) {
        for(SOIData soi: ServerGlobals.soi) {
            if(soi.contains(coord)) {
                return soi;
            }
        }
        return null;
    }

    public LocalRadarEcho[] getSignatures() {
        Position pos = ship.getComponent(Position.class);
        if(pos == null) {
            return new LocalRadarEcho[0];
        }
        
        Vector3d coord = pos.getVector3d();
    
        int num = ServerGlobals.radarEchoes.size;
        
        LocalRadarEcho[] echoes = new LocalRadarEcho[num];
        for(int i = 0; i < num; i++) {
            RadarData echo = ServerGlobals.radarEchoes.get(i);
            echoes[i] = new LocalRadarEcho(echo, coord);
        }
        
        Arrays.sort(echoes);
        return echoes;
    }

    public void storeSignatures(LocalRadarEcho[] echoes) {
        int mem = dcpu.registers[1];
        for(int i = 0; i < 16; i++) {
            if(i >= echoes.length || echoes[i].distanceSquared > RANGE_SQUARED) {
                dcpu.ram[mem++ & 0xffff] = 0;
                Log.info(i + ": " + 0);
            } else {
                dcpu.ram[mem++ & 0xffff] = (char) (echoes[i].echo.id & 0xffff);
                Log.info(i + ": " + echoes[i].echo.id);
            }
        }
    }

    private RadarData findRadarEcho(char b) {
        for(RadarData echo: ServerGlobals.radarEchoes) {
            if(echo.match(b)) {
                return echo;
            }
        }
        return null;
    }

    public boolean setTrackingTarget(RadarData echo) {
        Log.info("Tracking target " + target);

        int id = echo == null ? 0: echo.id;
        if(target != id) {
            target = id;
            dirty = true;
        }

        return id != 0;
    }

    private RadarData getRadarData(int id) {
        for(RadarData echo: ServerGlobals.radarEchoes) {
            if(echo.id == id) {
                return echo;
            }
        }
        return null;
    }

    public boolean storeTargetDataToArray(RadarData echo, char[] mem, char ptr) {
        Vector3d myCoord = ship.getComponent(Position.class).getVector3d();
        float dist = (float) echo.coord.distance(myCoord);

        if(dist < RANGE) {
            Entity targetEntity = ServerGlobals.world.getEntity(target);
            if(targetEntity == null) return false;
            
            // target type
            mem[ptr++ & 0xffff] = 0x0002;

            // target mass
            int mass = (int) echo.mass;
            mem[ptr++ & 0xffff] = (char) ((mass >> 16) & 0xffff);
            mem[ptr++ & 0xffff] = (char) (mass & 0xffff);
            Log.info(" mass: " + mass);

            // distance (float)
            ptr = writeFloatToArray(dist, mem, ptr);
            Log.info(" distance: " + dist);

            // direction
            Quaternion myRot = ship.getComponent(Rotation.class).getQuaternion4d();

            // vector from me to target in global coordinate system
            Vector3d d = echo.coord.sub(myCoord);
            Log.info("Global: " + d.toString());

            d = d.multiply(myRot);
            Log.info("Local: " + d.toString());

            double pitch = -Math.atan2(d.y, d.x*d.x + d.z*d.z);
            double yaw = Math.atan2(d.z, d.x) - Globals.PI_HALF;

            mem[ptr++ & 0xffff] = convertToDegMin(pitch);
            mem[ptr++ & 0xffff] = convertToDegMin(yaw);

            Log.info(" pitch: " + Math.toDegrees(pitch) );
            Log.info(" yaw: " + Math.toDegrees(yaw));
            return true;
        }
        return false;
    }

    public boolean storeOrbitalStateVectorToArray(SOIData soi, char[] mem, char ptr) {
        Entity targetEntity = ServerGlobals.world.getEntity(target);
        if(targetEntity == null) return false;
        
        Vector3d targetCoord = targetEntity.getComponent(Position.class).getVector3d();
        Vector3d targetVeloc = targetEntity.getComponent(Velocity.class).vector;

        Vector3d soiCoord = soi.coord;
        Vector3d soiVeloc = soi.entity.getComponent(PrevPosition.class).getVelocity(soiCoord);
        
        Vector3d coord = soiCoord.sub(targetCoord);
        Vector3d veloc = soiVeloc.sub(targetVeloc);

        int x = (int) (coord.x / 100.0);
        mem[ptr++ & 0xffff] = (char) ((x >> 16) & 0xffff);
        mem[ptr++ & 0xffff] = (char) (x & 0xffff);

        int y = (int) (coord.y / 100.0);
        mem[ptr++ & 0xffff] = (char) ((y >> 16) & 0xffff);
        mem[ptr++ & 0xffff] = (char) (y & 0xffff);

        int z = (int) (coord.z / 100.0);
        mem[ptr++ & 0xffff] = (char) ((z >> 16) & 0xffff);
        mem[ptr++ & 0xffff] = (char) (z & 0xffff);

        int dx = (int) veloc.x;
        mem[ptr++ & 0xffff] = (char) ((dx >> 16) & 0xffff);
        mem[ptr++ & 0xffff] = (char) (dx & 0xffff);

        int dy = (int) veloc.y;
        mem[ptr++ & 0xffff] = (char) ((dy >> 16) & 0xffff);
        mem[ptr++ & 0xffff] = (char) (dy & 0xffff);

        int dz = (int) veloc.z;
        mem[ptr++ & 0xffff] = (char) ((dz >> 16) & 0xffff);
        mem[ptr++ & 0xffff] = (char) (dz & 0xffff);
        
        return true;
    }

    public boolean storeOrbitalStateVectorToBuffer2(SOIData soi, char[] mem, char ptr) {
        Entity targetEntity = ServerGlobals.world.getEntity(target);
        if(targetEntity == null) return false;
        
        Vector3d targetCoord = targetEntity.getComponent(Position.class).getVector3d();
        Vector3d targetVeloc = targetEntity.getComponent(Velocity.class).vector;

        Vector3d soiCoord = soi.coord;
        Vector3d soiVeloc = soi.entity.getComponent(PrevPosition.class).getVelocity(soiCoord);
        
        Vector3d coord = soiCoord.sub(targetCoord);
        Vector3d veloc = soiVeloc.sub(targetVeloc);

        ptr = writeFloatToArray((float) coord.x, mem, ptr);
        ptr = writeFloatToArray((float) coord.y, mem, ptr);
        ptr = writeFloatToArray((float) coord.z, mem, ptr);

        ptr = writeFloatToArray((float) veloc.x, mem, ptr);
        ptr = writeFloatToArray((float) veloc.y, mem, ptr);
        ptr = writeFloatToArray((float) veloc.z, mem, ptr);

        return true;
    }

    private char writeFloatToArray(float src, char[] dst, char ptr) {
        int bits = Float.floatToIntBits(src);

        dst[ptr++ & 0xffff] = (char) ((bits >> 16) & 0xffff);
        dst[ptr++ & 0xffff] = (char) (bits & 0xffff);
        
        return ptr;
    }

    public static class LocalRadarEcho implements Comparable {
        public final RadarData echo;
        public final double distanceSquared;

        public LocalRadarEcho(RadarData echo, Vector3d coord) {
            this.echo = echo;
            this.distanceSquared = echo.coord.distanceSquared(coord);
        }

        @Override
        public int compareTo(Object o) {
            LocalRadarEcho other = (LocalRadarEcho) o;

            if(distanceSquared < other.distanceSquared) return -1;
            else if(distanceSquared > other.distanceSquared) return 1;
            return 0;
        }
        
        public String toString() {
            return echo.toString() + " " + distanceSquared;
        }
    }
    
    public static char convertToDegMin(double rad) {
        double deg = Math.toDegrees(rad);
        if(deg < 0) deg += 360.0;

        int d = ((int) deg) % 360;
        int m = ((int) (60.0 * deg)) % 60;
        return (char) ((d << 6) | m);
    }


    private boolean dirty = false;

    @Override
    public Network.ComponentMessage create(Entity entity) {
        dirty = false;

        RadarTargetData data = new RadarTargetData();
        data.target = target;
        
        Log.info(ID.get(entity) + data.toString());

        return data.create(entity);
    }

    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public boolean synchronize() {
        return dirty;
    }
}
