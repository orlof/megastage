package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Globals;
import org.megastage.util.Vector3d;

public class Orbit extends ReplicatedComponent {
    public int center;
    public double distance;

    public double angularSpeed;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        center = parentEid;
        distance = getDoubleValue(element, "orbital_distance", 0.0);

        return null;
    }

    @Override
    public void initialize(int eid) {
        Mass mass = (Mass) World.INSTANCE.getComponent(center, CompType.Mass);
        angularSpeed = getAngularSpeed(mass.mass);        
    }
    
    public double getAngularSpeed(double centerMass) {
        return 2.0 * Math.PI / getOrbitalPeriod(centerMass);
    }

    public double getOrbitalPeriod(double centerMass) {
        return getOrbitLength() / getOrbitalSpeed(centerMass);
    }
    
    public double getOrbitLength() {
        return 2.0 * Math.PI * distance;
    }
    
    public double getOrbitalSpeed(double centerMass) {
        return Math.sqrt(centerMass * Globals.G / distance);
    }
    
    public Vector3d getLocalCoordinates(double time) {
        double angle = angularSpeed * time;
        return new Vector3d(
                distance * Math.sin(angle),
                0.0,
                distance * Math.cos(angle)
        );
    }

    public Vector3d getLocalVelocity(double time, double centerMass) {
        double angle = angularSpeed * time;
        return new Vector3d(
                distance * Math.cos(angle),
                0.0,
                distance * -Math.sin(angle)
        );
    }
}
