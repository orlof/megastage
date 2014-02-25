package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.server.ShipManager;
import org.megastage.server.ShipManager.Target;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class ThermalLaserSystem extends SystemTemplate {
    public ThermalLaserSystem() {
        super(Aspect.getAspectForAll(VirtualThermalLaser.class));
    }

    @Override
    protected void process(Entity e) {
        VirtualThermalLaser vtl = Mapper.VIRTUAL_THERMAL_LASER.get(e);
        switch(vtl.status) {
            case VirtualThermalLaser.STATUS_DORMANT:
                break;
            case VirtualThermalLaser.STATUS_FIRING:
                if(Time.value < vtl.startTime + vtl.duration) {
                    // firing
                    Array<Target> collisions = findCollisions(e, vtl);
                    if(collisions.size > 0) {
                        Target target = collisions.get(0);
                        VirtualForceField forceField = Mapper.VIRTUAL_FORCE_FIELD.get(target.entity);
                        forceField.damage(world.getDelta() * vtl.wattage);
                        //vtl.setRange((float) target.distance);
                    } else {
                        //vtl.setRange(vtl.maxRange);
                    }
                } else {
                    // turn off
                    vtl.startTime = Time.value;
                    vtl.duration = vtl.duration * vtl.wattage / 20;
                    vtl.status = VirtualThermalLaser.STATUS_COOLDOWN;
                    vtl.dirty = true;
                }
                break;
            case VirtualThermalLaser.STATUS_COOLDOWN:
                if(Time.value >= vtl.startTime + vtl.duration) {
                    vtl.status = VirtualThermalLaser.STATUS_DORMANT;
                    vtl.dirty = true;
                }
                break;
        }
    }

    public static final Array<Target> EMPTY_SHIP_ARRAY = new Array<>(0);
    public static final Vector3d FORWARD_VECTOR = new Vector3d(0,0,-1);
    
    Array<Target> findCollisions(Entity e, VirtualThermalLaser vtl) {
        Position myPos = Mapper.POSITION.get(e);
        if(myPos == null) return EMPTY_SHIP_ARRAY;

        Vector3d coord = myPos.getLocalVector3d(e);
        if(coord == null) return EMPTY_SHIP_ARRAY;
        // Log.info(ID.get(e) + coord.toString());

        Array<Target> targets = ShipManager.getTargetsInRange(vtl.ship, coord, 100);
        if(targets.size == 0) {
            return targets;
        }

        // Log.info(ID.get(e) + ships.toString());

        Quaternion shipAngle = Mapper.ROTATION.get(vtl.ship).getQuaternion4d();
        Vector3d attackVector = FORWARD_VECTOR.multiply(shipAngle);
        //Vector3d attackVector = shipVector.multiply(weaponAngle);

        targets = ShipManager.findCollision(targets, attackVector);

        return targets;
    }
}
