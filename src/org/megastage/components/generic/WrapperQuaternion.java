package org.megastage.components.generic;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

public class WrapperQuaternion extends GenericComponent {
    public Quaternion value;

    public void config(Element elem) {
        super.config(elem);
        value = XmlUtil.getQuaternionValue(elem, "rgba");
    }
}
