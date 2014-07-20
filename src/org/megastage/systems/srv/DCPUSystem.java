package org.megastage.systems.srv;

import org.megastage.util.Log;
import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.dcpu.*;
import org.megastage.ecs.CompType;
import org.megastage.util.ID;

public class DCPUSystem extends Processor {
    public DCPUSystem(World world, long interval) {
        super(world, interval, CompType.DCPU);
    }

    @Override
    protected void process(int eid) {
        DCPU dcpu = (DCPU) world.getComponent(eid, CompType.DCPU);

        long uptime = world.time - dcpu.startupTime;
        if(uptime < 0) return;
        
        long cycleTarget = uptime * dcpu.hz / 1000;
        
        Log.trace("Gametime: " + world.time);
        Log.trace("Uptime: " + uptime);
        Log.trace("Cycles: " + dcpu.cycles + " / " + cycleTarget);

        while (dcpu.cycles < cycleTarget) {
            tick(dcpu);

            if (dcpu.cycles > dcpu.nextHardwareTick) {
                tickHardware(dcpu);
            }
        }
    }

    public void tickHardware(DCPU dcpu) {
        dcpu.nextHardwareTick += dcpu.hardwareTickInterval;

        for(int i=0; i < dcpu.hardwareSize; i++) {
            tick60hz(dcpu, dcpu.hardware[i]);
        }
    }

