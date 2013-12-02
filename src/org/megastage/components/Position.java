package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.client.controls.PositionControl;
import org.megastage.components.client.ClientSpatial;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Globals;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Position extends EntityComponent {
    public long x, y, z;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        x = 1000 * getLongValue(element, "x", 0);
        y = 1000 * getLongValue(element, "y", 0);
        z = 1000 * getLongValue(element, "z", 0);
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.cems.setComponent(entity, this);
    }

    public void add(Vector vector) {
        x += Math.round(vector.x);
        y += Math.round(vector.y);
        z += Math.round(vector.z);
    }

    public void move(Velocity velocity, float time) {
        add(velocity.getPositionChange(time));
    }
    
    public Vector3f getAsVector() {
        return new Vector3f((float) (x / ClientGlobals.scale), (float) (y / ClientGlobals.scale), (float) (z / ClientGlobals.scale));
    }
    
    public String toString() {
        return "Position(" + x + ", " + y + ", " + z + ")";
    }
}
