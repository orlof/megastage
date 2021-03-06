package org.megastage.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BoxLayout;
import org.megastage.ecs.World;
import org.megastage.systems.client.ClientMonitorRenderSystem;
import org.megastage.systems.client.ClientNetworkSystem;
import org.megastage.systems.client.EntityDeleteSystem;

public class ECSState extends AbstractAppState {
    public World world;

    public ECSState() {
        setEnabled(false);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        Container menu = new Container(new BoxLayout(), "retro");

        for(int i=0; i < ClientGlobals.chatLabel.length; i++) {
            ClientGlobals.chatLabel[i] = menu.addChild(new Label("", "retro"));
            ClientGlobals.chatLabel[i].setFontSize(10.0f);
        }
        
        ClientGlobals.cmdTextField = menu.addChild(new TextField("", "retro"));
        ClientGlobals.cmdTextField.setFontSize(10f);
        ClientGlobals.cmdTextField.setPreferredWidth(ClientGlobals.cam.getWidth() * 0.90f);
        
        Camera cam = app.getCamera();
        Vector3f pref = menu.getPreferredSize();
        menu.setLocalTranslation(cam.getWidth() * 0.5f - pref.x * 0.5f, pref.y, 10);
        ClientGlobals.app.getGuiNode().attachChild(menu);
        
        world = new World();

        //world.addProcessor(new ImposterSystem(world, 1000));
        world.addProcessor(new ClientMonitorRenderSystem(world, 0));
        //world.addProcessor(new OrbitalMovementSystem(world, 0));
        //world.addProcessor(new ClientFixedRotationSystem(world, 0));

        ClientGlobals.network = new ClientNetworkSystem(world, 20);
        world.addProcessor(ClientGlobals.network);

        world.addProcessor(new EntityDeleteSystem(world, 0));

        world.initialize();

        ClientGlobals.network.sendLogin();
        //world.getSystem(ClientNetworkSystem.class).sendUseEntity();
    }

    @Override
    public void update(float tpf) {
        world.tick(System.currentTimeMillis());
    }

    @Override
    public void cleanup() {
        world.getProcessor(ClientNetworkSystem.class).sendLogout();
    }
}
