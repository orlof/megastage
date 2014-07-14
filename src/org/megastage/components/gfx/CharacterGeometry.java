package org.megastage.components.gfx;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.client.EntityNode;
import org.megastage.client.JME3Material;
import org.megastage.client.SpatialManager;
import static org.megastage.client.SpatialManager.getOrCreateNode;
import org.megastage.client.controls.AxisRotationControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.ecs.World;

public class CharacterGeometry extends GeometryComponent {
    public float red, green, blue, alpha;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    public void receive(int eid) {
        if(eid == ClientGlobals.playerEntity) {
            World.INSTANCE.setComponent(eid, this);
        } else {
            World.INSTANCE.setComponent(eid, this);
            EntityNode node = SpatialManager.getOrCreateNode(eid);

            PositionControl positionControl = new PositionControl(eid);
            AxisRotationControl bodyRotationControl = new AxisRotationControl(eid, false, true, false);
            AxisRotationControl headRotationControl = new AxisRotationControl(eid, true, false, false);
            
            node.addControl(positionControl);
            node.addControl(bodyRotationControl);

            Geometry body = new Geometry("body", new Box(0.25f, 0.5f, 0.25f));
            JME3Material.setLightingMaterial(body, new ColorRGBA(red, green, blue, alpha));
            node.attachChild(body);
            
            Geometry head = new Geometry("head", new Box(0.25f, 0.25f, 0.25f));
            head.setLocalTranslation(0, 1.0f, 0);
            JME3Material.setLightingMaterial(head, new ColorRGBA(red, green, blue, alpha));

            head.addControl(headRotationControl);
            node.attachChild(head);

            ClientGlobals.globalRotationNode.attachChild(node);

            initGeometry(node.offset, eid);
        }
    }
}
