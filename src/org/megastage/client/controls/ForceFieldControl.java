package org.megastage.client.controls;

import com.artemis.Entity;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.minlog.Log;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.util.Mapper;
import org.megastage.util.GlobalTime;

public class ForceFieldControl extends ForceShieldControl {
    public static int INTERVAL = 1500;
    
    Entity entity;
    Spatial spatial;
    Sphere sphere;
    IntMap<Long> lastHit = new IntMap<>();
    
    public ForceFieldControl(Entity entity, Spatial spatial, Material material, Sphere sphere) {
        super(material);
        this.entity = entity;
        this.spatial = spatial;
        this.sphere = sphere;
    }

    public boolean isVisible() {
        return spatial.getCullHint() != Spatial.CullHint.Always;
    }
    
    @Override
    public void update(float tpf) {
        ForceFieldData data = Mapper.FORCE_FIELD_DATA.get(entity);
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

        if(GlobalTime.value < last + INTERVAL) {
            return;
        }
        lastHit.put(id, GlobalTime.value);
        super.registerHit(position);
    }

    
}
