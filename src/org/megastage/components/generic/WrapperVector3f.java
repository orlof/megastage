package org.megastage.components.generic;

import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

public class WrapperVector3f extends GenericComponent {
    public Vector3f value;

    public void config(Element elem) {
        super.config(elem);
        value = XmlUtil.getVector3fValue(elem, "value");
    }
}