    public void tick(DCPU dcpu) {
        dcpu.cycles++;
        if (dcpu.isOnFire) {
            int pos = (int) (Math.random() * 0x10000) & 0xFFFF;
            char val = (char) ((int) (Math.random() * 0x10000) & 0xFFFF);
            int len = (int) (1 / (Math.random() + 0.001f)) - 0x50;
            for (int i = 0; i < len; i++) {
                dcpu.ram[(pos + i) & 0xFFFF] = val;
            }
        }

        if (dcpu.isSkipping) {
            char opcode = dcpu.ram[dcpu.pc];
            int cmd = opcode & 0x1F;
            dcpu.pc = (char) (dcpu.pc + getInstructionLength(opcode));
            if ((cmd >= 16) && (cmd <= 23))
                dcpu.isSkipping = true;
            else {
                dcpu.isSkipping = false;
            }
            return;
        }

        if (!dcpu.queueingEnabled) {
            if (dcpu.ip != dcpu.iwp) {
                char a = dcpu.interrupts[dcpu.ip = dcpu.ip + 1 & 0xFF];
                if (dcpu.ia > 0) {
                    dcpu.queueingEnabled = true;
                    dcpu.ram[--dcpu.sp & 0xFFFF] = dcpu.pc;
                    dcpu.ram[--dcpu.sp & 0xFFFF] = dcpu.registers[0];
                    dcpu.registers[0] = a;
                    dcpu.pc = dcpu.ia;
                }
            }
        }

        char opcode = dcpu.ram[dcpu.pc++];

        int cmd = opcode & 0x1F;
        if (cmd == 0) {
            cmd = opcode >> 5 & 0x1F;
            if (cmd != 0) {
                int atype = opcode >> 10 & 0x3F;
                int aaddr = getAddrA(dcpu, atype);
                char a = get(dcpu, aaddr);

                switch (cmd) {
                    case 1: //JSR
                        dcpu.cycles += 2;
                        dcpu.ram[--dcpu.sp & 0xFFFF] = dcpu.pc;
                        dcpu.pc = a;
                        break;
//        case 7: //HCF
//          dcpu.cycles += 8;
//          dcpu.isOnFire = true;
//          break;
                    case 8: //INT
                        dcpu.cycles += 3;
                        dcpu.interrupt(a);
                        break;
                    case 9: //IAG
                        set(dcpu, aaddr, dcpu.ia);
                        break;
                    case 10: //IAS
                        dcpu.ia = a;
                        break;
                    case 11: //RFI
                        dcpu.cycles += 2;
                        //disables interrupt queueing, pops A from the stack, then pops PC from the stack
                        dcpu.queueingEnabled = false;
                        dcpu.registers[0] = dcpu.ram[dcpu.sp++ & 0xFFFF];
                        dcpu.pc = dcpu.ram[dcpu.sp++ & 0xFFFF];
                        break;
                    case 12: //IAQ
                        dcpu.cycles++;
                        //if a is nonzero, interrupts will be added to the queue instead of triggered. if a is zero, interrupts will be triggered as normal again
                        if (a == 0) {
                            dcpu.queueingEnabled = false;
                        } else {
                            dcpu.queueingEnabled = true;
                        }
                        break;
                    case 16: //HWN
                        //Log.info("HWN " + hardware.size());
                        dcpu.cycles++;
                        set(dcpu, aaddr, (char) dcpu.hardwareSize);
                        break;
                    case 17: //HWQ
                        dcpu.cycles += 3;
                        if ((a >= 0) && (a < dcpu.hardwareSize)) {
                            //Log.info("HWQ " + ((int) a) + " " + hardware.get(a).toString());
                            query(dcpu, dcpu.hardware[a]);
                        } else {
                            //Log.info("HWQ " + (int) a);
                        }
                        break;
                    case 18: //HWI
                        dcpu.cycles += 3;
                        if ((a >= 0) && (a < dcpu.hardwareSize)) {
                            interrupt(dcpu, dcpu.hardware[a]);
                        } else {
                            //Log.info("HWI " + (int) a);
                        }
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 13:
                    case 14:
                    case 15:
                    default:
                        break;
                }
            }
        } else {
            int atype = opcode >> 10 & 0x3F;

            char a = getValA(dcpu, atype);

            int btype = opcode >> 5 & 0x1F;
            int baddr = getAddrB(dcpu, btype);
            char b = get(dcpu, baddr);

            switch (cmd) {
                case 1: //SET
                    b = a;
                    break;
                case 2: { //ADD
                    dcpu.cycles++;
                    int val = b + a;
                    b = (char) val;
                    dcpu.ex = (char) (val >> 16);
                    break;
                }
                case 3: { //SUB
                    dcpu.cycles++;
                    int val = b - a;
                    b = (char) val;
                    dcpu.ex = (char) (val >> 16);
                    break;
                }
                case 4: { //MUL
                    dcpu.cycles++;
                    int val = b * a;
                    b = (char) val;
                    dcpu.ex = (char) (val >> 16);
                    break;
                }
                case 5: { //MLI
                    dcpu.cycles++;
                    int val = (short) b * (short) a;
                    b = (char) val;
                    dcpu.ex = (char) (val >> 16);
                    break;
                }
                case 6: { //DIV
                    dcpu.cycles += 2;
                    if (a == 0) {
                        b = dcpu.ex = 0;
                    } else {
                        b /= a;
                        dcpu.ex = (char) ((b << 16) / a);
                    }
                    break;
                }
                case 7: { //DVI
                    dcpu.cycles += 2;
                    if (a == 0) {
                        b = dcpu.ex = 0;
                    } else {
                        b = (char) ((short) b / (short) a);
                        dcpu.ex = (char) (((short) b << 16) / (short) a);
                    }
                    break;
                }
                case 8: //MOD
                    dcpu.cycles += 2;
                    if (a == 0)
                        b = 0;
                    else {
                        b = (char) (b % a);
                    }
                    break;
                case 9: //MDI
                    dcpu.cycles += 2;
                    if (a == 0)
                        b = 0;
                    else {
                        b = (char) ((short) b % (short) a);
                    }
                    break;
                case 10: //AND
                    b = (char) (b & a);
                    break;
                case 11: //BOR
                    b = (char) (b | a);
                    break;
                case 12: //XOR
                    b = (char) (b ^ a);
                    break;
                case 13: //SHR
                    dcpu.ex = (char) (b << 16 >> a);
                    b = (char) (b >>> a);
                    break;
                case 14: //ASR
                    dcpu.ex = (char) ((short) b << 16 >>> a);
                    b = (char) ((short) b >> a);
                    break;
                case 15: //SHL
                    dcpu.ex = (char) (b << a >> 16);
                    b = (char) (b << a);
                    break;
                case 16: //IFB
                    dcpu.cycles++;
                    if ((b & a) == 0) dcpu.skip();
                    return;
                case 17: //IFC
                    dcpu.cycles++;
                    if ((b & a) != 0) dcpu.skip();
                    return;
                case 18: //IFE
                    dcpu.cycles++;
                    if (b != a) dcpu.skip();
                    return;
                case 19: //IFN
                    dcpu.cycles++;
                    if (b == a) dcpu.skip();
                    return;
                case 20: //IFG
                    dcpu.cycles++;
                    if (b <= a) dcpu.skip();
                    return;
                case 21: //IFA
                    dcpu.cycles++;
                    if ((short) b <= (short) a) dcpu.skip();
                    return;
                case 22: //IFL
                    dcpu.cycles++;
                    if (b >= a) dcpu.skip();
                    return;
                case 23: //IFU
                    dcpu.cycles++;
                    if ((short) b >= (short) a) dcpu.skip();
                    return;
                case 26: { //ADX
                    dcpu.cycles++;
                    int val = b + a + dcpu.ex;
                    b = (char) val;
                    dcpu.ex = (char) (val >> 16);
                    break;
                }
                case 27: { //SBX
                    dcpu.cycles++;
                    int val = b - a + dcpu.ex;
                    b = (char) val;
                    dcpu.ex = (char) (val >> 16);
                    break;
                }
                case 30: //STI
                    b = a;
                    set(dcpu, baddr, b);
                    dcpu.registers[6]++;
                    dcpu.registers[7]++;
                    return;
                case 31: //STD
                    b = a;
                    set(dcpu, baddr, b);
                    dcpu.registers[6]--;
                    dcpu.registers[7]--;
                    return;
                case 24:
                case 25:
            }
            set(dcpu, baddr, b);
        }
    }

