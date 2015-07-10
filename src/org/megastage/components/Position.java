package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import com.cubes.Vector3Int;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.util.Log;

public class Position extends BaseComponent {

    public static Vector3f getWorldCoordinates(int eid) throws ECSException {
        Position pos = (Position) World.INSTANCE.getComponentOrError(eid, CompType.Position);
        return pos.getWorldCoord(eid);
    }

    private Vector3f vector;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        vector = new Vector3f(
                getFloatValue(element, "x", 0.0f),
                getFloatValue(element, "y", 0.0f),
                getFloatValue(element, "z", 0.0f));
        
        return null;
    }
    
    public Vector3f get() {
        return vector;
    }
    
    public Vector3f getCopy() {
        return vector.clone();
    }
    
    public void set(Vector3f vec) {
        if(!vector.equals(vec)) {
            vector.set(vec);
            setDirty(true);
        }
    }
    
    public float distance(Position other) {
        return vector.distance(other.vector);
    }
            
    public float distanceSquared(Position other) {
        return vector.distanceSquared(other.vector);
    }
            
    public Vector3f getWorldCoord(int eid) throws ECSException {
        Vector3f coord = getCopy();
        
        BindTo bindTo = (BindTo) World.INSTANCE.getComponent(eid, CompType.BindTo);
        while(bindTo != null) {
            assert bindTo.parent > 0;
            
            ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(bindTo.parent, CompType.ShipGeometry);
            if(sg != null) {
                coord = coord.subtractLocal(sg.ship.getCenterOfMass());

                Rotation shipRot = (Rotation) World.INSTANCE.getComponentOrError(bindTo.parent, CompType.Rotation);
                shipRot.rotateLocal(coord);

                Position shipPos = (Position) World.INSTANCE.getComponentOrError(bindTo.parent, CompType.Position);
                coord = coord.add(shipPos.vector);

                return coord;
            }
            
            bindTo = (BindTo) World.INSTANCE.getComponent(bindTo.parent, CompType.BindTo);
        }
        
        return coord;
    }
    
    public Vector3f getBlockCoordinates(int eid, Vector3Int block) {
        Vector3f coord = new Vector3f(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f);

        ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(eid, CompType.ShipGeometry);
        coord.subtractLocal(sg.ship.getCenterOfMass());

        Rotation rot = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
        rot.rotateLocal(coord);

        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        coord.addLocal(pos.vector);

        return coord;
    }

    public void move(Vector3f displacement) {
        if(displacement.lengthSquared() > 0.0f) {
            vector.addLocal(displacement);
            setDirty(true);
        }
    }

}
