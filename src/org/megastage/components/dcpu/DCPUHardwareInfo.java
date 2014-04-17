package org.megastage.components.dcpu;

import org.megastage.components.BaseComponent;

public class DCPUHardwareInfo extends BaseComponent {
    public int type;
    public int revision;
    public int manufacturer;

    public DCPUHardwareInfo(int type, int revision, int manufacturer) {
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
}