package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public abstract class DCPUHardware extends BaseComponent {
    public static final int TYPE_LEM = 0x7349F615;
    public static final int TYPE_RADAR = 0x3442980F;
    public static final int TYPE_FLOPPY = 0x4fd524c5;
    public static final int TYPE_KEYBOARD = 0x30CF7406;
    public static final int TYPE_CLOCK = 0x12D0B402;
    public static final int TYPE_ENGINE = 0xa8fb6730;
    public static final int TYPE_PPS = 0x3c7742c2;
//    public static final int TYPE_PPS = 0x0cb7cb4c;
    public static final int TYPE_GYRO = 0xeec6c4de;
    public static final int TYPE_GRAVITATION_SENSOR = 0x3846bc64;
    public static final int TYPE_THERMAL_LASER = 0xEEFA0000;
    public static final int TYPE_FORCE_FIELD = 0xF1E7D666;
    public static final int TYPE_POWER_PLANT = 0x1574886a;
    public static final int TYPE_POWER_CONTROLLER = 0xaff14367;
    public static final int TYPE_BATTERY = 0x83fc39b2;
    
    public static final int MANUFACTORER_NYA_ELEKTRISKA = 0x1C6C8B36;
    public static final int MANUFACTORER_MOJANG = 0x4AB55488;
    public static final int MANUFACTORER_MACKAPAR = 0x1EB37E91;
    public static final int MANUFACTORER_GENERAL_DRIVES = 0xe1e0bd31;
    public static final int MANUFACTORER_TALON_NAVIGATION = 0x982d3e46;
    public static final int MANUFACTORER_PRECISION_RESEARCH = 0x352ad8bf;
    public static final int MANUFACTORER_SEIKORION = 0xcf115b97;
    public static final int MANUFACTORER_OTEC = 0xb8badde8;
    public static final int MANUFACTORER_ENDER_INNOVATIONS = 0xE142A1FA;
    public static final int MANUFACTORER_CRADLE_TECH = 0xa3783fc8;
    public static final int MANUFACTORER_SORATOM = 0x80a9ddea;
    public static final int MANUFACTORER_URI_OASIS = 0x3867ab5f;

    public int type;
    public int revision;
    public int manufactorer;
    public DCPU dcpu;
    public Entity ship;
    public char priority = 0;

    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        dcpu = Mapper.DCPU.get(parent);
        dcpu.connectHardware(this);

        ship = dcpu.ship;
        
        return null;
    }

    public void query() {
        this.dcpu.registers[0] = (char) (this.type & 0xFFFF);
        this.dcpu.registers[1] = (char) (this.type >> 16 & 0xFFFF);
        this.dcpu.registers[2] = (char) (this.revision & 0xFFFF);
        this.dcpu.registers[3] = (char) (this.manufactorer & 0xFFFF);
        this.dcpu.registers[4] = (char) (this.manufactorer >> 16 & 0xFFFF);
    }

    public void interrupt() {
    }

    public void tick60hz() {
    }

    public <T extends DCPUHardware> T getHardware(Class<T> type) {
        for(DCPUHardware hw: dcpu.hardware) {
            if(type.isAssignableFrom(hw.getClass())) {
                return type.cast(hw);
            }
        }
        
        return null;
    }

    protected static final char writeFloatToMemory(char[] mem, char ptr, float val) {
        int bits = Float.floatToIntBits(val);

        mem[ptr++] = (char) (bits >> 16);
        mem[ptr++] = (char) bits;
        
        return ptr;
    }

    public static final char writeRadiansToMemory(char[] mem, char ptr, double rad) {
        // sign bit
        char result = rad < 0 ? (char) 0x8000: 0x0000;

        double degrees = Math.abs(Math.toDegrees(rad));
        result |= (Math.round(degrees) % 360) << 6;
        result |= Math.round(60.0 * degrees) % 60;
        
        mem[ptr++] = result;
        
        return ptr;
    }

    public static final char writePitchAndYawToMemory(char[] mem, char ptr, Entity ship, Entity target) {
        // target direction
        Quaternion rot = Mapper.ROTATION.get(ship).getQuaternion4d();

        // vector from me to target in global coordinate system
        Vector3d ownCoord = Mapper.POSITION.get(ship).getVector3d();
        Vector3d othCoord = Mapper.POSITION.get(target).getVector3d();
        
        Vector3d delta = othCoord.sub(ownCoord);

        delta = delta.multiply(rot);

        double pitch = Math.atan2(delta.y, Math.sqrt(delta.x*delta.x + delta.z*delta.z));
        double yaw = Math.atan2(delta.x, -delta.z);

        //Log.info(""+Math.toDegrees(pitch));
        //Log.info(""+Math.toDegrees(yaw));
        
        ptr = writeRadiansToMemory(mem, ptr, pitch);
        ptr = writeRadiansToMemory(mem, ptr, yaw);
        
        return ptr;
    }

}