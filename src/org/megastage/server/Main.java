package org.megastage.server;

import org.megastage.util.CmdLineParser;
import org.megastage.util.Log;

public class Main {
    public static void main(String args[]) throws Exception {
        parseCmdLineArguments(args);

        Game game = new Game();

        if(!game.loadSavedWorld()) {
            game.initializeNewWorld();
        }

        game.loopForever();
    }

    private static void parseCmdLineArguments(String[] args) {
        CmdLineParser cmd = new CmdLineParser(args);
        Log.set(cmd.getInteger("--log-level", Log.LEVEL_INFO));

        ServerGlobals.autoExit = cmd.isDefined("--auto-exit");
        ServerGlobals.prefabDir = cmd.getString("--prefabs-dir", "prefabs");
        ServerGlobals.saveDir = cmd.getString("--save-dir", "savegames");
    }
}
