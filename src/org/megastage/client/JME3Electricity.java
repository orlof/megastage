package org.megastage.client;

import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public enum JME3Electricity {
    ELECTRICITY1("Materials/Electricity/electricity1.j3m"),
    ELECTRICITY1_2("Materials/Electricity/electricity1_2.j3m"),
    ELECTRICITY2("Materials/Electricity/electricity2.j3m"),
    ELECTRICITY2_2("Materials/Electricity/electricity2_2.j3m"),
    ELECTRICITY3_LINE1("Materials/Electricity/electricity3_line1.j3m"),
    ELECTRICITY3_LINE2("Materials/Electricity/electricity3_line2.j3m"),
    ELECTRICITY3_LINE3("Materials/Electricity/electricity3_line3.j3m"),
    ELECTRICITY4("Materials/Electricity/electricity4.j3m"),
    ELECTRICITY5_2("Materials/Electricity/electricity5_2.j3m");

    private String filename;
    private Material material;

    JME3Electricity(String filename) {
        this.filename = filename;
    }

    private Material getMaterial() {
        if(material == null) {
            material = ClientGlobals.app.getAssetManager().loadMaterial(filename);
        }

        return material;
    }

    public Node electrify(Node node) {
        for (Spatial child : ((Node) node).getChildren()){
            if (child instanceof Geometry){
                Geometry electricity = new Geometry("electrified_" + child.getName());
                electricity.setQueueBucket(RenderQueue.Bucket.Transparent);

                Geometry childGeometry = (Geometry) child;
                electricity.setMesh(childGeometry.getMesh());

                electricity.setMaterial(material);

                node.attachChild(electricity);
            }
        }

        return node;
    }
}
