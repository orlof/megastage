package org.megastage.ecs;

import org.jdom2.Element;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network;
import org.megastage.util.MegastageException;

public abstract class BaseComponent extends ToStringComponent {
    public transient boolean dirty = true;

    /** This message is called when server wraps this component for sending to client as status update */
    public Message synchronize(int eid) {
        return new Network.ComponentMessage(eid, this);
    }

    /** This method is called when client receives this component as a state update */
    public void receive(int eid) {
        throw new MegastageException("Receive is not implemented in %s", this.getClass().getName());
    }

    /** This method is called when initial state component is created from template **/
    public void config(Element elem) {}

    /** This method is called after world is ready **/
    public void initialize(int eid) {}

    /** This method is called when entity is deleted **/
    public void delete(int eid) {}

    public static BaseComponent create(Element elem) {
        try {
            CompSpec spec = CompType.getSpec(elem.getAttributeValue("type"));
            BaseComponent comp = (BaseComponent) spec.clazz.newInstance();
            comp.config(elem);
            return comp;

        } catch (Exception e) {
            e.printStackTrace();
            throw new MegastageException(e);
        }
    }
}
