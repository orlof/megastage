package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import java.util.Arrays;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Position;
import org.megastage.systems.srv.RadarEchoSystem.RadarEcho;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;
import org.megastage.util.Vector;

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

        Log.debug("a=" + Integer.toHexString(a) + ", b=" + Integer.toHexString(b));

        if (a == 0) {
            Position pos = ship.getComponent(Position.class);

            int num = ServerGlobals.radarEchoes.size();
            LocalRadarEcho[] echoes = new LocalRadarEcho[num];
            
            for(int i = 0; i < num; i++) {
                RadarEcho echo = ServerGlobals.radarEchoes.get(i);
                echoes[i] = new LocalRadarEcho(echo, pos);
            }

            Arrays.sort(echoes);
            
            int mem = dcpu.registers[1];
            for(int i = 0; i < 16; i++) {
                if(i >= echoes.length || echoes[i].distanceSquared > RANGE_SQUARED) {
                    dcpu.ram[mem++ & 0xffff] = 0;
                } else {
                    dcpu.ram[mem++ & 0xffff] = (char) (echoes[i].echo.id & 0xffff);
                }
            }

            dcpu.cycles += 16;
        } else if(a == 1) {
            int num = ServerGlobals.radarEchoes.size();
            for(int i=0; i < num; i++) {
                RadarEcho echo = ServerGlobals.radarEchoes.get(i);
                if(echo.id == b) {
                    this.target = echo.id;
                    dcpu.registers[1] = (char) 0xffff;
                    return;
                }
            }

            this.target = 0;
            dcpu.registers[1] = (char) 0x0000;
            return;
        } else if(a == 2) {
            int num = ServerGlobals.radarEchoes.size();
            for(int i=0; i < num; i++) {
                RadarEcho echo = ServerGlobals.radarEchoes.get(i);
                
                if(echo.id == this.target) {
                    dcpu.ram[b++ & 0xffff] = 0;
                    
                    int mass = (int) echo.mass;
                    dcpu.ram[b++ & 0xffff] = (char) ((mass >> 16) & 0xffff);
                    dcpu.ram[b++ & 0xffff] = (char) (mass & 0xffff);

                    Position pos = ship.getComponent(Position.class);
                    double d2 = echo.pos.distanceSquared(pos);
                    float d = (float) Math.sqrt(d2);
                    int bits = Float.floatToIntBits(d);

                    dcpu.ram[b++ & 0xffff] = (char) ((bits >> 16) & 0xffff);
                    dcpu.ram[b++ & 0xffff] = (char) (bits & 0xffff);

                    dcpu.ram[b++ & 0xffff] = 0;
                    dcpu.ram[b++ & 0xffff] = 0;
                    
                    dcpu.cycles += 7;
                    return;
                }
            }

            for(int i=0; i < 7; i++) {
                dcpu.ram[b++ & 0xffff] = 0;
            }
            dcpu.cycles += 7;
            return;
        }
    }

    private static class LocalRadarEcho implements Comparable {
        private final RadarEcho echo;
        private final double distanceSquared;

        public LocalRadarEcho(RadarEcho echo, Position pos) {
            this.echo = echo;
            this.distanceSquared = echo.pos.distanceSquared(pos);
        }

        @Override
        public int compareTo(Object o) {
            LocalRadarEcho other = (LocalRadarEcho) o;

            if(distanceSquared < other.distanceSquared) return -1;
            else if(distanceSquared > other.distanceSquared) return 1;
            return 0;
        }
    }
}
