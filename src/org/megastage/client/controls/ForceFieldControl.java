package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.IntMap;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.client.ClientGlobals;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ForceFieldControl extends ForceShieldControl {
    public static int INTERVAL = 1500;
    
    int eid;
    Spatial spatial;
    Sphere sphere;
    IntMap<Long> lastHit = new IntMap<>();
    
    public ForceFieldControl(int eid, Spatial spatial, Material material, Sphere sphere) {
        super(material);
        this.eid = eid;
        this.spatial = spatial;
        this.sphere = sphere;
    }

    public boolean isVisible() {
        return spatial.getCullHint() != Spatial.CullHint.Always;
    }
    
    @Override
    public void update(float tpf) {
        ForceFieldData data = (ForceFieldData) ClientGlobals.world.getComponent(eid, CompType.ForceFieldGeometry);
        if(data == null) return;

        if(!isVisible() && data.isVisible()) {
            Log.info("Enable force field");
            spatial.setCullHint(Spatial.CullHint.Inherit);
        } else if(isVisible() && !data.isVisible()) {
            Log.info("Disable force field");
            spatial.setCullHint(Spatial.CullHint.Always);
        }

        if(data.radius != sphere.getRadius()) {
            sphere.updateGeometry(32, 32, data.radius);
        }
        
        super.update(tpf);
    }

    public void registerHit(Vector3f position, int id) {
        Long last = lastHit.get(id);
        if(last == null) last = 0l;

        if(World.INSTANCE.time < last + INTERVAL) {
            return;
        }
        lastHit.put(id, World.INSTANCE.time);
        super.registerHit(position);
    }
}
