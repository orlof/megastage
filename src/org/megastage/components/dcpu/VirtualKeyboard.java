package org.megastage.components.dcpu;

import org.megastage.util.Log;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class VirtualKeyboard extends DCPUHardware {
    private char[] keyBuffer = new char[64];
    private int krp;
    private int kwp;
    private boolean[] isDown = new boolean[256];
    public char interruptMessage;
    public boolean doInterrupt;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_KEYBOARD, 0x1337, MANUFACTORER_MACKAPAR);

        return null;
    }

    public void keyTyped(int key) {
        if (key < 20 || key >= 127) return;
        if (keyBuffer[kwp & 0x3F] == 0) {
            Log.trace("write keyBuffer[%d]=%s", kwp, Integer.toHexString(key));
            keyBuffer[kwp++ & 0x3F] = (char) key;
            doInterrupt = true;
        }
    }

    public void keyPressed(int key) {
        int i = key; //keyMapping.getKey(key);
        if (i < 0) return;
        if ((i < 20 || i >= 127) && keyBuffer[kwp & 0x3F] == 0) {
            keyBuffer[kwp++ & 0x3F] = (char) i;
        }
        isDown[i] = true;
        doInterrupt = true;
    }

    public void keyReleased(int key) {
        int i = key; //keyMapping.getKey(key);
        if (i < 0) return;
        isDown[i] = false;
        doInterrupt = true;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        int a = dcpu.registers[0];
        if (a == 0) {
            for (int i = 0; i < keyBuffer.length; i++) {
                keyBuffer[i] = 0;
            }
            krp = 0;
            kwp = 0;
        } else if (a == 1) {
            dcpu.registers[2] = keyBuffer[(krp & 0x3F)];
            if (dcpu.registers[2] != 0) {
                keyBuffer[(krp++ & 0x3F)] = 0;
            }
        } else if (a == 2) {
            int key = dcpu.registers[1];
            if ((key >= 0) && (key < 256))
                dcpu.registers[2] = (char) (isDown[key] ? 1 : 0);
            else
                dcpu.registers[2] = 0;
        } else if (a == 3) {
            interruptMessage = dcpu.registers[1];
        }
    }

    @Override
    public void tick60hz(DCPU dcpu) {
        if (doInterrupt) {
            if (interruptMessage != 0) dcpu.interrupt(interruptMessage);
            doInterrupt = false;
        }
    }
}
