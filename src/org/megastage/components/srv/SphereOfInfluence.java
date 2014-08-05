package org.megastage.components.srv;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.Log;

public class SphereOfInfluence extends BaseComponent {
    public float radius;
    
    public SphereOfInfluence() {
        super();
    }

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        return null;
    }

    @Override
    public void initialize(int eid) {
        Orbit orbit = (Orbit) World.INSTANCE.getComponent(eid, CompType.Orbit);
        if(orbit == null) {
            Log.warn("Cannot calculate SOI for [%d]", eid);
        } else {
            Mass mass = (Mass) World.INSTANCE.getComponent(eid, CompType.Mass);
            Mass centerMass = (Mass) World.INSTANCE.getComponent(orbit.center, CompType.Mass);

            radius = calculateSOI(orbit.distance, mass.value, centerMass.value);
        }
    }

    public static float calculateSOI(float orbitalDistance, float mass, float centerMass) {
        return (float) (orbitalDistance * Math.pow(mass / centerMass, 0.4f));
    }
    
    public static double calculateHillSphere(double orbitalDistance, double mass, double centerMass) {
        return orbitalDistance * Math.pow(mass / (3.0 * centerMass), 1.0 / 3.0);
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(SphereOfInfluence.calculateSOI(1.5e11f, 6e24f, 2e30f));
        System.out.println(SphereOfInfluence.calculateHillSphere(1.5e11, 6e24, 2e30));
    }
}
