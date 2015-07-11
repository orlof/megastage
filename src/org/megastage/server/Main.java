package org.megastage.server;

import org.jdom2.Element;
import org.megastage.util.CmdLineParser;
import org.megastage.util.Log;
import org.megastage.util.XmlUtil;

public class Main {
    public static void main(String args[]) throws Exception {
        CmdLineParser cmd = new CmdLineParser(args);
        Log.set(cmd.getInteger("--log-level", Log.LEVEL_INFO));
        
        ServerGlobals.autoexit = cmd.isDefined("--auto-exit");
        
        Element cfg = XmlUtil.read(cmd.getString("--config", "server.xml"));
        Game game = new Game(cfg);

        if(!game.loadSavedWorld()) {
            game.initializeNewWorld(cfg.getChild("entities"));
        }

        game.loopForever();
    }
}
