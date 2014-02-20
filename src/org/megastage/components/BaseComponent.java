package org.megastage.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:11
 */
public abstract class BaseComponent implements Component {
    public transient boolean dirty = true;

    // TODO change to static create(...)
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        return null;
    }

    /** This method is called after world is ready **/
    public void initialize(World world, Entity entity) {}

    public Message replicate(Entity entity) {
        return null;
    }

    public Message synchronize(Entity entity) {
        return null;
    }

    public final Message ifDirty(Entity entity) {
        if(dirty) {
            dirty = false;
            return new Network.ComponentMessage(entity, this);
        }
        return null;
    }

    public final Message replicateIfDirty(Entity entity) {
        if(dirty) {
            dirty = false;
            return replicate(entity);
        }
        return null;
    }

    public final Message replicateIfTrue(Entity entity, boolean check) {
        if(check) {
            dirty = false;
            return replicate(entity);
        }
        return null;
    }

    public final Message always(Entity entity) {
        dirty = false;
        return new Network.ComponentMessage(entity, this);
    }

    public void receive(Connection pc, Entity entity) {
        this.dirty = true;

        entity.addComponent(this);
        entity.changedInWorld();
    }

    public void delete(Connection pc, Entity entity) {
        entity.deleteFromWorld();
    }

    protected static boolean hasValue(Element config, String arrtName) {
        return config.getAttribute(arrtName) != null;
    }
    
    protected static String getStringValue(Element config, String attrName, String defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            return attr.getValue();
        }

        return defaultValue;
    }

    protected static boolean getBooleanValue(Element config, String attrName, boolean defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getBooleanValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    protected static long getLongValue(Element config, String attrName, long defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getLongValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    protected static int getIntegerValue(Element config, String attrName, int defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String value = attr.getValue();

            if(value.startsWith("0x")) {
                return (int) (Long.parseLong(value.substring(2), 16) & 0xffffffff);
            } else {
                return Integer.parseInt(value, 10);
            }
        }

        return defaultValue;
    }

    protected static double getDoubleValue(Element config, String attrName, double defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            if(val.endsWith("km") || val.endsWith("kg")) {
                val = val.substring(0, val.length()-2);
                return 1000.0f * Double.parseDouble(val);
            }
            return Double.parseDouble(val);
        }

        return defaultValue;
    }

    protected static float getFloatValue(Element config, String attrName, float defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            if(val.endsWith("km") || val.endsWith("kg")) {
                val = val.substring(0, val.length()-2);
                return 1000.0f * Float.parseFloat(val);
            }
            return Float.parseFloat(val);
        }

        return defaultValue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void reset() {}
}
