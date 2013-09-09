package org.megastage.systems;

import com.artemis.systems.VoidEntitySystem;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ClientKeyboardSystem extends VoidEntitySystem {
    public ClientKeyboardSystem() {
        super();

        // only one pressed and typed event is generated without release (no key repeat)
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            boolean[] keyCharPressed = new boolean[256];
            boolean[] keyCharTyped = new boolean[256];
            boolean[] keyCodePressed = new boolean[256];

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                System.out.println("ClientKeyboardSystem.dispatchKeyEvent");
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                        int keyCode = e.getKeyCode();
                        if(keyCode < 256 && !keyCodePressed[keyCode]) {
                            System.out.println("PRESSED Code = " + e.getKeyCode());
                            keyCodePressed[keyCode] = true;
                            world.getSystem(ClientNetworkSystem.class).sendKeyPressed(keyCode);
                        }
                        return true;
                    }

                    char keyChar = e.getKeyChar();
                    if(keyChar < 256 && !keyCharPressed[keyChar]) {
                        System.out.println("PRESSED Char = " + (int) e.getKeyChar());
                        keyCharPressed[keyChar] = true;
                        world.getSystem(ClientNetworkSystem.class).sendKeyPressed(e.getKeyChar());
                    }
                    return true;

                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    if(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                        int keyCode = e.getKeyCode();
                        if(keyCode < 256 && keyCodePressed[keyCode]) {
                            System.out.println("RELEASED Code = " + keyCode);
                            keyCodePressed[keyCode] = false;
                            world.getSystem(ClientNetworkSystem.class).sendKeyReleased(keyCode);
                        }
                        return true;
                    }

                    char keyChar = e.getKeyChar();
                    if(keyChar < 256 && keyCharPressed[keyChar]) {
                        System.out.println("RELEASED Char = " + (int) keyChar);
                        keyCharPressed[keyChar] = false;
                        keyCharTyped[keyChar] = false;
                        world.getSystem(ClientNetworkSystem.class).sendKeyReleased(keyChar);
                    }
                    return true;

                } else if (e.getID() == KeyEvent.KEY_TYPED) {
                    char keyChar = e.getKeyChar();
                    if(keyChar < 256 && !keyCharTyped[keyChar]) {
                        System.out.println("TYPED Char = " + (int) keyChar);
                        keyCharTyped[keyChar] = true;
                        world.getSystem(ClientNetworkSystem.class).sendKeyTyped(keyChar);
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
