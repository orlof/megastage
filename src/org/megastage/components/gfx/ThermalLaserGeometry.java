package org.megastage.components.gfx;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.util.Vector3d;
    
public class ThermalLaserGeometry extends GeometryComponent {
    public float length;
    public Vector3d attackVector;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        length = getFloatValue(element, "length", 3.0f);
        attackVector = getVector3d(element, "attack_vector", new Vector3d(0,0,-1));
        return null;
    }
}
