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
import org.megastage.components.EntityComponent;
import org.megastage.util.ClientGlobals;


    
/**
 *
 * @author Orlof
 */
public class ShipGeometry extends EntityComponent {
    public float xCenter, yCenter, zCenter;
    
    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        List<Element> yList = element.getChildren("y");
        int y = yList.size();

        float blockCount = 0;
        
        for(Element yElem: yList) {
            y--;

            List<Element> zList = yElem.getChildren("z");
            for(int z=0; z < zList.size(); z++) {
                Element zElem = zList.get(z);
                
                String xString = zElem.getText();
                for(int x=0; x < xString.length(); x++) {
                    if(xString.charAt(x) == '#') {
                        set(x, y, z);
                        xCenter += x; yCenter += y; zCenter += z; blockCount++;
                    }
                }
            }
        }

        // calculate center of mass (for rotation)
        xCenter /= blockCount; yCenter /= blockCount; zCenter /= blockCount;
        xCenter += 0.5; yCenter += 0.5; zCenter += 0.5;
        
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
