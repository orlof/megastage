package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.protocol.Message;
import org.megastage.util.Globals;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector3d;

public class Position extends BaseComponent {
    public long x, y, z;
    
    public Position() {
        super();
    }

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
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

    @Override
    public Message replicate(Entity entity) {
        return always(entity);
    }

    @Override
    public Message synchronize(Entity entity) {
        return ifDirty(entity);
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        Position pos = Mapper.POSITION.get(entity);
        if(pos == null) {
            super.receive(pc, entity);
            return;
        }
        pos.set(this);
        pos.dirty = true;
    }
    
    public void add(Vector3d vector) {
        set(x + Math.round(vector.x), 
                y + Math.round(vector.y),
                z + Math.round(vector.z));
    }

    public void move(Velocity velocity, float time) {
        add(velocity.getPositionChange(time));
    }
    
    public Vector3d getGlobalCoordinates(Entity entity) {
        Vector3d coord = getVector3d();
        
        BindTo bindTo = Mapper.BIND_TO.get(entity);
        while(bindTo != null) {
            entity = ServerGlobals.world.getEntity(bindTo.parent);
            if(entity == null) return null;

            ShipGeometry sg = Mapper.SHIP_GEOMETRY.get(entity);
            if(sg != null) {
                coord = coord.sub(sg.map.getCenter3d());
                
                Quaternion shipRot = Mapper.ROTATION.get(entity).getQuaternion4d();
                coord = coord.multiply(shipRot);
                
                Vector3d shipPos = Mapper.POSITION.get(entity).getVector3d();
                coord = coord.add(shipPos);
                
                return coord;
            }
            
// not needed as midle components currently have no position
//            Position pos = Mapper.POSITION.get(entity);
//            if(pos != null) {
//                coord.add(pos.getVector3d());
//            }
            bindTo = Mapper.BIND_TO.get(entity);
        }
        
        
        return coord;
    }
    
    public Vector3d getGlobalVector3dxxx(Entity entity) {
        long x = this.x, y=this.y, z=this.z;
        
        BindTo bindTo = Mapper.BIND_TO.get(entity);
        while(bindTo != null) {
            entity = ServerGlobals.world.getEntity(bindTo.parent);
            if(entity == null) return null;
            
            Position pos = Mapper.POSITION.get(entity);
            if(pos != null) {
                x+=pos.x; y+=pos.y; z+=pos.z;
            }
            bindTo = Mapper.BIND_TO.get(entity);
        }
        
        ShipGeometry sg = Mapper.SHIP_GEOMETRY.get(entity);
        if(sg != null) {
            Vector3d center = sg.map.getCenter3d();
            return new Vector3d(x / Globals.UNIT_D - center.x, y / Globals.UNIT_D - center.y, z / Globals.UNIT_D - center.z);
        }
        
        return new Vector3d(x / Globals.UNIT_D, y / Globals.UNIT_D, z / Globals.UNIT_D);
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

    @Override
    public String toString() {
        return "Position(" + x + ", " + y + ", " + z + ", " + dirty + ")";
    }

}
