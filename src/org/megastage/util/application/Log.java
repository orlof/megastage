package org.megastage.util.application;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MegaStage
 * User: Orlof
 * Date: 9.9.2013
 * Time: 22:16
 */
public class Log {
    public static void info(Class clazz, String msg) {
        Logger.getLogger(clazz.getName()).log(Level.INFO, msg);
    }
}
