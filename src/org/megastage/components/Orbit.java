package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Globals;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Orbit extends BaseComponent {
    public Entity center;

    public double distance;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        center = parent;
        distance = 1000.0 * getDoubleValue(element, "orbital_distance", 0.0);
    }

    public double getAngularSpeed(double mass) {
        double period = 2.0d * Math.PI * Math.sqrt(Math.pow(distance, 3.0d) / (Globals.G * mass));
        Log.info("center mass: " +mass+ "period " + period / 3600);
        return 2.0d * Math.PI / period;        
    }
    
    public Vector getLocalCoordinates(double time, double mass) {
        double angle = getAngularSpeed(mass) * time;
        return new Vector(
                distance * Math.sin(angle),
                0.0d,
                distance * Math.cos(angle)
        );
    }
}