    public int getAddrB(DCPU dcpu, int type) {
        switch (type & 0xF8) {
            case 0x00:
                return 0x10000 + (type & 0x7);
            case 0x08:
                return dcpu.registers[type & 0x7];
            case 0x10:
                dcpu.cycles++;
                return dcpu.ram[dcpu.pc++] + dcpu.registers[type & 0x7] & 0xFFFF;
            case 0x18:
                switch (type & 0x7) {
                    case 0x0:
                        return (--dcpu.sp) & 0xFFFF;
                    case 0x1:
                        return dcpu.sp & 0xFFFF;
                    case 0x2:
                        dcpu.cycles++;
                        return dcpu.ram[dcpu.pc++] + dcpu.sp & 0xFFFF;
                    case 0x3:
                        return 0x10008;
                    case 0x4:
                        return 0x10009;
                    case 0x5:
                        return 0x10010;
                    case 0x6:
                        dcpu.cycles++;
                        return dcpu.ram[dcpu.pc++];
                }
                dcpu.cycles++;
                return 0x20000 | dcpu.ram[dcpu.pc++];
        }

        throw new IllegalStateException("Illegal a value type " + Integer.toHexString(type) + "! How did you manage that!?");
    }

    public int getAddrA(DCPU dcpu, int type) {
        if (type >= 0x20) {
            return 0x20000 | (type & 0x1F) + 0xFFFF & 0xFFFF;
        }

        switch (type & 0xF8) {
            case 0x00:
                return 0x10000 + (type & 0x7);
            case 0x08:
                return dcpu.registers[type & 0x7];
            case 0x10:
                dcpu.cycles++;
                return dcpu.ram[dcpu.pc++] + dcpu.registers[type & 0x7] & 0xFFFF;
            case 0x18:
                switch (type & 0x7) {
                    case 0x0:
                        return dcpu.sp++ & 0xFFFF;
                    case 0x1:
                        return dcpu.sp & 0xFFFF;
                    case 0x2:
                        dcpu.cycles++;
                        return dcpu.ram[dcpu.pc++] + dcpu.sp & 0xFFFF;
                    case 0x3:
                        return 0x10008;
                    case 0x4:
                        return 0x10009;
                    case 0x5:
                        return 0x10010;
                    case 0x6:
                        dcpu.cycles++;
                        return dcpu.ram[dcpu.pc++];
                }
                dcpu.cycles++;
                return 0x20000 | dcpu.ram[dcpu.pc++];
        }

        throw new IllegalStateException("Illegal a value type " + Integer.toHexString(type) + "! How did you manage that!?");
    }

