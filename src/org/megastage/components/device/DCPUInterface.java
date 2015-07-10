package org.megastage.components.device;

import org.megastage.components.dcpu.DCPU;
import org.megastage.components.generic.EntityReference;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public abstract class DCPUInterface extends BaseComponent {

    @Override
    public void initialize(int eid) {
        EntityReference conn = (EntityReference) World.INSTANCE.getComponent(eid, CompType.DCPUConnection);
        DCPU dcpu = (DCPU) World.INSTANCE.getComponent(conn.eid, CompType.DCPU);
        dcpu.addHardware(eid);
    }

    public static transient final int TYPE_LEM = 0x7349F615;
    public static transient final int TYPE_RADAR = 0x3442980F;
    public static transient final int TYPE_FLOPPY = 0x4fd524c5;
    public static transient final int TYPE_KEYBOARD = 0x30CF7406;
    public static transient final int TYPE_CLOCK = 0x12D0B402;
    public static transient final int TYPE_ENGINE = 0xa8fb6730;
    public static transient final int TYPE_PPS = 0x3c7742c2;
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

    public void interrupt(DCPU dcpu, int eid) {}
    public void tick60hz(DCPU dcpu, int eid) {}
}
