package org.megastage.client.controls;

import com.artemis.Entity;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.minlog.Log;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

public class ForceFieldControl extends ForceShieldControl {
    public static int INTERVAL = 1500;
    
    Entity entity;
    Spatial spatial;
    IntMap<Long> lastHit = new IntMap<>();
    
    public ForceFieldControl(Entity entity, Spatial spatial, Material material) {
        super(material);
        this.entity = entity;
        this.spatial = spatial;
    }

    public boolean isPowered() {
        return spatial.getCullHint() != Spatial.CullHint.Always;
    }
    
    @Override
    public void update(float tpf) {
        ForceFieldData data = Mapper.FORCE_FIELD_DATA.get(entity);
        if(data == null) return;

        if(!isPowered() && data.energy > 0) {
            Log.info("Enable force field");
            spatial.setCullHint(Spatial.CullHint.Inherit);
        } else if(isPowered() && data.energy <= 0) {
            Log.info("Enable force field");
            spatial.setCullHint(Spatial.CullHint.Always);
        }
        super.update(tpf);
    }

    public void registerHit(Vector3f position, int id) {
        Long last = lastHit.get(id);
        if(last == null) last = 0l;

        if(Time.value < last + INTERVAL) {
            return;
        }
        lastHit.put(id, Time.value);
        super.registerHit(position);
    }

    
}
