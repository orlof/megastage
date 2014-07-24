package org.megastage.client;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.BaseAppState;
import com.simsilica.lemur.style.ElementId;
import org.megastage.util.XMLSettings;


public class MainMenuState extends BaseAppState {

    private XMLSettings settings;
    
    private Container menu;

    private Container network;
    private boolean networkPanelOpen = false;

    private TextField userField;
    private TextField hostField;

    private AudioNode music;

    public MainMenuState() {
    }

    @Override
    protected void initialize( Application app ) {
        settings = new XMLSettings(".megastage", "user_options.xml");

        menu = new Container(new SpringGridLayout(), new ElementId(LemurStyles.MENU_ID), "retro");

        Label title = menu.addChild(new Label("", new ElementId(LemurStyles.MENU_TITLE_ID), "retro"));

        IconComponent titleImage = new IconComponent("Interface/logo.png", new Vector2f(1.5f, 1.0f), 5, 5, 0, false);
        title.setBackground(titleImage);
         
        Button start = menu.addChild(new Button("Play", "retro"));
        start.addClickCommands(new Play());

        Button multi = menu.addChild(new Button("Network", "retro"));
        multi.addClickCommands(new OpenNetwork());

        // We'll add exit later in the grid to get some space for the
        // menu panel.
        Button exit = menu.addChild(new Button("Exit", "retro"), 10, 0);
        exit.addClickCommands(new Exit());

        Camera cam = app.getCamera();
        float menuScale = cam.getHeight()/720f;

        Vector3f pref = menu.getPreferredSize();
        float bias = (cam.getHeight() - (pref.y*menuScale)) * 0.35f;
        menu.setLocalTranslation(cam.getWidth() * 0.5f - pref.x * 0.5f * menuScale,
                                 cam.getHeight() * 0.5f + pref.y * 0.5f * menuScale + bias,
                                 10);
        menu.setLocalScale(menuScale);


        // Create the dymamic network settings
        network = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Last), 
                                new ElementId(LemurStyles.SUBMENU_ID), "retro");
        network.addChild(new Label("Player:", new ElementId(LemurStyles.EDIT_LABEL_ID), "retro"));
        userField = network.addChild(new TextField(settings.get("player", ClientGlobals.player), "retro"), 1);
        network.addChild(new Label("Server:", new ElementId(LemurStyles.EDIT_LABEL_ID), "retro"));
        hostField = network.addChild(new TextField(settings.get("server", ClientGlobals.serverHost), "retro"), 1);
    }

    @Override
    protected void cleanup( Application app ) {
    }

    @Override
    public void update( float tpf ) {
        startMusic();
    }

    protected void startMusic() {
        if( music == null || music.getStatus() == AudioSource.Status.Stopped ) {
            AssetManager assets = getApplication().getAssetManager();
            music = new AudioNode(assets, "Sounds/AllThis.ogg", true);
            music.setReverbEnabled(false);
            music.setPositional(false);
            music.play();
        }
    }

    protected void stopMusic() {
        if( music != null ) {
            music.stop();
            music = null;
        }
    }

    @Override
    protected void enable() {
        Main main = (Main) getApplication();
        main.getGuiNode().attachChild(menu);
        startMusic();
    }

    @Override
    protected void disable() {
        menu.removeFromParent();
        stopMusic();
    }

    private class Play implements Command<Button> {
        @Override
        public void execute( Button source ) {
            storeOptions();
            stopMusic();
            setEnabled(false);
            getApplication().getInputManager().setCursorVisible(false);
            ClientGlobals.setAppStates(ECSState.class);
        }
    }

    private class OpenNetwork implements Command<Button> {
        @Override
        public void execute( Button source ) {
            networkPanelOpen = !networkPanelOpen;
            if( networkPanelOpen ) {
                menu.addChild(network, 9, 0);
            } else {
                menu.removeChild(network);
            }
        }
    }
    
    private void storeOptions() {
        ClientGlobals.player = settings.set("player", userField.getText());
        ClientGlobals.serverHost = settings.set("server", hostField.getText());
        settings.save();
    }
    
    private class Exit implements Command<Button> {
        @Override
        public void execute( Button source ) {
            storeOptions();
            getApplication().stop();
        }
    }
}