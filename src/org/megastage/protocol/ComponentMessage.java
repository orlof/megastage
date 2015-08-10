package org.megastage.protocol;

import org.megastage.ecs.BaseComponent;

public class ComponentMessage implements Message {
    public int eid;
    public BaseComponent component;

    public ComponentMessage() { /* required for Kryo */ }

    public ComponentMessage(int eid, BaseComponent baseComponent) {
        this.eid = eid;
        this.component = baseComponent;
    }

    @Override
    public void receive() {
        component.receive(eid);
    }

    @Override
    public String toString() {
        return "ComponentMessage(" + eid + ", " + component.toString() + ")";
    }
}
