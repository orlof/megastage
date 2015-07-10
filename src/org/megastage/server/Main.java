package org.megastage.server;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonReader;
import org.megastage.util.JsonUtil;
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
        
        ServerConfig cfg = readConfig(cmd.getString("--config", "server.json"));
        Game game = new Game(cfg);

        if(!game.loadSavedWorld()) {
            game.initializeNewWorld(root);
        }

        game.loopForever();
    }

    public static ServerConfig readConfig(String fileName) {
        Json json = JsonUtil.create();
        return json.fromJson(ServerConfig.class, new File(fileName));
    }
}
