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

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    world.getSystem(ClientNetworkSystem.class).sendKeyPressed(e.getKeyChar());
                    //kbd.keyPressed(e.getKeyCode());
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    world.getSystem(ClientNetworkSystem.class).sendKeyReleased(e.getKeyChar());
                    //kbd.keyReleased(e.getKeyCode());
                } else if (e.getID() == KeyEvent.KEY_TYPED) {
                    world.getSystem(ClientNetworkSystem.class).sendKeyTyped(e.getKeyChar());
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
