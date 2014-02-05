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
import org.megastage.components.Rotation;
import org.megastage.components.srv.Velocity;
import org.megastage.systems.srv.RadarEchoSystem.RadarData;
import org.megastage.systems.srv.SphereOfInfluenceSystem.SOIData;
import org.megastage.util.Globals;
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

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        Log.info("a=" + Integer.toHexString(a) + ", b=" + Integer.toHexString(b));

        if (a == 0) {
            // STORE_SIGNATURES
            Vector3d coord = ship.getComponent(Position.class).getVector3d();
            
            int num = ServerGlobals.radarEchoes.size();
            LocalRadarEcho[] echoes = new LocalRadarEcho[num];
            
            for(int i = 0; i < num; i++) {
                RadarData echo = ServerGlobals.radarEchoes.get(i);
                echoes[i] = new LocalRadarEcho(echo, coord);
            }

            Arrays.sort(echoes);
            
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

            dcpu.cycles += 16;
        } else if(a == 1) {
            // SET_TRACKING_TARGET
            int num = ServerGlobals.radarEchoes.size();
            for(int i=0; i < num; i++) {
                RadarData echo = ServerGlobals.radarEchoes.get(i);
                if(echo.match(b)) {
                    target = echo.id;
                    dcpu.registers[2] = (char) 0xffff;
                    Log.info("Tracking target " + target);
                    return;
                }
            }

            target = 0;
            dcpu.registers[2] = (char) 0x0000;
            return;
        } else if(a == 2) {
            // STORE_TARGET_DATA
            int num = ServerGlobals.radarEchoes.size();
            for(int i=0; i < num; i++) {
                RadarData echo = ServerGlobals.radarEchoes.get(i);
                
                if(echo.id == target) {
                    Log.info("Target found");
                    Vector3d myCoord = ship.getComponent(Position.class).getVector3d();
                    float dist = (float) echo.coord.distance(myCoord);
                    
                    if(dist > RANGE) {
                        break;
                    }
                    
                    // target type
                    dcpu.ram[b++ & 0xffff] = 0x0002;
                    Log.info(" type: " + 2);

                    // target mass
                    int mass = (int) echo.mass;
                    dcpu.ram[b++ & 0xffff] = (char) ((mass >> 16) & 0xffff);
                    dcpu.ram[b++ & 0xffff] = (char) (mass & 0xffff);
                    Log.info(" mass: " + mass);

                    // distance (float)
                    int bits = Float.floatToIntBits(dist);

                    dcpu.ram[b++ & 0xffff] = (char) ((bits >> 16) & 0xffff);
                    dcpu.ram[b++ & 0xffff] = (char) (bits & 0xffff);
                    Log.info(" distance: " + dist);

                    // direction
                    Quaternion myRot = ship.getComponent(Rotation.class).getQuaternion();
                    
                    Entity other = ServerGlobals.world.getEntity(target);
                    
                    Vector3d otherCoord = other.getComponent(Position.class).getVector3d();
                    Vector3d d = otherCoord.sub(myCoord).multiply(myRot.inverse());
                    
                    double pitch = -Math.atan2(d.y, d.x*d.x + d.z*d.z);
                    double yaw = Math.atan2(d.z, d.x) - Globals.PI_HALF;
                    
                    dcpu.ram[b++ & 0xffff] = convertToDegMin(pitch);
                    dcpu.ram[b++ & 0xffff] = convertToDegMin(yaw);

                    Log.info(" pitch: " + pitch);
                    Log.info(" yaw: " + yaw);
                    
                    dcpu.cycles += 7;
                    dcpu.registers[2] = (char) 0xffff;
                    return;
                }
            }

            target = 0;
            for(int i=0; i < 7; i++) {
                dcpu.ram[b++ & 0xffff] = 0;
            }
            dcpu.cycles += 7;
            dcpu.registers[2] = (char) 0x0000;
            return;
        } else if(a == 3) {
            // orbital state vector
            Vector3d ownCoord = ship.getComponent(Position.class).getVector3d();
            Vector3d ownVeloc = ship.getComponent(Velocity.class).vector;

            SOIData soi = getSOI(ownCoord);
            Log.info("SOI: " + soi.entity.toString());
            
            Vector3d soiCoord = soi.coord;
            Vector3d soiVeloc = soi.entity.getComponent(PrevPosition.class).getVelocity(soiCoord);
            
            Vector3d coord = soiCoord.sub(ownCoord);
            Vector3d veloc = soiVeloc.sub(ownVeloc);

            int x = (int) (coord.x / 100.0);
            dcpu.ram[b++ & 0xffff] = (char) ((x >> 16) & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (x & 0xffff);

            int y = (int) (coord.y / 100.0);
            dcpu.ram[b++ & 0xffff] = (char) ((y >> 16) & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (y & 0xffff);

            int z = (int) (coord.z / 100.0);
            dcpu.ram[b++ & 0xffff] = (char) ((z >> 16) & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (z & 0xffff);

            int dx = (int) veloc.x;
            dcpu.ram[b++ & 0xffff] = (char) ((dx >> 16) & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (dx & 0xffff);

            int dy = (int) veloc.y;
            dcpu.ram[b++ & 0xffff] = (char) ((dy >> 16) & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (dy & 0xffff);

            int dz = (int) veloc.z;
            dcpu.ram[b++ & 0xffff] = (char) ((dz >> 16) & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (dz & 0xffff);

            dcpu.cycles += 12;
            return;
        }
    }

    private SOIData getSOI(Vector3d coord) {
        for(int i=0; i < ServerGlobals.soi.size(); i++) {
            SOIData soi = ServerGlobals.soi.get(i);
            if(soi.contains(coord)) {
                return soi;
            }
        }
        return null;
    }

    private static class LocalRadarEcho implements Comparable {
        private final RadarData echo;
        private final double distanceSquared;

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
    }
    
    public static char convertToDegMin(double rad) {
        double deg = Math.toDegrees(rad);
        int d = ((int) deg) % 360;
        int m = ((int) (60.0 * deg)) % 60;
        return (char) ((d << 6) | m);
    }
    
}
