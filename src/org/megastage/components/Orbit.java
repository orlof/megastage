package org.megastage.components;

import com.jme3.math.Vector3f;
import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Globals;

public class Orbit extends ReplicatedComponent {
    public int center;
    public float distance;

    public float angularSpeed;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        center = parentEid;
        distance = getFloatValue(element, "orbital_distance", 0.0f);

        return null;
    }

    @Override
    public void initialize(int eid) {
        Mass mass = (Mass) World.INSTANCE.getComponent(center, CompType.Mass);
        angularSpeed = getAngularSpeed(mass.value);        
    }
    
    public float getAngularSpeed(float centerMass) {
        return (float) (2.0 * Math.PI / getOrbitalPeriod(centerMass));
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
    
    public Vector3f getLocalCoordinates(float time) {
        float angle = angularSpeed * time;
        return new Vector3f(
                distance * (float) Math.sin(angle),
                0.0f,
                distance * (float) Math.cos(angle)
        );
    }

    public Vector3f getLocalVelocity(float time, float centerMass) {
        float angle = angularSpeed * time;
        return new Vector3f(
                distance * (float) Math.cos(angle),
                0.0f,
                distance * (float) -Math.sin(angle)
        );
    }
}
