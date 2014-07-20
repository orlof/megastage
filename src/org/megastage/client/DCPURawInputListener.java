/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import org.megastage.util.Log;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import java.util.HashMap;

public class DCPURawInputListener implements RawInputListener {
    HashMap<Integer, Character> pressed = new HashMap<>();

    @Override
    public void beginInput() {}

    @Override
    public void endInput() {}

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {}

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {}

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {}

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {}

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        if(evt.isRepeating()) return;
        if(evt.isPressed()) keyPressed(evt);
        if(evt.isReleased()) keyReleased(evt);
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {}

/*
  0x10: Backspace
  0x11: Return
  0x12: Insert
  0x13: Delete
  0x20-0x7f: ASCII characters
  0x80: Arrow up
  0x81: Arrow down
  0x82: Arrow left
  0x83: Arrow right
  0x90: Shift
  0x91: Control
 */
    public boolean keyPressed(KeyInputEvent evt) {
        int keyCode = evt.getKeyCode();
        char keyChar = evt.getKeyChar();

        if (keyChar < 0x20 || keyChar > 0x7f) {
            switch(keyCode) {
                case 0x0e:
                    keyChar = 0x10;
                    break;
                case 0x1c:
                    keyChar = 0x11;
                    break;
                case 0xd3:
                    keyChar = 0x12;
                    break;
                case 0xc8:
                    keyChar = 0x80;
                    break;
                case 0xd0:
                    keyChar = 0x81;
                    break;
                case 0xcb:
                    keyChar = 0x82;
                    break;
                case 0xcd:
                    keyChar = 0x83;
                    break;
                case 0x36:
                    keyChar = 0x90;
                    break;
                case 0x2a:
                    keyChar = 0x90;
                    break;
                case 0x1d:
                    keyChar = 0x91;
                    break;
                case 0x9d:
                    keyChar = 0x91;
                    break;
                default:
                    Log.debug("Key event discarded");
                    return true;
            }
        }

        pressed.put(keyCode, keyChar);
        ClientGlobals.userCommand.keyPressed(keyChar);

        ClientGlobals.userCommand.keyTyped(keyChar);
        
        AudioNode an = SoundManager.get(SoundManager.KEYBOARD);
        an.setVolume(1);
        an.playInstance();
        
        return true;
    }

    public boolean keyReleased(KeyInputEvent evt) {
        int keyCode = evt.getKeyCode();
        Character keyChar = pressed.get(keyCode);

        if(keyChar != null) {
            ClientGlobals.userCommand.keyReleased(keyChar);
        }

        return true;
    }
}

