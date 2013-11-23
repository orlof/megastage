/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Vector3f;
import org.megastage.systems.ClientNetworkSystem;

/**
 *
 * @author Teppo
 */
public class SpectatorModeInputManager implements AnalogListener {
    private final SimpleApplication app;

    public SpectatorModeInputManager(SimpleApplication app) {
        this.app = app;
    }

    public void init() {
        InputManager in = app.getInputManager();
        
        in.addMapping("FORWARD", new KeyTrigger(KeyInput.KEY_W));
        in.addMapping("BACK", new KeyTrigger(KeyInput.KEY_S));
        in.addMapping("CCW", new KeyTrigger(KeyInput.KEY_A));
        in.addMapping("CW", new KeyTrigger(KeyInput.KEY_D));

        in.addMapping("UP", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        in.addMapping("DOWN", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        in.addMapping("LEFT", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        in.addMapping("RIGHT", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        
        in.addListener(this, new String[] { 
            "FORWARD", "BACK", "CCW", "CW", "UP", "DOWN", "LEFT", "RIGHT"
        });
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        ArtemisState state = app.getStateManager().getState(ArtemisState.class);
        ClientNetworkSystem system = state.world.getSystem(ClientNetworkSystem.class);
        system.sendAnalogInput(name, value, tpf);
    }
}
