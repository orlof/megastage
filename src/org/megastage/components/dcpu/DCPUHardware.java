package org.megastage.components.dcpu;

import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.Log;

public abstract class DCPUHardware extends ReplicatedComponent implements Comparable<DCPUHardware> {
    public static transient final int TYPE_LEM = 0x7349F615;
    public static transient final int TYPE_RADAR = 0x3442980F;
    public static transient final int TYPE_FLOPPY = 0x4fd524c5;
    public static transient final int TYPE_KEYBOARD = 0x30CF7406;
    public static transient final int TYPE_CLOCK = 0x12D0B402;
    public static transient final int TYPE_ENGINE = 0xa8fb6730;
    public static transient final int TYPE_PPS = 0x3c7742c2;
//    public static final int TYPE_PPS = 0x0cb7cb4c;
    public static transient final int TYPE_GYRO = 0xeec6c4de;
    public static transient final int TYPE_GRAVITATION_SENSOR = 0x3846bc64;
    public static transient final int TYPE_THERMAL_LASER = 0xEEFA0000;
    public static transient final int TYPE_FORCE_FIELD = 0xF1E7D666;
    public static transient final int TYPE_POWER_PLANT = 0x1574886a;
    public static transient final int TYPE_POWER_CONTROLLER = 0xaff14367;
    public static transient final int TYPE_BATTERY = 0x83fc39b2;
    
    public static transient final int MANUFACTORER_NYA_ELEKTRISKA = 0x1C6C8B36;
    public static transient final int MANUFACTORER_MOJANG = 0x4AB55488;
    public static transient final int MANUFACTORER_MACKAPAR = 0x1EB37E91;
    public static transient final int MANUFACTORER_GENERAL_DRIVES = 0xe1e0bd31;
    public static transient final int MANUFACTORER_TALON_NAVIGATION = 0x982d3e46;
    public static transient final int MANUFACTORER_PRECISION_RESEARCH = 0x352ad8bf;
    public static transient final int MANUFACTORER_SEIKORION = 0xcf115b97;
    public static transient final int MANUFACTORER_OTEC = 0xb8badde8;
    public static transient final int MANUFACTORER_ENDER_INNOVATIONS = 0xE142A1FA;
    public static transient final int MANUFACTORER_CRADLE_TECH = 0xa3783fc8;
    public static transient final int MANUFACTORER_SORATOM = 0x80a9ddea;
    public static transient final int MANUFACTORER_URI_OASIS = 0x3867ab5f;

    public int type;
    public int revision;
    public int manufacturer;

    public int dcpuEID;
    public int shipEID;

    public char priority = 0;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        dcpuEID = parentEid;
        return null;
    }
    
    protected final void setInfo(int type, int revision, int manufacturer) {
        this.type = type;
        this.revision = revision;
        this.manufacturer = manufacturer;
    }

    public void query(DCPU dcpu) {
        dcpu.registers[0] = (char) (this.type & 0xFFFF);
        dcpu.registers[1] = (char) (this.type >> 16 & 0xFFFF);
        dcpu.registers[2] = (char) (this.revision & 0xFFFF);
        dcpu.registers[3] = (char) (this.manufacturer & 0xFFFF);
        dcpu.registers[4] = (char) (this.manufacturer >> 16 & 0xFFFF);
    }

    @Override
    public void initialize(int eid) {
        DCPU dcpu = (DCPU) World.INSTANCE.getComponent(dcpuEID, CompType.DCPU);
        dcpu.addHardware(eid);
        shipEID = dcpu.shipEID;
    }

    public void interrupt(DCPU dcpu) {}

    public void tick60hz(DCPU dcpu) {}

    protected static char writeFloatToMemory(char[] mem, char ptr, float val) {
        int bits = Float.floatToIntBits(val);

        mem[ptr++] = (char) (bits >> 16);
        mem[ptr++] = (char) bits;
        
        return ptr;
    }

    public static char writeRadiansToMemory(char[] mem, char ptr, double rad) {
        // sign bit
        char result = rad < 0 ? (char) 0x8000: 0x0000;

        double degrees = Math.abs(Math.toDegrees(rad));
        result |= (Math.round(degrees) % 360) << 6;
        result |= Math.round(60.0 * degrees) % 60;
        
        mem[ptr++] = result;
        
        return ptr;
    }

    public static char writePitchAndYawToMemory(char[] mem, char ptr, int shipEid, int targetEid) {
        // target direction
        Rotation shipRot = (Rotation) World.INSTANCE.getComponent(shipEid, CompType.Rotation);        

        // vector from me to target in global coordinate system
        Position shipPos = (Position) World.INSTANCE.getComponent(shipEid, CompType.Position);

        Position targetPos = (Position) World.INSTANCE.getComponent(targetEid, CompType.Position);
        
        Vector3f delta = targetPos.get().subtract(shipPos.get());

        shipRot.rotateLocal(delta);

        double pitch = Math.atan2(delta.y, Math.sqrt(delta.x*delta.x + delta.z*delta.z));
        double yaw = Math.atan2(delta.x, -delta.z);

        //Log.info(""+Math.toDegrees(pitch));
        //Log.info(""+Math.toDegrees(yaw));
        
        ptr = writeRadiansToMemory(mem, ptr, pitch);
        ptr = writeRadiansToMemory(mem, ptr, yaw);
        
        return ptr;
    }

    @Override
    public Message synchronize(int eid) {
        return null;
    }

    @Override
    public int compareTo(DCPUHardware o) {
        if(priority > o.priority) return 1;
        if(priority < o.priority) return -1;
        return 0;
    }
}
