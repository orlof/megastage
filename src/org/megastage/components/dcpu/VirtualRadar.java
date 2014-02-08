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

    private final char[] targetDataBuf = new char[7];
    private final char[] orbitalStateVectorBuf = new char[12];
    
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
            RadarData echo = findTargetEcho();
            if(echo != null && storeEchoDataToBuffer(echo)) {
                copyToMemory(targetDataBuf, b);
                dcpu.cycles += 7;
                dcpu.registers[2] = (char) 0xffff;
            } else {
                dcpu.registers[2] = (char) 0x0000;
            }
        } else if(a == 3) {
            // orbital state vector
            Vector3d ownCoord = ship.getComponent(Position.class).getVector3d();

            SOIData soi = getSOI(ownCoord);
            Log.info("SOI: " + ID.get(soi.entity));

            storeOrbitalStateVectorToBuffer(soi);
            copyToMemory(orbitalStateVectorBuf, b);

            dcpu.cycles += 12;
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
        Log.info("" + num);
        
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

    private RadarData findTargetEcho() {
        for(RadarData echo: ServerGlobals.radarEchoes) {
            if(echo.id == target) {
                return echo;
            }
        }
        return null;
    }

    private void storeTargetDataToMemory(RadarData echo, float dist, Vector3d myCoord) {
        // target type
        targetDataBuf[0] = 0x0002;
        Log.info(" type: " + 2);

        // target mass
        int mass = (int) echo.mass;
        targetDataBuf[1] = (char) ((mass >> 16) & 0xffff);
        targetDataBuf[2] = (char) (mass & 0xffff);
        Log.info(" mass: " + mass);

        // distance (float)
        writeFloatToArray(dist, targetDataBuf, 3);
        Log.info(" distance: " + dist);

        // direction
        Quaternion myRot = ship.getComponent(Rotation.class).getQuaternion();

        Entity other = ServerGlobals.world.getEntity(target);

        Vector3d otherCoord = other.getComponent(Position.class).getVector3d();
        Vector3d d = otherCoord.sub(myCoord).multiply(myRot.inverse());

        double pitch = Math.atan2(d.y, d.x*d.x + d.z*d.z);
        double yaw = -Math.atan2(d.z, d.x) - Globals.PI_HALF;

        targetDataBuf[5] = convertToDegMin(pitch);
        targetDataBuf[6] = convertToDegMin(yaw);

        Log.info(" pitch: " + Math.toDegrees(pitch) );
        Log.info(" yaw: " + Math.toDegrees(yaw));
    }

    public boolean storeEchoDataToBuffer(RadarData echo) {
        Vector3d myCoord = ship.getComponent(Position.class).getVector3d();
        float dist = (float) echo.coord.distance(myCoord);

        if(dist < RANGE) {
            storeTargetDataToMemory(echo, dist, myCoord);
            return true;
        }
        return false;
    }

    public void storeOrbitalStateVectorToBuffer(SOIData soi) {
        Entity targetEntity = ServerGlobals.world.getEntity(target);
        
        Vector3d targetCoord = targetEntity.getComponent(Position.class).getVector3d();
        Vector3d targetVeloc = targetEntity.getComponent(Velocity.class).vector;

        Vector3d soiCoord = soi.coord;
        Vector3d soiVeloc = soi.entity.getComponent(PrevPosition.class).getVelocity(soiCoord);
        
        Vector3d coord = soiCoord.sub(targetCoord);
        Vector3d veloc = soiVeloc.sub(targetVeloc);

        int x = (int) (coord.x / 100.0);
        orbitalStateVectorBuf[0] = (char) ((x >> 16) & 0xffff);
        orbitalStateVectorBuf[1] = (char) (x & 0xffff);

        int y = (int) (coord.y / 100.0);
        orbitalStateVectorBuf[2] = (char) ((y >> 16) & 0xffff);
        orbitalStateVectorBuf[3] = (char) (y & 0xffff);

        int z = (int) (coord.z / 100.0);
        orbitalStateVectorBuf[4] = (char) ((z >> 16) & 0xffff);
        orbitalStateVectorBuf[5] = (char) (z & 0xffff);

        int dx = (int) veloc.x;
        orbitalStateVectorBuf[6] = (char) ((dx >> 16) & 0xffff);
        orbitalStateVectorBuf[7] = (char) (dx & 0xffff);

        int dy = (int) veloc.y;
        orbitalStateVectorBuf[8] = (char) ((dy >> 16) & 0xffff);
        orbitalStateVectorBuf[9] = (char) (dy & 0xffff);

        int dz = (int) veloc.z;
        orbitalStateVectorBuf[10] = (char) ((dz >> 16) & 0xffff);
        orbitalStateVectorBuf[11] = (char) (dz & 0xffff);
    }

    public void storeOrbitalStateVectorToBuffer2(SOIData soi) {
        Entity targetEntity = ServerGlobals.world.getEntity(target);
        
        Vector3d targetCoord = targetEntity.getComponent(Position.class).getVector3d();
        Vector3d targetVeloc = targetEntity.getComponent(Velocity.class).vector;

        Vector3d soiCoord = soi.coord;
        Vector3d soiVeloc = soi.entity.getComponent(PrevPosition.class).getVelocity(soiCoord);
        
        Vector3d coord = soiCoord.sub(targetCoord);
        Vector3d veloc = soiVeloc.sub(targetVeloc);

        writeFloatToArray((float) coord.x, orbitalStateVectorBuf, 0);
        writeFloatToArray((float) coord.y, orbitalStateVectorBuf, 2);
        writeFloatToArray((float) coord.z, orbitalStateVectorBuf, 4);

        writeFloatToArray((float) veloc.x, orbitalStateVectorBuf, 6);
        writeFloatToArray((float) veloc.y, orbitalStateVectorBuf, 8);
        writeFloatToArray((float) veloc.z, orbitalStateVectorBuf, 10);

    }

    private void copyToMemory(char[] src, char b) {
        for(int i=0; i < targetDataBuf.length; i++) {
            dcpu.ram[b++ & 0xffff] = src[i];
        }
    }

    private void writeFloatToArray(float src, char[] dst, int index) {
        int bits = Float.floatToIntBits(src);

        dst[index++] = (char) ((bits >> 16) & 0xffff);
        dst[index] = (char) (bits & 0xffff);
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
