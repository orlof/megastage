package org.megastage.components.geometry;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.simsilica.lemur.Label;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.client.EntityNode;
import org.megastage.client.JME3Material;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.AxisRotationControl;
import org.megastage.client.controls.LocalPositionControl;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

import java.awt.image.BufferedImage;

public class CharacterGeometry extends BaseComponent {
    public ColorRGBA rgba;

    @Override
    public void config(Element elem) {
        rgba = XmlUtil.getColorRGBAValue(elem, "rgba");
    }

    @Override
    public void receive(int eid) {
        super.receive(eid);

        EntityNode node = SpatialManager.getOrCreateCleanNode(eid);

        node.addControl(new LocalPositionControl(eid));
        node.addControl(new AxisRotationControl(eid, false, true, false));

        initGeometry(node.offset, eid);
    }

    @Override
    public void delete(int eid) {
        EntityNode node = SpatialManager.getOrCreateNode(eid);
        node.removeFromParent();
    }

    private void initGeometry(Node node, int eid) {
        node.attachChild(createBody(eid));
        node.attachChild(createHead(eid));
        node.attachChild(createNameTag(eid));
    }

    private Spatial createBody(int eid) {
        Geometry body = new Geometry("body", new Box(0.25f, 0.5f, 0.25f));
        JME3Material.setLightingMaterial(body, rgba);
        return body;
    }

    private Spatial createHead(int eid) {
        Node head = new Node("head");
        head.setLocalTranslation(0, 1.0f, 0);

        Geometry geom = new Geometry("head", new Box(0.25f, 0.25f, 0.25f));
        JME3Material.setLightingMaterial(geom, rgba);

        AxisRotationControl headRotationControl = new AxisRotationControl(eid, true, false, false);
        head.addControl(headRotationControl);

        head.attachChild(geom);

        head.attachChild(createFace());

        if (eid == ClientGlobals.playerEntity) {
            head.attachChild(ClientGlobals.camNode);
        }

        return head;
    }

    private Spatial createFace() {
        BufferedImage img = ClientGlobals.loadImage("Textures/smiley.png");

        Image img2 = new AWTLoader().load(img, true);

        Texture2D tex = new Texture2D(img2);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.Trilinear);

        Material mat = JME3Material.getBasicMaterial("Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);

        Quad quad = new Quad(0.5f, 0.5f);

        Geometry geom = new Geometry("face");
        geom.setMesh(quad);
        geom.setMaterial(mat);
        geom.setLocalTranslation(-0.25f, -0.25f, 0.3f);
        return geom;
    }

    private Spatial createNameTag(int eid) {
        Node node = new Node();
        node.setLocalTranslation(0.0f, 2.0f, 0.0f);

        Label tag = new Label("", "retro");
        tag.setFontSize(0.3f);

        Vector3f pref = tag.getPreferredSize();

        float scale = pref.x;
        if(pref.y > scale) scale = pref.y;
        if(pref.z > scale) scale = pref.z;

        tag.scale(1.0f / scale);
        tag.setLocalTranslation(-(pref.x / scale) * 0.5f, -(pref.y / scale) * 0.5f, 0.0f);

        node.addControl(new BillboardControl());

        node.attachChild(tag);
        
        return node;
    }
}
