package org.megastage.components.generic;

import com.jme3.math.ColorRGBA;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

public class WrapperColorRGBA extends GenericComponent {
    public ColorRGBA value;

    public void config(Element elem) {
        super.config(elem);
        value = XmlUtil.getColorRGBAValue(elem, "rgba");
    }
}
