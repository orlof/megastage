package org.megastage.components;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network.ComponentMessage;
import org.megastage.util.Globals;
import org.megastage.util.Vector3d;

public class Orbit extends BaseComponent {
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
    public void initialize(World world, int eid) {
        Mass mass = (Mass) world.getComponent(center, CompType.Mass);
        double m = mass.mass;
        angularSpeed = getAngularSpeed(m);        
    }
    
    
    @Override
    public Message replicate(int eid) {
        return new ComponentMessage(eid, this);
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

    @Override
    public String toString() {
        return "Orbit(" + center + ", " + distance + ", " + (2*Math.PI) / angularSpeed + ")";
    }
}
