package org.megastage.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import org.megastage.components.client.VirtualMonitorView;
import org.megastage.util.Globals;
import org.megastage.util.Network;
import org.megastage.util.NetworkListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ClientKeyboardSystem extends VoidEntitySystem {
    public ClientKeyboardSystem() {
        super();

        // TODO keeping button down should never produce repeated key_typed or pressed events
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            boolean[] downChar = new boolean[256];
            boolean[] downCode = new boolean[256];
            boolean[] downChar2 = new boolean[256];
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                System.out.println("ClientKeyboardSystem.dispatchKeyEvent");
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if(e.getKeyChar() == 65535) {
                        if(!downCode[e.getKeyCode()]) {
                            downCode[e.getKeyCode()] = true;
                            System.out.println("PRESS e.getKeyCode() = " + (int) e.getKeyCode());
                            world.getSystem(ClientNetworkSystem.class).sendKeyPressed(e.getKeyCode());
                        }
                        return true;
                    }

                    if(!downChar[e.getKeyChar()]) {
                        downChar[e.getKeyChar()] = true;
                        System.out.println("PRESS e.getKeyChar() = " + (int) e.getKeyChar());
                        world.getSystem(ClientNetworkSystem.class).sendKeyPressed(e.getKeyChar());
                    }
                    return true;

                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    if(e.getKeyChar() == 65535) {
                        if(downCode[e.getKeyCode()]) {
                            downCode[e.getKeyCode()] = false;
                            System.out.println("REL e.getKeyCode() = " + (int) e.getKeyCode());
                            world.getSystem(ClientNetworkSystem.class).sendKeyReleased(e.getKeyCode());
                        }
                        return true;
                    }

                    if(downChar[e.getKeyChar()] || downChar2[e.getKeyChar()]) {
                        downChar[e.getKeyChar()] = false;
                        downChar2[e.getKeyChar()] = false;
                        System.out.println("REL e.getKeyChar() = " + (int) e.getKeyChar());
                        world.getSystem(ClientNetworkSystem.class).sendKeyReleased(e.getKeyChar());
                    }
                    return true;

                } else if (e.getID() == KeyEvent.KEY_TYPED) {
                    if(!downChar2[e.getKeyChar()]) {
                        downChar2[e.getKeyChar()] = true;
                        System.out.println("TYPE e.getKeyChar() = " + (int) e.getKeyChar());
                        world.getSystem(ClientNetworkSystem.class).sendKeyTyped(e.getKeyChar());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void processSystem() {
        // Intentionally left empty
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

}
