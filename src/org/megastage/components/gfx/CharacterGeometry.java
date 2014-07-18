package org.megastage.components.gfx;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.BaseComponent;
import org.megastage.client.EntityNode;
import org.megastage.client.JME3Material;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.AxisRotationControl;
import org.megastage.client.controls.LocalPositionControl;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;


public class CharacterGeometry extends ReplicatedComponent {
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
        super.receive(eid);

        EntityNode node = SpatialManager.getOrCreateNode(eid);

        node.addControl(new LocalPositionControl(eid));
        node.addControl(new AxisRotationControl(eid, false, true, false));

        initGeometry(node.offset, eid);
    }

    private void initGeometry(Node node, int eid) {
        node.attachChild(createBody(eid));
        node.attachChild(createHead(eid));
    }

    private Spatial createBody(int eid) {
        Geometry body = new Geometry("body", new Box(0.25f, 0.5f, 0.25f));
        JME3Material.setLightingMaterial(body, new ColorRGBA(red, green, blue, alpha));
        return body;
    }

    private Spatial createHead(int eid) {
        Node head = new Node("head");
        head.setLocalTranslation(0, 1.0f, 0);

        Geometry geom = new Geometry("head", new Box(0.25f, 0.25f, 0.25f));
        JME3Material.setLightingMaterial(geom, new ColorRGBA(red, green, blue, alpha));

        AxisRotationControl headRotationControl = new AxisRotationControl(eid, true, false, false);
        head.addControl(headRotationControl);
        
        head.attachChild(geom);

        if(eid == ClientGlobals.playerEntity) {
            head.attachChild(ClientGlobals.camNode);
        }
        
        return head;
    }
}
