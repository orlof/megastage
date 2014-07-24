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
        Log.set(Log.LEVEL_INFO);
        
        CmdLineParser cmd = new CmdLineParser(args);
        ServerGlobals.autoexit = cmd.isDefined("-autoexit");
        
        Element root = readConfig(cmd.getString("-config", "world.xml"));
        Game game = new Game(root);

        game.loopForever();
    }

    public static Element readConfig(String fileName) throws JDOMException, IOException {
        return new SAXBuilder().build(new File(fileName)).getRootElement();
    }
}
