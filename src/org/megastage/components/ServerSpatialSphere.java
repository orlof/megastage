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
public class ServerSpatialSphere extends BaseComponent {
    public int zSamples, radialSamples;
    public float radius;
    public int color;

    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        zSamples = getIntegerValue(element, "z_samples", 5);
        radialSamples = getIntegerValue(element, "radial_samples", 5);
        radius = getFloatValue(element, "radius", 10.0f);

        color = getIntegerValue(element, "color_rgba", 0xffffff00); 
        //System.out.println(Integer.toHexString(color));
    }
}
