package org.megastage.client;

import com.esotericsoftware.minlog.Log;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;


/**
 *  Manages a SpectatorCamera.
 *
 *  @author    Original author: Paul Speed
 *             Modified by: Orlof
 */
public class WalkAppState extends AbstractAppState {

    private Application app;
    private WalkCommandHandler walkCommandHandler = new WalkCommandHandler();

    public WalkAppState() {
    }    

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;

        this.walkCommandHandler.registerWithInput(app.getInputManager());
    }
            
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        walkCommandHandler.setEnabled(enabled);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        walkCommandHandler.unregisterInput();        
    }
}
