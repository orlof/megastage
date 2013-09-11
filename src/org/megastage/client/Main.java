/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import org.megastage.util.application.AppConfig;

import java.io.FileInputStream;
import java.util.logging.LogManager;

/**
 *
 * @author contko3
 */
public class Main {
    public static final String VERSION = "DEV";

    public static void main(String[] args) throws Exception {
        //setup the logger
        LogManager.getLogManager().readConfiguration(new FileInputStream("client_logging.properties"));

        AppConfig.init("megastage_client", VERSION);

        Game game = new Game();
        game.loopForever();
    }
    
}
