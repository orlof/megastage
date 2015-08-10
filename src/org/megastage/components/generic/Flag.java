package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.XmlUtil;

public class Flag extends BaseComponent {
    public int cid;

    public void config(Element elem) {
        String type = XmlUtil.getStringValue(elem, "type");
        cid = CompType.getSpec(type).cid;
    }

    @Override
    public void receive(int eid, int cid) {
        World.INSTANCE.setComponent(eid, cid, this);
    }
}
