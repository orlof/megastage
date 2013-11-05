package org.megastage.client;

import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.LogFormat;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private final static String MAPPING_DCPU = "DCPU";
    
    public static void main(String[] args) {
        Log.setLogger(new LogFormat());
        Log.set(Log.LEVEL_DEBUG);

        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0,0,5));
        
        ArtemisState s = new ArtemisState();
        stateManager.attach(s);
        
        inputManager.addRawInputListener(new MyRawInputListener(s));
        
    }

    class MyRawInputListener implements RawInputListener {
        ArtemisState world = null;
        HashMap<Integer, Character> pressed = new HashMap<>();
        
        public MyRawInputListener(ArtemisState s) {
            this.world = s;
        }
        
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
                    default:
                        Log.debug("Key event discarded");
                        return true;
                }
            }
            
            pressed.put(keyCode, keyChar);
            Log.debug("KeyPressed(keyChar=" + Integer.toHexString(keyChar) + ")");
            world.world.getSystem(ClientNetworkSystem.class).sendKeyPressed(keyChar);
            Log.debug("KeyTyped(keyChar=" + Integer.toHexString(keyChar) + ")");
            world.world.getSystem(ClientNetworkSystem.class).sendKeyTyped(keyChar);
            return true;
        }

        public boolean keyReleased(KeyInputEvent evt) {
            int keyCode = evt.getKeyCode();
            Character keyChar = pressed.get(keyCode);
            
            if(keyChar != null) {
                Log.debug("KeyReleased(keyChar=" + Integer.toHexString(keyChar) + ")");
                world.world.getSystem(ClientNetworkSystem.class).sendKeyReleased(keyChar);
            }

            return true;
        }
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            System.out.println(name + " " + isPressed + " " + tpf);
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void initDCPUInputSystem() {
        inputManager.clearMappings();
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_5));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_6));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_7));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_8));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_9));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_0));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_BACK));

        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_RETURN));
        
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_MULTIPLY));
        
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_COLON));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_PERIOD));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_SUBTRACT));

        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addMapping(MAPPING_DCPU, new KeyTrigger(KeyInput.KEY_DELETE));

        inputManager.addListener(actionListener, new String[] {MAPPING_DCPU});
    }
}
