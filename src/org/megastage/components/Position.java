package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.protocol.Network;
import org.megastage.util.Globals;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class Position extends BaseComponent {
    public long x, y, z;
    public transient boolean dirty = true;
    
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
    public boolean synchronize() {
        return dirty;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        dirty = false;
        return super.create(entity);
    }
    
    @Override
    public boolean replicate() {
        return true;
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
