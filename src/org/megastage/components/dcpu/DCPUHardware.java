package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public abstract class DCPUHardware extends BaseComponent {
    public static final int TYPE_LEM = 0x7349F615;
    public static final int TYPE_KEYBOARD = 0x30CF7406;
    public static final int TYPE_CLOCK = 0x12D0B402;
    public static final int TYPE_ENGINE = 0x80000000;
    public static final int TYPE_PPS = 0x80000001;
    public static final int TYPE_GYRO = 0x80000002;
    public static final int MANUFACTORER_NYA_ELEKTRISKA = 0x1C6C8B36;
    public static final int MANUFACTORER_MOJANG = 0x4AB55488;
    public static final int MANUFACTORER_MACKAPAR = 0x1EB37E91;
    public static final int MANUFACTORER_GENERAL_ENGINES = 0x90000000;
    public static final int MANUFACTORER_TALON_NAVIGATION = 0x90000001;
    public static final int MANUFACTORER_PRECISION_RESEARCH = 0x90000002;
    public static final int MANUFACTORER_SEIKORION = 0x90000003;

    public int type;
    public int revision;
    public int manufactorer;
    public DCPU dcpu;
    public Entity ship;

    public void init(World world, Entity parent, Element element) throws DataConversionException {
        dcpu = parent.getComponent(DCPU.class);
        dcpu.connectHardware(this);

        ship = dcpu.ship;
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
}