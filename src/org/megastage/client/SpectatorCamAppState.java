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
public class SpectatorCamAppState extends AbstractAppState {

    private Application app;
    private SpectatorCamera spectatorCam;

    public SpectatorCamAppState() {
    }    

    public SpectatorCamera getCamera() {
        return spectatorCam;
    }

    public void setCamera(SpectatorCamera spectatorCam) {
        this.spectatorCam = spectatorCam;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;

        if (app.getInputManager() != null) {
        
            if (spectatorCam == null) {
                throw new RuntimeException("Camera is not initilized");
            }
            
            spectatorCam.registerWithInput(app.getInputManager());            
        }               
    }
            
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        spectatorCam.setEnabled(enabled);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        spectatorCam.unregisterInput();        
    }


}
