package org.megastage.util;

import java.awt.*;
import java.io.*;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLSettings {
    /**
     * The properties file that the settings are stored in.
     */
    private File file;

    /**
     * Root element of the settings
     */
    private Element root;

    // PUBLIC MANAGEMENT METHODS
    
    /**
     * Loads a user settings file at the specified path.
     *
     * @param dirName subdirectory name under user.home
     * @param fileName in specified subdirectory
     */
    public XMLSettings(String dirName, String fileName) {
        File dir = new File(System.getProperty("user.home"), dirName);
        dir.mkdir();

        file = new File(dir, fileName);

        if (file.exists()) {
            try {
                root = new SAXBuilder().build(file).getRootElement();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
        } else {
            root = new Element("config");
        }
        if (get("Application.Version", null) == null) {
            //if the format of the file changes from version to version, then this property can be used to convert the file to the new format
            set("Application.Version", "PreAlpha");
        }
    }

    /**
     * Writes the settings to disk.
     */
    public void save() {
        try {
            Format f = Format.getPrettyFormat();
            f.setTextMode(Format.TextMode.NORMALIZE);
            f.setIndent("  ");
            f.setLineSeparator(System.getProperty("line.separator"));

            XMLOutputter outputter = new XMLOutputter(f);

            FileOutputStream os = new FileOutputStream(file);
            outputter.output(root, os);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PUBLIC GENERIC ACCESS METHODS
    
    /**
     * retrieves a property as String
     *
     * @param path to the property value in xml
     * @param defaultValue
     * @return the int value or defaultValue if it is not set or could not be parsed
     */
    public String get(String path, String defaultValue) {
        String stringVal = get(path);
        if(stringVal.isEmpty()) {
            return defaultValue;
        }

        return stringVal;
    }

    public String set(String path, String value) {
        Element elem = getElement(path);
        elem.setText(value);
        return value;
    }

    
    /**
     * retrieves a property as int
     *
     * @param path to the property value in xml
     * @param defaultValue
     * @return the int value or defaultValue if it is not set or could not be parsed
     */
    public int get(String path, int defaultValue) {
        String stringVal = get(path);
        if(stringVal.isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(stringVal);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public void set(String path, int value) {
        set(path, String.valueOf(value));
    }
    
    /**
     * retrieves a property as int
     *
     * @param path to the property value in xml
     * @param defaultValue
     * @return the int value or defaultValue if it is not set or could not be parsed
     */
    public boolean get(String path, boolean defaultValue) {
        String stringVal = get(path);
        if(stringVal.isEmpty()) {
            return defaultValue;
        }

        return Boolean.parseBoolean(stringVal);
    }

    public void set(String path, boolean value) {
        set(path, String.valueOf(value));
    }

    /**
     * retrieves a property as int
     *
     * @param path to the property value in xml
     * @param defaultWidth
     * @param defaultHeight
     * @return the int value or defaultValue if it is not set or could not be parsed
     */
    public Dimension getAsDimension(String path, int defaultWidth, int defaultHeight) {
        return new Dimension(
                get(path + ".Width", defaultWidth),
                get(path + ".Height", defaultHeight));
    }

    public void set(String path, Dimension value) {
        set(path + ".Width", String.valueOf(value.width));
        set(path + ".Height", String.valueOf(value.height));
    }


    // PUBLIC SPECIFIC ACCESS METHODS

    /**
     * Gets the user's preferred Locale.
     *
     * @return
     */
    public Locale getLocale() {
        String localeStr = get("Application.Locale");
        if (localeStr != null) {
            int pos = localeStr.indexOf('_');
            if (pos >= 0) {
                return new Locale(localeStr.substring(0, pos), localeStr.substring(pos + 1));
            } else if (localeStr.length() > 0) {
                return new Locale(localeStr);
            }
        }
        return null;
    }

    /**
     * Sets the user's preferred Locale.
     *
     * @param locale
     */
    public void setLocale(Locale locale) {
        String localeStr = locale.getLanguage();
        if (locale.getCountry() != null && !locale.getCountry().isEmpty()) {
            localeStr += "_" + locale.getCountry();
        }
        set("Application.Locale", localeStr);
    }


    // PRIVATE HELPER ACCESS METHODS

    protected String get(String path) {
        return getElement(path).getText();
    }

    private Element getElement(String path) {
        Element current = root;
        for(String name: path.split("\\.")) {
            Element child = current.getChild(name);
            if(child == null) {
                child = new Element(name);
                current.addContent(child);
            }
            current = child;
        }
        return current;
    }
}
