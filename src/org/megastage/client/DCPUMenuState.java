package org.megastage.client;

import org.megastage.util.Log;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.BaseAppState;
import com.simsilica.lemur.style.ElementId;
import java.util.concurrent.Callable;
import org.megastage.util.XMLSettings;


public class DCPUMenuState extends BaseAppState {

    private XMLSettings settings;
    
    private Container menu;

    private AudioNode music;

    public DCPUMenuState() {
        setEnabled(false);
    }

    @Override
    protected void initialize( Application app ) {
        Log.info("");
        settings = new XMLSettings(".megastage", "user_options.xml");

        menu = new Container(new SpringGridLayout(), new ElementId(LemurStyles.MENU_ID), "retro");

        Label title1 = menu.addChild(new Label("Change Boot-ROM", new ElementId(LemurStyles.MENU_TITLE_ID), "retro"));

        for(String title: ClientGlobals.bootroms) {
            Button b = menu.addChild(new Button(title, "retro"));
            b.addClickCommands(new ChangeBootROMCommand(title));
            b.addCommands(Button.ButtonAction.HighlightOn, new Highlight());
        }
        
        Label title2 = menu.addChild(new Label("Change Floppy", new ElementId(LemurStyles.MENU_TITLE_ID), "retro"));

        for(String title: ClientGlobals.floppies) {
            Button b = menu.addChild(new Button(title, "retro"));
            b.addClickCommands(new ChangeFloppyCommand(title));
            b.addCommands(Button.ButtonAction.HighlightOn, new Highlight());
        }
        
        // We'll add exit later in the grid to get some space for the
        // menu panel.
        Button exit = menu.addChild(new Button("Resume", "retro"), 10, 0);
        exit.addClickCommands(new ResumeCommand());
        exit.addCommands(Button.ButtonAction.HighlightOn, new Highlight());

        Camera cam = app.getCamera();
        float menuScale = cam.getHeight()/720f;

        Vector3f pref = menu.getPreferredSize();
        float bias = (cam.getHeight() - (pref.y*menuScale)) * 0.35f;
        menu.setLocalTranslation(cam.getWidth() * 0.5f - pref.x * 0.5f * menuScale,
                                 cam.getHeight() * 0.5f + pref.y * 0.5f * menuScale + bias,
                                 10);
        menu.setLocalScale(menuScale);
        
        //getApplication().getInputManager().setCursorVisible(true);
    }

    @Override
    protected void cleanup( Application app ) {
    }

    @Override
    protected void enable() {
        ClientGlobals.app.enqueue(new Callable() { @Override public Object call() throws Exception {
            Main main = (Main)getApplication();
            main.getGuiNode().attachChild(menu);
            main.getInputManager().setCursorVisible(true);
            return null;
        }});
        
   }

    @Override
    protected void disable() {
        Log.info("");
        ClientGlobals.app.enqueue(new Callable() { @Override public Object call() throws Exception {
            menu.removeFromParent();
            getApplication().getInputManager().setCursorVisible(false);
            return null;
        }});

        //getApplication().getStateManager().detach(this);
    }

    private class Highlight implements Command<Button> {
        @Override
        public void execute( Button source ) {
//            selectNeutral.playInstance();
        }
    }

    private class ResumeCommand implements Command<Button> {
        @Override
        public void execute( Button source ) {
            Log.info("");
            ClientGlobals.userCommand.unpickItem();
        }
    }

    private class ChangeBootROMCommand implements Command<Button> {
        String filename;
        private ChangeBootROMCommand(String filename) {
            this.filename = filename;
        }
        @Override
        public void execute( Button source ) {
            ClientGlobals.userCommand.changeBootRom(filename);
        }
    }

    private class ChangeFloppyCommand implements Command<Button> {
        String filename;
        private ChangeFloppyCommand(String filename) {
            this.filename = filename;
        }
        @Override
        public void execute( Button source ) {
            ClientGlobals.userCommand.changeFloppy(filename);
        }
    }
}