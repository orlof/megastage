package org.megastage.components.srv;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.util.ID;
import org.megastage.util.Mapper;

public class SphereOfInfluence extends BaseComponent {
    public double radius;
    public int priority = 0;
    
    public SphereOfInfluence() {
        super();
    }

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        radius = getDoubleValue(element, "radius", 0.0);
        return null;
    }

    @Override
    public void initialize(World world, Entity entity) {
        Orbit orbit = Mapper.ORBIT.get(entity);
        if(orbit == null) {
            priority = -1;
        } else {
            Mass mass = Mapper.MASS.get(entity);

            Entity center = world.getEntity(orbit.center);        
            Mass centerMass = Mapper.MASS.get(center);

            if(Mapper.ORBIT.get(center) != null) {
                priority++;
            }

            radius = calculateSOI(orbit.distance, mass.mass, centerMass.mass);
        }
    }

    @Override
    public String toString() {
        return "SphereOfInfluence(" + radius + ")";
    }

    public static double calculateSOI(double orbitalDistance, double mass, double centerMass) {
        return orbitalDistance * Math.pow(mass / centerMass, 0.4);
    }
    
    public static double calculateHillSphere(double orbitalDistance, double mass, double centerMass) {
        return orbitalDistance * Math.pow(mass / (3.0 * centerMass), 1.0 / 3.0);
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(SphereOfInfluence.calculateSOI(1.5e11, 6e24, 2e30));
        System.out.println(SphereOfInfluence.calculateHillSphere(1.5e11, 6e24, 2e30));
    }
}
