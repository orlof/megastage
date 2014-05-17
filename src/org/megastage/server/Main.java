package org.megastage.server;

import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import org.megastage.util.LogFormat;

public class Main {
    public static void main(String args[]) throws Exception {
        Log.setLogger(new LogFormat());
        Log.set(Log.LEVEL_INFO);
        
        Element root = readConfig(args[0]);
        Game game = new Game(root);

        game.world.synchronizeClocks(0, System.currentTimeMillis());
        game.world.setGametime(-20);
        game.loopForever();
    }

    public static Element readConfig(String fileName) throws JDOMException, IOException {
        return new SAXBuilder().build(new File(fileName)).getRootElement();
    }
}
