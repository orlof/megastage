package org.megastage.server;

import org.megastage.util.Log;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import org.megastage.util.CmdLineParser;

public class Main {
    public static void main(String args[]) throws Exception {
        CmdLineParser cmd = new CmdLineParser(args);
        Log.set(cmd.getInteger("--log-level", Log.LEVEL_INFO));
        
        ServerGlobals.autoexit = cmd.isDefined("--auto-exit");
        
        Element root = readConfig(cmd.getString("--config", "world.xml"));
        Game game = new Game(root);

        if(!game.loadSavedWorld()) {
            game.initializeNewWorld(root);
        }

        game.loopForever();
    }

    public static Element readConfig(String fileName) throws JDOMException, IOException {
        return new SAXBuilder().build(new File(fileName)).getRootElement();
    }
}
