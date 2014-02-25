package org.megastage.client.controls;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.shaderblow.forceshield.ForceShieldControl;
import org.megastage.util.Time;

public class ForceFieldControl extends ForceShieldControl {
    public static int INTERVAL = 1000;
    
    long lastHit;

    public ForceFieldControl(Material material) {
        super(material);
    }

    @Override
    public void registerHit(Vector3f position) {
        if(Time.value < lastHit + INTERVAL) {
            return;
        }
        lastHit = Time.value;
        super.registerHit(position); //To change body of generated methods, choose Tools | Templates.
    }

    
}
