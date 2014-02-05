/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.gfx;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import java.util.List;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.client.ClientGlobals;
import org.megastage.components.srv.CollisionType;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

/**
 *
 * @author Orlof
 */
public class ShipGeometry extends BaseComponent {
    public float xCenter, yCenter, zCenter;
    public long updateTime;

    public Cube3dMap map = new Cube3dMap();
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        List<Element> yList = element.getChildren("y");
        int index = yList.size();

        float blockCount = 0;
        
        for(Element yElem: yList) {
            index--;

            List<Element> zList = yElem.getChildren("z");
            for(int z=0; z < zList.size(); z++) {
                Element zElem = zList.get(z);
                
                String xString = zElem.getText();
                for(int x=0; x < xString.length(); x++) {
                    if(xString.charAt(x) != ' ') {
                        map.set(x, index, z, xString.charAt(x));
                        xCenter += x; yCenter += index; zCenter += z; blockCount++;
                    }
                }
            }
        }

        // calculate center of mass (for rotation)
        xCenter /= blockCount; yCenter /= blockCount; zCenter /= blockCount;
        xCenter += 0.5; yCenter += 0.5; zCenter += 0.5;

        BaseComponent[] adds = new BaseComponent[2];

        double sphere = Math.sqrt(xCenter * xCenter + yCenter * yCenter + zCenter * zCenter);
        CollisionType ct = new CollisionType();
        ct.item = CollisionType.SHIP;
        ct.radius = sphere;
        adds[1] = ct;
        
        Mass mass = new Mass();
        mass.mass = 1000 * blockCount;
        adds[0] = mass;
        
        updateTime = Time.value;

        return adds;
    }

    public double getInertia(Vector3d axis) {
        double xc = xCenter - 0.5;
        double yc = yCenter - 0.5;
        double zc = zCenter - 0.5;
        
        double inertia = 0;
        for(int x=0; x < map.xsize; x++) {
            for(int y=0; y < map.ysize; y++) {
                for(int z=0; z < map.zsize; z++) {
                    if(map.get(x, y, z) == '#') {
                        Vector3d point = new Vector3d(x - xc, y - yc, z - zc);
                        inertia += 1000.0 * axis.distanceToPoint(point);
                    }
                }
            }
        }
        
        return inertia;
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

}
