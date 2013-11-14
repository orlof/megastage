package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.client.controls.OrbitalRotationControl;
import org.megastage.components.client.ClientSpatial;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.Globals;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class OrbitalRotation extends EntityComponent {
    public double angularSpeed;
    public double period;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        period = 3600.0 * getDoubleValue(element, "period", 0.0);
        angularSpeed = (2.0 * Math.PI) / (1000.0 * period);
    }

    public double getAngle() {
        return (Globals.time * angularSpeed)  % (2.0 * Math.PI);
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.cems.setComponent(entity, this);

        ClientSpatial clientSpatial = system.cems.getComponent(entity, ClientSpatial.class);
        clientSpatial.addControl(new OrbitalRotationControl(entity));
   }


}
