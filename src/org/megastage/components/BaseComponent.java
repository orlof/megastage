package org.megastage.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:11
 */
public abstract class BaseComponent extends Component {
    public abstract void init(World world, Entity parent, Element element) throws Exception;

    protected long getLongValue(Element config, String attrName, long defaultValue) {
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
}
