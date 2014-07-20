package org.megastage.server;

import org.megastage.util.Log;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws Exception {
        Log.set(Log.LEVEL_INFO);
        
        Element root = readConfig(args[0]);
        Game game = new Game(root);

        game.loopForever();
    }

    public static Element readConfig(String fileName) throws JDOMException, IOException {
        return new SAXBuilder().build(new File(fileName)).getRootElement();
    }
}
