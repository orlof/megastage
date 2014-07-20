package org.megastage.components.srv;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class SphereOfInfluence extends BaseComponent {
    public double radius;
    public int priority = 0;
    
    public SphereOfInfluence() {
        super();
    }

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        radius = getDoubleValue(element, "radius", 0.0);
        return null;
    }

    @Override
    public void initialize(int eid) {
        Orbit orbit = (Orbit) World.INSTANCE.getComponent(eid, CompType.Orbit);
        if(orbit == null) {
            priority = -1;
        } else {
            Mass mass = (Mass) World.INSTANCE.getComponent(eid, CompType.Mass);
            Mass centerMass = (Mass) World.INSTANCE.getComponent(orbit.center, CompType.Mass);

            if(World.INSTANCE.hasComponent(orbit.center, CompType.Orbit)) {
                priority++;
            }

            radius = calculateSOI(orbit.distance, mass.value, centerMass.value);
        }
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
