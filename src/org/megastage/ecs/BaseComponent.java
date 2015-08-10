package org.megastage.ecs;

import org.jdom2.Element;
import org.megastage.protocol.Carrier;
import org.megastage.protocol.Message;
import org.megastage.util.MegastageException;
import org.megastage.util.XmlUtil;

public abstract class BaseComponent implements Carrier {
    /** This method is called when initial state component is created from template **/
    public void config(Element elem) {}

    /** This method is called after world is ready **/
    public void initialize(int eid) {}

    /** This method is called when entity is deleted **/
    public void delete(int eid) {}

    public Message replicate(int eid) {
        return null;
    }

    public void receive(int eid) {
        throw new MegastageException("BaseComponent cannot carry data to client %s", this);
    }

    public static BaseComponent create(CompSpec spec, Element elem) {
        try {
            Class clazz = XmlUtil.getClassValue(elem, "class", spec.clazz);

            BaseComponent comp = (BaseComponent) clazz.newInstance();
            comp.config(elem);

            return comp;

        } catch (Exception e) {
            e.printStackTrace();
            throw new MegastageException(e);
        }
    }

    public String toString() {
        return ToString.make(this);
    }
}
