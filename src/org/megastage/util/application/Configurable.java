package org.megastage.util.application;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Teppo
 * Date: 24.1.2012
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class Configurable {
    public int id = 0;
    public Element data;

    private static int nextID = 1;
    public static synchronized int nextID() { return nextID++; }

    public Configurable(String name) {
        // Server Side
        data = new Element(name)
                .setAttribute("id", String.valueOf(id));

        this.id = nextID();
    }

    public Configurable(Element e) throws DataConversionException {
        // Client Side
        this.data = e;
        this.id   = e.getAttribute("id").getIntValue();
    }
    
    public String getName() {
        return data.getName();
    }
    
    public boolean isName(String name) {
        return name.equalsIgnoreCase(getName());
    }
    
    public boolean setParameter(String name, String value, boolean create) {
        return setParameter(name, "string", value, create);
    }

    public boolean setParameter(String name, int value, boolean create) {
        return setParameter(name, "string", String.valueOf(value), create);
    }

    public boolean setParameter(String name, float value, boolean create) {
        return setParameter(name, "string", String.valueOf(value), create);
    }

    public boolean setParameter(String name, boolean value, boolean create) {
        return setParameter(name, "string", String.valueOf(value), create);
    }

    public String getString(String parameter) throws NoSuchParameterException {
        return getElement(parameter, false).getText();
    }

    public String getString(String parameter, String defaultValue) {
        try {
            return getString(parameter);
        } catch (NoSuchParameterException e) {}
        return defaultValue;
    }

    public int getInt(String parameter) throws NoSuchParameterException {
        return Integer.parseInt(getString(parameter));
    }

    public int getInt(String parameter, int defaultValue) {
        try {
            return getInt(parameter);
        } catch (NoSuchParameterException e) {}
        return defaultValue;
    }

    public float getFloat(String parameter) throws NoSuchParameterException {
        return Float.parseFloat(getString(parameter));
    }

    public float getFloat(String parameter, float defaultValue) {
        try {
            return getFloat(parameter);
        } catch (NoSuchParameterException e) {}
        return defaultValue;
    }

    public double getDouble(String parameter) throws NoSuchParameterException {
        return Double.parseDouble(getString(parameter));
    }

    public double getDouble(String parameter, double defaultValue) {
        try {
            return getDouble(parameter);
        } catch (NoSuchParameterException e) {}
        return defaultValue;
    }

    public boolean getBoolean(String parameter) throws NoSuchParameterException {
        return Boolean.parseBoolean(getString(parameter));
    }

    public boolean getBoolean(String parameter, boolean defaultValue) {
        try {
            return getBoolean(parameter);
        } catch (NoSuchParameterException e) {}
        return defaultValue;
    }

    public void setAttribute(String parameter, String attr, String value) throws NoSuchParameterException {
        getElement(parameter, true).setAttribute(attr, value);
    }

    private boolean setParameter(String name, String type, String value, boolean create) {
        try {
            getElement(name, create)
                    .setAttribute("type", type)
                    .setText(value);

            return true;
        } catch (NoSuchParameterException e) {
            return false;
        }
    }

    private Element getElement(String path, boolean create) throws NoSuchParameterException {
        Element current = data;
        for(String name: path.split("\\.")) {
            Element child = current.getChild(name);
            if(child == null) {
                if(create) {
                    child = new Element(name);
                    current.addContent(child);
                } else {
                    // throw new NoSuchParameterException(data.getName() + "["+id+"]." + path);
                    throw new NoSuchParameterException(data.getName() + "." + path);
                }
            }
            current = child;
        }
        return current;
    }
}

