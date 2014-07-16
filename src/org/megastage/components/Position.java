package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import com.cubes.Vector3Int;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Globals;

public class Position extends ReplicatedComponent {
    public Vector3f coords;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        coords = new Vector3f(
                getFloatValue(element, "x", 0f),
                getFloatValue(element, "y", 0f),
                getFloatValue(element, "z", 0f));
        
        return null;
    }

    public Vector3f getGlobalCoordinates(int eid) {
        Vector3f coord = new Vector3f(coords);
        
        BindTo bindTo = (BindTo) World.INSTANCE.getComponent(eid, CompType.BindTo);
        while(bindTo != null) {
            assert bindTo.parent > 0;
            
            ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(bindTo.parent, CompType.ShipGeometry);
            if(sg != null) {
                coord = coord.subtractLocal(sg.map.getCenterOfMass());

                Rotation shipRot = (Rotation) World.INSTANCE.getComponent(bindTo.parent, CompType.Rotation);
                shipRot.value.multLocal(coord);

                Position shipPos = (Position) World.INSTANCE.getComponent(bindTo.parent, CompType.Position);
                coord = coord.add(shipPos.coords);

                return coord;
            }
            
            bindTo = (BindTo) World.INSTANCE.getComponent(bindTo.parent, CompType.BindTo);
        }
        
        return coord;
    }
    
    public Vector3f getBlockCoordinates(int eid, Vector3Int block) {
        Vector3f coord = new Vector3f(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f);

        ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(eid, CompType.ShipGeometry);
        coord.subtractLocal(sg.map.getCenterOfMass());

        Rotation rot = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
        rot.value.multLocal(coord);

        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        coord.addLocal(pos.coords);

        return coord;
    }
            
}
