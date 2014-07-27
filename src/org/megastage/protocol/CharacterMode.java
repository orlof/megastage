package org.megastage.protocol;

import com.jme3.renderer.Camera;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.TextEntryComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.client.DCPUMenuState;
import org.megastage.client.ECSState;
import org.megastage.client.InputController;
import org.megastage.client.Main;
import org.megastage.util.Log;

public enum CharacterMode {
    NONE {
        @Override
        public void enter() {
        }

        @Override
        public void exit() {
        }
    },
    WALK {
        @Override
        public void enter() {
            ClientGlobals.mode = WALK;

            InputController.enableMouseLook();
            InputController.enableWalk();
            InputController.enablePickItem();
            InputController.enableTextCommand();
            InputController.enableGameExit();

            InputController.enableDebug();
            
            InputController.showCrosshair(true);
            InputController.commit();
        }
    },
    DCPU {
        @Override
        public void enter() {
            ClientGlobals.mode = DCPU;
            
            InputController.enableMouseLook();
            InputController.enableDCPU();

            InputController.commit();
        }
    },
    MENU {
        @Override
        public void enter() {
            ClientGlobals.mode = MENU;

            InputController.enableMenuExit();
            InputController.commit();

            ClientGlobals.setAppStates(DCPUMenuState.class, ECSState.class);
        }

        @Override
        public void exit() {
            super.exit();
            ClientGlobals.setAppStates(ECSState.class);
        }
    },
    TEXT {
        @Override
        public void enter() {
            ClientGlobals.mode = TEXT;

            InputController.enableTextCommandExit();
            //InputController.enableMouseLook();
            InputController.commit();
           
            //TextEntryComponent tec = new TextEntryComponent(null);
            TextField textField = new TextField("", "retro");
            textField.setFontSize(0.3f);
            
            Main main = ClientGlobals.app;
            
            Camera cam = main.getCamera();
            textField.setPreferredWidth(cam.getWidth() * 0.8f);
            textField.setLocalTranslation(cam.getWidth() * 0.1f, 20.0f, 0.0f);
            
            main.getInputManager().setCursorVisible(true);
            main.getGuiNode().attachChild(textField);
        }
    };
    
    public abstract void enter();

    public void exit() {
        ClientGlobals.mode = NONE;
            
        InputController.disableActions();
        InputController.showCrosshair(false);
    }

    public static void change(CharacterMode newMode) {
        if(newMode == ClientGlobals.mode) return;
        
        Log.info("Mode change %s -> %s", ClientGlobals.mode, newMode);

        ClientGlobals.mode.exit();
        ClientGlobals.mode = newMode;
        ClientGlobals.mode.enter();
    }
}
