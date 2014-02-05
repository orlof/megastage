/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 *
 * @author Orlof
 */
public class RadarEcho extends BaseComponent {
    public int type;
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = getIntegerValue(element, "echo", 0);
        
        return null;
    }

    public String toString() {
        return "RadarEcho(" + type + ")";
    }
}
