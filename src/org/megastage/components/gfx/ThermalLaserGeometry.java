package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.Vector3d;
    
/**
 *
 * @author Orlof
 */
public class ThermalLaserGeometry extends BaseComponent {
    public float length;
    public Vector3d attackVector;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        length = getFloatValue(element, "length", 3.0f);
        attackVector = getVector3d(element, "attack_vector", new Vector3d(0,0,-1));
        return null;
    }

    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.setupThermalLaser(eid, this);
    }
    
    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
}
