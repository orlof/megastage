package org.megastage.server;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Teppo
 * Date: 6.8.2013
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static final String version = "DEV";

    public static void main(String args[]) throws Exception {
        LogManager.getLogManager().readConfiguration(new FileInputStream("server_logging.properties"));
        Element root = readConfig(args[0]);
        Game game = new Game(root);
        game.loopForever();
    }

    public static Element readConfig(String fileName) throws JDOMException, IOException {
        return new SAXBuilder().build(new File(fileName)).getRootElement();
    }

}
