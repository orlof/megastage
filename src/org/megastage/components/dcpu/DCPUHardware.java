package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public abstract class DCPUHardware extends BaseComponent {
    public static final int TYPE_LEM = 0x7349F615;
    public static final int TYPE_KEYBOARD = 0x30CF7406;
    public static final int TYPE_CLOCK = 0x12D0B402;
    public static final int MANUFACTORER_NYA_ELEKTRISKA = 0x1C6C8B36;
    public static final int MANUFACTORER_MOJANG = 0x4AB55488;
    public static final int MANUFACTORER_MACKAPAR = 0x1EB37E91;

    public int type;
    public int revision;
    public int manufactorer;
    public DCPU dcpu;

    public void init(World world, Entity parent, Element element) {
        dcpu = parent.getComponent(DCPU.class);
        dcpu.connectHardware(this);
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