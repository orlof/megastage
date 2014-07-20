package org.megastage.components.gfx;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.client.JME3Material;
import static org.megastage.client.SpatialManager.getOrCreateNode;
import org.megastage.client.controls.ImposterPositionControl;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class ImposterGeometry extends ReplicatedComponent {
    public float radius;
    public double cutoff;
    public float red, green, blue, alpha;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 20.0f);
        cutoff = getDoubleValue(element, "cutoff", 500000.0);
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    public void receive(int eid) {
        assert !World.INSTANCE.hasComponent(eid, CompType.ImposterGeometry);
        setupImposter(eid, this);
    }

    private Geometry createImposter(float size, ColorRGBA color) {
        Mesh q = new Mesh();

        Vector3f [] vertices = new Vector3f[] { new Vector3f(0,0,0) };
        q.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

        q.setMode(Mesh.Mode.Points);
        q.setPointSize(size);
        q.updateBound();
        q.setStatic();

        Material mat = JME3Material.getUnshadedMaterial(color);
        
        Geometry geom = new Geometry("imposter", q);
        geom.setMaterial(mat);

        return geom;
    }

    public void setupImposter(final int eid, final ImposterGeometry data) {
        ColorRGBA colorRGBA = new ColorRGBA(data.red, data.green, data.blue, data.alpha);

        final Geometry imposter = createImposter(data.radius, colorRGBA);
        final Node node = getOrCreateNode(eid);
        
        node.addControl(new ImposterPositionControl(eid));
        node.attachChild(imposter);
    }


}
