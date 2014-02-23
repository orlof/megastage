package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.server.ShipManager;
import org.megastage.server.ShipManager.Ship;
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
                    Array<Ship> collisions = findCollisions(e, vtl);
                    if(collisions.size > 0) {
                        Ship target = collisions.get(0);
                        double range = Math.sqrt(target.distanceSquared) - target.collisionRadius;
                        vtl.setRange((float) range);
                    } else {
                        vtl.setRange(vtl.maxRange);
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

    Array<Ship> findCollisions(Entity e, VirtualThermalLaser vtl) {
        try {
            Vector3d wCoord = Mapper.POSITION.get(e).getVector3d();
            Vector3d cCoord = Mapper.SHIP_GEOMETRY.get(vtl.ship).map.getCenter3d();
            Vector3d sCoord = Mapper.POSITION.get(vtl.ship).getVector3d();
        
            Vector3d coord = wCoord.sub(cCoord).add(sCoord);

            Array<Ship> ships = ShipManager.getShipsInRange(vtl.ship, coord, 100);
            if(ships.size == 0) {
                return ships;
            }

            Quaternion shipAngle = Mapper.ROTATION.get(vtl.ship).getQuaternion4d();
            Vector3d attackVector = new Vector3d(0,0,-1).multiply(shipAngle);
            //Vector3d attackVector = shipVector.multiply(weaponAngle);

            ships = ShipManager.findCollision(ships, attackVector);

            return ships;
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return new Array<>(0);
    }
}
