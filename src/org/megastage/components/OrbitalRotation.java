package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Globals;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class OrbitalRotation extends BaseComponent {
    public double angularSpeed;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        double period = 3600000.0 * getDoubleValue(element, "period", 0.0);
        angularSpeed = (2.0 * Math.PI) / period;
    }

    public double getAngle() {
        double angle = (Globals.time * angularSpeed)  % (2.0 * Math.PI);
        return angle;
    }
}