    public char getValA(DCPU dcpu, int type) {
        if (type >= 0x20) {
            return (char) ((type & 0x1F) + 0xFFFF);
        }

        switch (type & 0xF8) {
            case 0x00:
                return dcpu.registers[type & 0x7];
            case 0x08:
                return dcpu.ram[dcpu.registers[type & 0x7]];
            case 0x10:
                dcpu.cycles++;
                return dcpu.ram[dcpu.ram[dcpu.pc++] + dcpu.registers[type & 0x7] & 0xFFFF];
            case 0x18:
                switch (type & 0x7) {
                    case 0x0:
                        return dcpu.ram[dcpu.sp++ & 0xFFFF];
                    case 0x1:
                        return dcpu.ram[dcpu.sp & 0xFFFF];
                    case 0x2:
                        dcpu.cycles++;
                        return dcpu.ram[dcpu.ram[dcpu.pc++] + dcpu.sp & 0xFFFF];
                    case 0x3:
                        return dcpu.sp;
                    case 0x4:
                        return dcpu.pc;
                    case 0x5:
                        return dcpu.ex;
                    case 0x6:
                        dcpu.cycles++;
                        return dcpu.ram[dcpu.ram[dcpu.pc++]];
                }
                dcpu.cycles++;
                return dcpu.ram[dcpu.pc++];
        }

        throw new IllegalStateException("Illegal a value type " + Integer.toHexString(type) + "! How did you manage that!?");
    }

    public char get(DCPU dcpu, int addr) {
        if (addr < 0x10000)
            return dcpu.ram[addr & 0xFFFF];
        if (addr < 0x10008)
            return dcpu.registers[addr & 0x7];
        if (addr >= 0x20000)
            return (char) addr;
        if (addr == 0x10008)
            return dcpu.sp;
        if (addr == 0x10009)
            return dcpu.pc;
        if (addr == 0x10010)
            return dcpu.ex;
        throw new IllegalStateException("Illegal address " + Integer.toHexString(addr) + "! How did you manage that!?");
    }

    public void set(DCPU dcpu, int addr, char val) {
        if (addr < 0x10000)
            dcpu.ram[addr & 0xFFFF] = val;
        else if (addr < 0x10008) {
            dcpu.registers[addr & 0x7] = val;
        } else if (addr < 0x20000) {
            if (addr == 0x10008)
                dcpu.sp = val;
            else if (addr == 0x10009)
                dcpu.pc = val;
            else if (addr == 0x10010)
                dcpu.ex = val;
            else
                throw new IllegalStateException("Illegal address " + Integer.toHexString(addr) + "! How did you manage that!?");
        }
    }

    public static int getInstructionLength(char opcode) {
        int len = 1;
        int cmd = opcode & 0x1F;
        if (cmd == 0) {
            cmd = opcode >> 5 & 0x1F;
            if (cmd > 0) {
                int atype = opcode >> 10 & 0x3F;
                if (((atype & 0xF8) == 16) || (atype == 31) || (atype == 30)) len++;
            }
        } else {
            int atype = opcode >> 5 & 0x1F;
            int btype = opcode >> 10 & 0x3F;
            if (((atype & 0xF8) == 16) || (atype == 31) || (atype == 30)) len++;
            if (((btype & 0xF8) == 16) || (btype == 31) || (btype == 30)) len++;
        }
        return len;
    }

    private void interrupt(DCPU dcpu, int eid) {
        DCPUHardware hw = (DCPUHardware) world.getComponent(eid, CompType.DCPUHardware);
        hw.interrupt(dcpu);
    }

    private void tick60hz(DCPU dcpu, int eid) {
        Log.trace(ID.get(eid));
        DCPUHardware hw = (DCPUHardware) world.getComponent(eid, CompType.DCPUHardware);
        hw.tick60hz(dcpu);
    }

    private void query(DCPU dcpu, int eid) {
        DCPUHardware hw = (DCPUHardware) world.getComponent(eid, CompType.DCPUHardware);
        hw.query(dcpu);
    }
}