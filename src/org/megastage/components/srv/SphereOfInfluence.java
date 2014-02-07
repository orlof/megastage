package org.megastage.components.srv;

import com.artemis.Entity;
import com.artemis.World;
import org.megastage.components.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class SphereOfInfluence extends BaseComponent {
    public double radius;
    
    public SphereOfInfluence() {
        super();
    }

    @Override
    public void initialize(World world, Entity entity) {
        Orbit orbit = entity.getComponent(Orbit.class);

        Entity center = entity;
        Orbit centerOrbit = orbit;
        
        while(centerOrbit != null) {
            center = world.getEntity(centerOrbit.center);
            centerOrbit = center.getComponent(Orbit.class);
        }

        Mass mass = entity.getComponent(Mass.class);
        Mass centerMass = center.getComponent(Mass.class);
        
        radius = orbit.distance * Math.pow(mass.mass / centerMass.mass, 0.4);
    }

    

    public String toString() {
        return "SphereOfInfluence(" + radius + ")";
    }
}
