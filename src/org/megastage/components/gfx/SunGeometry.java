package org.megastage.components.gfx;

import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.client.JME3Material;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class SunGeometry extends CelestialGeometryComponent {
    public float radius;
    public float red, green, blue, alpha;
    public float lightRadius;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        radius = (float) (getFloatValue(element, "radius", 10.0f));
        lightRadius = (float) (getFloatValue(element, "light_radius", 2000000.0f));
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createSun());
        node.attachChild(createLightNode());
    }

    private Spatial createSun() {
        Geometry geom = createSphere(radius);

        ColorRGBA color = new ColorRGBA(red, green, blue, alpha);
        JME3Material.setUnshadedMaterial(geom, color);
        
        return geom;
    }

    private Spatial createLightNode() {
        PointLight light = new PointLight();
        light.setColor(new ColorRGBA(red, green, blue, alpha));
        light.setRadius(lightRadius);
        ClientGlobals.rootNode.addLight(light);

        return new LightNode("LightNode", light);
    }
}
