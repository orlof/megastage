package org.megastage.client.controls;

import com.badlogic.gdx.utils.IntMap;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.util.Time;

public class ForceFieldControl extends ForceShieldControl {
    public static int INTERVAL = 1500;
    
    IntMap<Long> lastHit = new IntMap<>();

    public ForceFieldControl(Material material) {
        super(material);
    }

    public void registerHit(Vector3f position, int id) {
        Long last = lastHit.get(id);
        if(last == null) last = 0l;

        if(Time.value < last + INTERVAL) {
            return;
        }
        lastHit.put(id, Time.value);
        super.registerHit(position); //To change body of generated methods, choose Tools | Templates.
    }

    
}
