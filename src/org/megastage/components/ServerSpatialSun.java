/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Element;


    
/**
 *
 * @author Teppo
 */
public class ServerSpatialSun extends BaseComponent {
    public float radius;
    public int color;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 10.0f);

        color = getIntegerValue(element, "color_rgba", 0xffffff00); 
        //System.out.println(Integer.toHexString(color));
    }
}
