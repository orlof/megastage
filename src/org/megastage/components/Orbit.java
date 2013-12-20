package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.ClientGlobals;
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
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        center = parent.getId();
        distance = 1000.0 * getDoubleValue(element, "orbital_distance", 0.0);
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        center = ClientGlobals.artemis.toClientEntity(center).getId();
        entity.addComponent(this);
    }

    public double getOrbitalPeriod(double centerMass) {
        return (2.0 * Math.PI) / getAngularSpeed(centerMass);
    }
    
    public double getAngularSpeed(double centerMass) {
        return 1.0 / Math.sqrt(Math.pow(distance, 3.0) / (Globals.ORBIT_G * centerMass));
    }
    
    public Vector getLocalCoordinates(double time, double mass) {
        double angle = getAngularSpeed(mass) * time;
        return new Vector(
                distance * Math.sin(angle),
                0.0d,
                distance * Math.cos(angle)
        );
    }

    public String toString() {
        return "Orbit(" + center + ", " + distance + ")";
    }
}
