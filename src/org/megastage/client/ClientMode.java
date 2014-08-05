package org.megastage.client;

import org.megastage.util.Log;

public enum ClientMode {
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
            InputController.enableMouseLook();
            InputController.commit();

            ClientGlobals.setCmdTextFieldEnabled(true);
        }

        @Override
        public void exit() {
            super.exit();
            ClientGlobals.setCmdTextFieldEnabled(false);
        }
    };
    
    public abstract void enter();

    public void exit() {
        ClientGlobals.mode = NONE;
            
        InputController.disableActions();
        InputController.showCrosshair(false);
    }

    public static void change(ClientMode newMode) {
        if(newMode == ClientGlobals.mode) return;
        
        Log.info("Mode change %s -> %s", ClientGlobals.mode, newMode);

        ClientGlobals.mode.exit();
        ClientGlobals.mode = newMode;
        ClientGlobals.mode.enter();
    }
}
