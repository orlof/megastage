package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import com.cubes.Vector3Int;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Globals;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class Position extends ReplicatedComponent {
    public long x, y, z;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        if(hasValue(element, "x")) {
            x = 1000 * getLongValue(element, "x", 0);
            y = 1000 * getLongValue(element, "y", 0);
            z = 1000 * getLongValue(element, "z", 0);
        } else {
            x = 1000 * getLongValue(element, "dx", 0) + 500;
            y = 1000 * getLongValue(element, "dy", 0) + 500;
            z = 1000 * getLongValue(element, "dz", 0) + 500;
        }
        
        return null;
    }

    public void add(Vector3d vector) {
        set(x + Math.round(vector.x), 
                y + Math.round(vector.y),
                z + Math.round(vector.z));
    }

    public void move(Velocity velocity, float time) {
        add(velocity.getPositionChange(time));
    }
    
    public Vector3d getGlobalCoordinates(int eid) {
        Vector3d coord = getVector3d();
        
        BindTo bindTo = (BindTo) World.INSTANCE.getComponent(eid, CompType.BindTo);
        while(bindTo != null) {
            assert bindTo.parent > 0;
            
            ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(bindTo.parent, CompType.ShipGeometry);
            if(sg != null) {
                coord = coord.sub(sg.map.getCenterOfMass3d());

                Rotation shipRot = (Rotation) World.INSTANCE.getComponent(bindTo.parent, CompType.Rotation);
                Quaternion shipRotQ = shipRot.getQuaternion();
                coord = coord.multiply(shipRotQ);
                
                Position shipPos = (Position) World.INSTANCE.getComponent(bindTo.parent, CompType.Position);
                Vector3d shipPosVec = shipPos.getVector3d();
                coord = coord.add(shipPosVec);
                
                return coord;
            }
            
// not needed as midle components currently have no position
//            Position pos = Mapper.POSITION.get(eid);
//            if(pos != null) {
//                coord.add(pos.getVector3d());
//            }
            bindTo = (BindTo) World.INSTANCE.getComponent(bindTo.parent, CompType.BindTo);
        }
        
        return coord;
    }
    
    public Vector3d getBlockCoordinates(int eid, Vector3Int block, boolean center) {
        double offset = center ? 0.5: 0.0;
        
        Vector3d coord = new Vector3d(block.getX() + offset, block.getY() + offset, block.getZ() + offset);
        ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(eid, CompType.ShipGeometry);
        coord = coord.sub(sg.map.getCenterOfMass3d());

        Rotation shipRot = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
        Quaternion shipRotQ = shipRot.getQuaternion();
        coord = coord.multiply(shipRotQ);

        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        Vector3d shipPos = pos.getVector3d();
        coord = coord.add(shipPos);

        return coord;
    }
            
    public Vector3d getBaseCoordinates(int eid) {
        return getBlockCoordinates(eid, new Vector3Int(0,0,0), false);
    }
            
    public Vector3f getVector3f() {
        return new Vector3f(x / Globals.UNIT_F, y / Globals.UNIT_F, z / Globals.UNIT_F);
    }
    
    public Vector3d getVector3d() {
        return new Vector3d(x / Globals.UNIT_D, y / Globals.UNIT_D, z / Globals.UNIT_D);
    }
    
    public void set(Position pos) {
        set(pos.x, pos.y, pos.z);
    }

    public void set(long x, long y, long z) {
        if(this.x != x || this.y != y || this.z != z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dirty = true;
        }
    }
}
