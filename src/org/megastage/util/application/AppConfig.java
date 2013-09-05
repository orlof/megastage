/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util.application;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 *
 * @author contko3
 */
public class AppConfig {
    /**
     * I18n messages.
     */
    public static Messages messages;

    /**
     * The user's settings.
     */
    public static XMLSettings settings;

    /**
     * The classpath location of the icon.
     */
    public static Image image;
    
    public static String version;

    public static void init(String appName, String version) {
        AppConfig.version = version;

        AppConfig.image = new ImageIcon(appName + ".png").getImage();

        initSettings(appName + ".xml", version);
        initMessages("messages");
    }

    public static void initSettings(String fileName, String version) {
        settings = new XMLSettings(fileName, version);
    }

    private static void initMessages(String bundleName) {
        messages = new Messages(bundleName);

        //setup i18n
        Locale locale = settings.getLocale();
        if (locale != null) {
            messages.changeLocale(locale);
        }
    }
}
