package org.megastage.components.gfx;

import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.client.ClientGlobals;
import org.megastage.components.srv.CollisionType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Cube3dMap.BlockChange;
import org.megastage.util.Vector3d;

public class ShipGeometry extends GeometryComponent {
    public Cube3dMap map = new Cube3dMap();
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {

        createMapFromXML(element);
        
        BaseComponent[] extraComponents = new BaseComponent[2];
        extraComponents[0] = Mass.create(map.getMass());
        extraComponents[1] = CollisionType.create(CollisionType.SHIP, map.getCollisionRadius());
        
        return extraComponents;
    }

    public double getInertia(Vector3d axis) {
        return map.getInertia(axis);
    }

    @Override
    public boolean isDirty() {
        return map.pending != null && map.pending.size() > 0;
    }

    @Override
    public Message synchronize(int eid) {
        BlockChange change = map.pending.remove();
        return change.synchronize(eid);
    }
    
    @Override
    public void initialize(int eid) {
        map.trackChanges();
    }

    @Override
    public void receive(int eid) {
        if(World.INSTANCE.hasComponent(eid, ShipGeometry.class)) {
            ClientGlobals.spatialManager.updateShip(eid, this);
        } else {
            super.receive(eid);
            ClientGlobals.spatialManager.setupShip(eid, this);
        }
    }
    
    @Override
    public void delete(int eid) {
        ClientGlobals.spatialManager.removeFromSceneGraph(eid);
    }
    
    public void createMapFromXML(Element element) throws DataConversionException {
        int y = 0, z = 0;
        
        List<Element> mapElements = element.getChildren("map");
        for(Element elem: mapElements) {
            Attribute attr = elem.getAttribute("y");
            if(attr != null) {
                y = attr.getIntValue();
            }

            attr = elem.getAttribute("z");
            if(attr != null) {
                z = attr.getIntValue();
            }
            
            String blocks = elem.getText();
            for(int x=0; x < blocks.length(); x++) {
                char c = blocks.charAt(x);
                if(c != ' ') {
                    map.set(x, y, z, c, BlockChange.BUILD);
                }
            }
        }
    }
}
