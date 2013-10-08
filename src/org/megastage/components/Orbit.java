package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
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
    public double angularSpeed;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        center = parent;

        distance = 1000.0d * element.getAttribute("orbital_distance_km").getDoubleValue();

        if(element.getAttribute("mass_kg") != null) {
            double mass = element.getAttribute("mass_kg").getDoubleValue();
            double period = 2.0d * Math.PI * Math.sqrt(Math.pow(distance, 3.0d) / (Globals.G * mass));
            angularSpeed = 2.0d * Math.PI / period;
        }
    }

    public Vector getLocalCoordinates(double time) {
        double angle = angularSpeed * time;
        return new Vector(
                distance * Math.sin(angle),
                distance * Math.cos(angle),
                0.0d
        );
    }
    
}
