package org.megastage.components.gfx;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.client.ClientGlobals;
import org.megastage.components.srv.CollisionType;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class ShipGeometry extends BaseComponent {
    public long updateTime;

    public Cube3dMap map = new Cube3dMap();
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {

        createMapFromXML(element);
        
        BaseComponent[] extraComponents = new BaseComponent[2];
        extraComponents[0] = new Mass(map.getMass());
        extraComponents[1] = new CollisionType(CollisionType.SHIP, map.getBoundingSphere());
        
        updateTime = Time.value;

        return extraComponents;
    }

    public double getInertia(Vector3d axis) {
        return map.getInertia(axis);
    }
    
    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        super.receive(pc, entity);
        ClientGlobals.spatialManager.setupShip(entity, this);
    }
    
    @Override
    public void delete(Connection pc, Entity entity) {
        ClientGlobals.spatialManager.deleteEntity(entity);
        entity.deleteFromWorld();
    }
    
    @Override
    public String toString() {
        return "ShipGeometry()";
    }

    public static void main(String[] args) throws Exception {
        Cube3dMap map = new Cube3dMap();
        map.set(1,0,0,'#');
        map.set(1,1,0,'#');
        map.set(1,2,0,'#');
        map.set(1,3,0,'#');
        map.set(1,4,0,'#');

        map.set(0,2,0,'#');
        map.set(2,2,0,'#');
        
        System.out.println("Mass: " + map.getMass());
        System.out.println("Center of mass: " + map.getCenter());
        System.out.println("Bounding sphere radius: " + map.getBoundingSphere());
        System.out.println("X-inertia: " + map.getInertia(new Vector3d(1, 0, 0)));
        System.out.println("Y-inertia: " + map.getInertia(new Vector3d(0, 1, 0)));
        System.out.println("Z-inertia: " + map.getInertia(new Vector3d(0, 0, 1)));
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
                    map.set(x, y, z, c);
                }
            }
        }
    }
}
