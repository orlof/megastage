package org.megastage.server;

import org.megastage.util.CmdLineParser;
import org.megastage.util.Log;

public class Main {
    public static void main(String args[]) throws Exception {
        useCmdLineArguments(args);

        Game game = new Game();

        if(!game.loadSavedWorld()) {
            game.initializeNewWorld();
        }

        game.loopForever();
    }

    private static void useCmdLineArguments(String[] args) {
        CmdLineParser cmd = new CmdLineParser(args);
        Log.set(cmd.getInteger("--log-level", Log.LEVEL_INFO));

        ServerGlobals.autoexit = cmd.isDefined("--auto-exit");
        ServerGlobals.prefabPath = cmd.getString("--prefabs", "prefabs");
    }
}
