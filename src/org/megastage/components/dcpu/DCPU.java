package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.util.Globals;
import org.megastage.components.BaseComponent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Experimental 1.7 update to Notch's 1.4 emulator
 *
 * @author Notch, Herobrine
 */
public class DCPU extends BaseComponent {
    public static final int KHZ = 100;
    public static final int HARDWARE_TICK_INTERVAL = 1000 * KHZ / 60;

    public Entity ship;

    final public char[] ram = new char[65536];
    final public char[] registers = new char[8];

    public char pc;
    public char sp;
    public char ex;
    public char ia;

    public long startupTime;
    public long nextHardwareTick;
    public long cycles;

    public ArrayList<DCPUHardware> hardware = new ArrayList<DCPUHardware>();

    public boolean isSkipping = false;
    public boolean isOnFire = false;
    public boolean queueingEnabled = false; //TODO: Verify implementation
    public char[] interrupts = new char[256];
    public int ip;
    public int iwp;

    @Override
    public void init(World world, Entity parent, Element element) {
        this.ship = parent;
        try {
            load(new File("admiral.bin"));

            for(Element hwElement: element.getChildren("hardware")) {
                Class clazz = Class.forName("org.megastage.components." + hwElement.getAttributeValue("type"));
                DCPUHardware hw = (DCPUHardware) clazz.newInstance();
                hw.init(world, parent, hwElement);
                connectHardware(hw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startupTime = Globals.time + 2500;
        nextHardwareTick = Globals.time + HARDWARE_TICK_INTERVAL;
    }

    public void interrupt(char a) {
        interrupts[iwp = iwp + 1 & 0xFF] = a;
        if (iwp == ip) isOnFire = true;
    }

    public boolean connectHardware(DCPUHardware hw) {
        Log.debug("connect hardware " + hw.toString());
        hw.dcpu = this;
        hardware.add(hw);
        return true;
    }

    public boolean disconnectHardware(DCPUHardware hw) {
        return hardware.remove(hw);
    }

    public List<DCPUHardware> listHardware() {
        //TODO sync elsewhere
        return new ArrayList<DCPUHardware>(hardware);
    }

    public void run_ticks() {
        long uptime = Globals.time - startupTime;
        if(uptime < 0) return;
        
        long cycleTarget = uptime * KHZ;

        Log.trace((cycleTarget-cycles) + " cycles in batch");

        while (cycles < cycleTarget) {
            tick();

            if (cycles > nextHardwareTick) {
                tickHardware();
            }
        }

        Log.trace(String.format("%.2f Hz", 1000.0 * cycles / uptime));

    }

    public void tickHardware() {
        nextHardwareTick += HARDWARE_TICK_INTERVAL;

        synchronized (hardware) {
            for (DCPUHardware hw : hardware) {
                hw.tick60hz();
            }
        }
    }

    public void tick() {
        cycles++;
        if (isOnFire) {
            int pos = (int) (Math.random() * 0x10000) & 0xFFFF;
            char val = (char) ((int) (Math.random() * 0x10000) & 0xFFFF);
            int len = (int) (1 / (Math.random() + 0.001f)) - 0x50;
            for (int i = 0; i < len; i++) {
                ram[(pos + i) & 0xFFFF] = val;
            }
        }

        if (isSkipping) {
            char opcode = ram[pc];
            int cmd = opcode & 0x1F;
            pc = (char) (pc + getInstructionLength(opcode));
            if ((cmd >= 16) && (cmd <= 23))
                isSkipping = true;
            else {
                isSkipping = false;
            }
            return;
        }

        if (!queueingEnabled) {
            if (ip != iwp) {
                char a = interrupts[ip = ip + 1 & 0xFF];
                if (ia > 0) {
                    queueingEnabled = true;
                    ram[--sp & 0xFFFF] = pc;
                    ram[--sp & 0xFFFF] = registers[0];
                    registers[0] = a;
                    pc = ia;
                }
            }
        }

        char opcode = ram[pc++];

        int cmd = opcode & 0x1F;
        if (cmd == 0) {
            cmd = opcode >> 5 & 0x1F;
            if (cmd != 0) {
                int atype = opcode >> 10 & 0x3F;
                int aaddr = getAddrA(atype);
                char a = get(aaddr);

                switch (cmd) {
                    case 1: //JSR
                        cycles += 2;
                        ram[--sp & 0xFFFF] = pc;
                        pc = a;
                        break;
//        case 7: //HCF
//          cycles += 8;
//          isOnFire = true;
//          break;
                    case 8: //INT
                        cycles += 3;
                        interrupt(a);
                        break;
                    case 9: //IAG
                        set(aaddr, ia);
                        break;
                    case 10: //IAS
                        ia = a;
                        break;
                    case 11: //RFI
                        cycles += 2;
                        //disables interrupt queueing, pops A from the stack, then pops PC from the stack
                        queueingEnabled = false;
                        registers[0] = ram[sp++ & 0xFFFF];
                        pc = ram[sp++ & 0xFFFF];
                        break;
                    case 12: //IAQ
                        cycles++;
                        //if a is nonzero, interrupts will be added to the queue instead of triggered. if a is zero, interrupts will be triggered as normal again
                        if (a == 0) {
                            queueingEnabled = false;
                        } else {
                            queueingEnabled = true;
                        }
                        break;
                    case 16: //HWN
                        cycles++;
                        set(aaddr, (char) hardware.size());
                        break;
                    case 17: //HWQ
                        cycles += 3;
                        synchronized (hardware) {
                            if ((a >= 0) && (a < hardware.size())) {
                                hardware.get(a).query();
                            }
                        }
                        break;
                    case 18: //HWI
                        cycles += 3;
                        synchronized (hardware) {
                            if ((a >= 0) && (a < hardware.size())) {
                                hardware.get(a).interrupt();
                            }
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

            char a = getValA(atype);

            int btype = opcode >> 5 & 0x1F;
            int baddr = getAddrB(btype);
            char b = get(baddr);

            switch (cmd) {
                case 1: //SET
                    b = a;
                    break;
                case 2: { //ADD
                    cycles++;
                    int val = b + a;
                    b = (char) val;
                    ex = (char) (val >> 16);
                    break;
                }
                case 3: { //SUB
                    cycles++;
                    int val = b - a;
                    b = (char) val;
                    ex = (char) (val >> 16);
                    break;
                }
                case 4: { //MUL
                    cycles++;
                    int val = b * a;
                    b = (char) val;
                    ex = (char) (val >> 16);
                    break;
                }
                case 5: { //MLI
                    cycles++;
                    int val = (short) b * (short) a;
                    b = (char) val;
                    ex = (char) (val >> 16);
                    break;
                }
                case 6: { //DIV
                    cycles += 2;
                    if (a == 0) {
                        b = ex = 0;
                    } else {
                        b /= a;
                        ex = (char) ((b << 16) / a);
                    }
                    break;
                }
                case 7: { //DVI
                    cycles += 2;
                    if (a == 0) {
                        b = ex = 0;
                    } else {
                        b = (char) ((short) b / (short) a);
                        ex = (char) (((short) b << 16) / (short) a);
                    }
                    break;
                }
                case 8: //MOD
                    cycles += 2;
                    if (a == 0)
                        b = 0;
                    else {
                        b = (char) (b % a);
                    }
                    break;
                case 9: //MDI
                    cycles += 2;
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
                    ex = (char) (b << 16 >> a);
                    b = (char) (b >>> a);
                    break;
                case 14: //ASR
                    ex = (char) ((short) b << 16 >>> a);
                    b = (char) ((short) b >> a);
                    break;
                case 15: //SHL
                    ex = (char) (b << a >> 16);
                    b = (char) (b << a);
                    break;
                case 16: //IFB
                    cycles++;
                    if ((b & a) == 0) skip();
                    return;
                case 17: //IFC
                    cycles++;
                    if ((b & a) != 0) skip();
                    return;
                case 18: //IFE
                    cycles++;
                    if (b != a) skip();
                    return;
                case 19: //IFN
                    cycles++;
                    if (b == a) skip();
                    return;
                case 20: //IFG
                    cycles++;
                    if (b <= a) skip();
                    return;
                case 21: //IFA
                    cycles++;
                    if ((short) b <= (short) a) skip();
                    return;
                case 22: //IFL
                    cycles++;
                    if (b >= a) skip();
                    return;
                case 23: //IFU
                    cycles++;
                    if ((short) b >= (short) a) skip();
                    return;
                case 26: { //ADX
                    cycles++;
                    int val = b + a + ex;
                    b = (char) val;
                    ex = (char) (val >> 16);
                    break;
                }
                case 27: { //SBX
                    cycles++;
                    int val = b - a + ex;
                    b = (char) val;
                    ex = (char) (val >> 16);
                    break;
                }
                case 30: //STI
                    b = a;
                    set(baddr, b);
                    registers[6]++;
                    registers[7]++;
                    return;
                case 31: //STD
                    b = a;
                    set(baddr, b);
                    registers[6]--;
                    registers[7]--;
                    return;
                case 24:
                case 25:
            }
            set(baddr, b);
        }
    }

    public int getAddrB(int type) {
        switch (type & 0xF8) {
            case 0x00:
                return 0x10000 + (type & 0x7);
            case 0x08:
                return registers[type & 0x7];
            case 0x10:
                cycles++;
                return ram[pc++] + registers[type & 0x7] & 0xFFFF;
            case 0x18:
                switch (type & 0x7) {
                    case 0x0:
                        return (--sp) & 0xFFFF;
                    case 0x1:
                        return sp & 0xFFFF;
                    case 0x2:
                        cycles++;
                        return ram[pc++] + sp & 0xFFFF;
                    case 0x3:
                        return 0x10008;
                    case 0x4:
                        return 0x10009;
                    case 0x5:
                        return 0x10010;
                    case 0x6:
                        cycles++;
                        return ram[pc++];
                }
                cycles++;
                return 0x20000 | ram[pc++];
        }

        throw new IllegalStateException("Illegal a value type " + Integer.toHexString(type) + "! How did you manage that!?");
    }

    public int getAddrA(int type) {
        if (type >= 0x20) {
            return 0x20000 | (type & 0x1F) + 0xFFFF & 0xFFFF;
        }

        switch (type & 0xF8) {
            case 0x00:
                return 0x10000 + (type & 0x7);
            case 0x08:
                return registers[type & 0x7];
            case 0x10:
                cycles++;
                return ram[pc++] + registers[type & 0x7] & 0xFFFF;
            case 0x18:
                switch (type & 0x7) {
                    case 0x0:
                        return sp++ & 0xFFFF;
                    case 0x1:
                        return sp & 0xFFFF;
                    case 0x2:
                        cycles++;
                        return ram[pc++] + sp & 0xFFFF;
                    case 0x3:
                        return 0x10008;
                    case 0x4:
                        return 0x10009;
                    case 0x5:
                        return 0x10010;
                    case 0x6:
                        cycles++;
                        return ram[pc++];
                }
                cycles++;
                return 0x20000 | ram[pc++];
        }

        throw new IllegalStateException("Illegal a value type " + Integer.toHexString(type) + "! How did you manage that!?");
    }

    public char getValA(int type) {
        if (type >= 0x20) {
            return (char) ((type & 0x1F) + 0xFFFF);
        }

        switch (type & 0xF8) {
            case 0x00:
                return registers[type & 0x7];
            case 0x08:
                return ram[registers[type & 0x7]];
            case 0x10:
                cycles++;
                return ram[ram[pc++] + registers[type & 0x7] & 0xFFFF];
            case 0x18:
                switch (type & 0x7) {
                    case 0x0:
                        return ram[sp++ & 0xFFFF];
                    case 0x1:
                        return ram[sp & 0xFFFF];
                    case 0x2:
                        cycles++;
                        return ram[ram[pc++] + sp & 0xFFFF];
                    case 0x3:
                        return sp;
                    case 0x4:
                        return pc;
                    case 0x5:
                        return ex;
                    case 0x6:
                        cycles++;
                        return ram[ram[pc++]];
                }
                cycles++;
                return ram[pc++];
        }

        throw new IllegalStateException("Illegal a value type " + Integer.toHexString(type) + "! How did you manage that!?");
    }

    public char get(int addr) {
        if (addr < 0x10000)
            return ram[addr & 0xFFFF];
        if (addr < 0x10008)
            return registers[addr & 0x7];
        if (addr >= 0x20000)
            return (char) addr;
        if (addr == 0x10008)
            return sp;
        if (addr == 0x10009)
            return pc;
        if (addr == 0x10010)
            return ex;
        throw new IllegalStateException("Illegal address " + Integer.toHexString(addr) + "! How did you manage that!?");
    }

    public void set(int addr, char val) {
        if (addr < 0x10000)
            ram[addr & 0xFFFF] = val;
        else if (addr < 0x10008) {
            registers[addr & 0x7] = val;
        } else if (addr < 0x20000) {
            if (addr == 0x10008)
                sp = val;
            else if (addr == 0x10009)
                pc = val;
            else if (addr == 0x10010)
                ex = val;
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

    public void skip() {
        isSkipping = true;
    }

    public void load(File file) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        int i= 0;

        try {
            for (; i < ram.length; i++) {
                ram[i] = dis.readChar();
            }
        } catch (IOException e) {}

        Log.info("Boot image: " + i + " words");
        
        try { dis.close(); } catch (IOException e) {}
        
        for(; i < ram.length; i++) {
            ram[i] = 0;
        }
    }

    public void save(File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            for (int i = 0; i < ram.length; i++) {
                dos.writeChar(ram[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dos.close();
    }


}
