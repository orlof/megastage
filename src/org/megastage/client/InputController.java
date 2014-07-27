package org.megastage.client;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.scene.Spatial;
import org.megastage.util.Bag;
import org.megastage.util.Log;

public class InputController {
    private static Bag<String> mappings = new Bag<>(100);
    private static DCPURawInputListener dcpuListener = new DCPURawInputListener();

    private static void map(String name, Trigger... trigger) {
        ClientGlobals.inputManager.addMapping(name, trigger);
        mappings.add(name);
    }

    public static void commit() {
        String[] activeMappings = mappings.toArray(String.class);
        ClientGlobals.inputManager.addListener(ClientGlobals.inputHandler, activeMappings);
    }
    
    public static void showCrosshair(boolean isVisible) {
        if(isVisible) {
            ClientGlobals.crosshair.setCullHint(Spatial.CullHint.Inherit);
        } else {
            ClientGlobals.crosshair.setCullHint(Spatial.CullHint.Always);
        }
    }
    
    public static void disableActions() {
        ClientGlobals.inputManager.clearMappings();
        ClientGlobals.inputManager.removeRawInputListener(dcpuListener);
        mappings.clear();
    }
    
    public static void enableMouseLook() {
        map("LOOK_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        map("LOOK_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        map("LOOK_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        map("LOOK_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
    }
    
    public static void enableWalk() {
        map("WalkForward", new KeyTrigger(KeyInput.KEY_W));
        map("WALK_Backward", new KeyTrigger(KeyInput.KEY_S));
        map("WALK_Left", new KeyTrigger(KeyInput.KEY_A));
        map("WALK_Right", new KeyTrigger(KeyInput.KEY_D));
    }
    
    public static void enablePickItem() {
        map("ITEM_PickLeft", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        map("ITEM_PickRight", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    }
    
    public static void enableTextCommandExit() {
        map("TEXT_Cancel", new KeyTrigger(KeyInput.KEY_ESCAPE));
        map("TEXT_Commit", new KeyTrigger(KeyInput.KEY_RETURN));
    }
    
    public static void enableTextCommand() {
        map("TEXT_Enter", new KeyTrigger(KeyInput.KEY_TAB));
    }
    
    public static void enableGameExit() {
        map("GAME_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
    }
    
    public static void enableMenuExit() {
        map("MENU_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
    }
    
    public static void enableDebug() {
        map("SHIP_MoveForward", new KeyTrigger(KeyInput.KEY_UP));
        map("SHIP_MoveBackward", new KeyTrigger(KeyInput.KEY_DOWN));
        map("SHIP_MoveUp", new KeyTrigger(KeyInput.KEY_PGUP));
        map("SHIP_MoveDown", new KeyTrigger(KeyInput.KEY_PGDN));
        map("SHIP_MoveLeft", new KeyTrigger(KeyInput.KEY_LEFT));
        map("SHIP_MoveRight", new KeyTrigger(KeyInput.KEY_RIGHT));

        map("SHIP_PitchUp", new KeyTrigger(KeyInput.KEY_I));
        map("SHIP_PitchDown", new KeyTrigger(KeyInput.KEY_K));
        map("SHIP_RollCW", new KeyTrigger(KeyInput.KEY_L));
        map("SHIP_RollCCW", new KeyTrigger(KeyInput.KEY_J));
        map("SHIP_YawLeft", new KeyTrigger(KeyInput.KEY_U));
        map("SHIP_YawRight", new KeyTrigger(KeyInput.KEY_O));
        
        map("GAME_TogglePointer", new KeyTrigger(KeyInput.KEY_F10));
    }
    
    public static void enableDCPU() {
        map("DCPU_Exit", new MouseButtonTrigger(MouseInput.BUTTON_LEFT), new KeyTrigger(KeyInput.KEY_ESCAPE));
        ClientGlobals.inputManager.addRawInputListener(dcpuListener);
    }
}
