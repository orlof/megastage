package org.megastage.client.controls;

import org.megastage.util.Log;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.IntMap;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ForceFieldControl extends ForceShieldControl {
    public static int HIT_RATE = 1500;

    private final IntMap<Long> lastHit = new IntMap<>();
    
    private final int eid;
    private Spatial spatial;
    
    public ForceFieldControl(int eid, Material material) {
        super(material);
        this.eid = eid;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException("This control has already been added to a Spatial");
        }
        
        super.setSpatial(spatial);
        this.spatial = spatial;
    }

    public boolean isVisible() {
        return spatial.getCullHint() != Spatial.CullHint.Always;
    }
    
    @Override
    public void update(float tpf) {
        ForceFieldData data = (ForceFieldData) World.INSTANCE.getComponent(eid, CompType.ForceFieldData);
        assert data != null;

        if(!isVisible() && data.isVisible()) {
            Log.info("Enable force field");
            spatial.setCullHint(Spatial.CullHint.Inherit);
        } else if(isVisible() && !data.isVisible()) {
            Log.info("Disable force field");
            spatial.setCullHint(Spatial.CullHint.Always);
        }

        Sphere sphere = (Sphere) ((Geometry) spatial).getMesh();
        if(data.radius != sphere.getRadius()) {
            sphere.updateGeometry(32, 32, data.radius);
        }
        
        super.update(tpf);
    }

    public void registerHit(Vector3f position, int id) {
        Long last = lastHit.get(id);
        if(last == null) last = 0l;

        if(World.INSTANCE.time < last + HIT_RATE) {
            return;
        }
        lastHit.put(id, World.INSTANCE.time);
        super.registerHit(position);
    }
}
