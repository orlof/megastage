package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.util.Globals;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Orbit extends EntityComponent {
    public int center;
    public double distance;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        center = parent.getId();
        distance = getDoubleValue(element, "orbital_distance", 0.0);
        
        return null;
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        center = ClientGlobals.artemis.toClientEntity(center).getId();
        entity.addComponent(this);
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
    
    public Vector getLocalCoordinates(double time, double centerMass) {
        double angle = getAngularSpeed(centerMass) * time;
        return new Vector(
                1000.0 * distance * Math.sin(angle),
                0.0,
                1000.0 * distance * Math.cos(angle)
        );
    }

    public String toString() {
        return "Orbit(" + center + ", " + distance + ")";
    }
}
