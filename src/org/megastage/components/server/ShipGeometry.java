/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import java.util.List;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.EntityComponent;
import org.megastage.components.Mass;
import org.megastage.client.ClientGlobals;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector;


    
/**
 *
 * @author Orlof
 */
public class ShipGeometry extends EntityComponent {
    public float xCenter, yCenter, zCenter;
    public long updateTime;
    
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
                    if(xString.charAt(x) == '#') {
                        set(x, index, z);
                        xCenter += x; yCenter += index; zCenter += z; blockCount++;
                    }
                }
            }
        }

        // calculate center of mass (for rotation)
        xCenter /= blockCount; yCenter /= blockCount; zCenter /= blockCount;
        xCenter += 0.5; yCenter += 0.5; zCenter += 0.5;

        BaseComponent[] adds = new BaseComponent[1];

        Mass mass = new Mass();
        mass.mass = 1000 * blockCount;
        adds[0] = mass;
        
        updateTime = ServerGlobals.time;

        return adds;
    }

    public double getInertia(Vector axis) {
        double xc = xCenter - 0.5;
        double yc = yCenter - 0.5;
        double zc = zCenter - 0.5;
        
        double inertia = 0;
        for(int x=0; x < maxx; x++) {
            for(int y=0; y < maxy; y++) {
                for(int z=0; z < maxz; z++) {
                    if(data[x][y][z]) {
                        Vector point = new Vector(x - xc, y - yc, z - zc);
                        inertia += 1000.0 * axis.distance(point);
                    }
                }
            }
        }
        
        return inertia;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        entity.addComponent(this);
        ClientGlobals.spatialManager.setupShip(entity, this);
    }
    
    @Override
    public String toString() {
        return "ShipGeometry()";
    }

    public int maxx = -1, maxy = -1, maxz = -1;
    public boolean[][][] data = new boolean[16][16][16];
    
    public void set(int x, int y, int z) {
        if(x > maxx) maxx = x;
        if(y > maxy) maxy = y;
        if(z > maxz) maxz = z;
        data[x][y][z] = true;
    }

    public int getChunkSize() {
        int size = maxx;
        if(size < maxy) size = maxy;
        if(size < maxz) size = maxz;
        
        return size / 16 + 1;
    }
}
