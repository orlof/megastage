/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.cubes.Vector3Int;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import java.util.LinkedList;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.protocol.Message;


/**
 *
 * @author Orlof
 */
public class Cube3dMap {
    private final static transient int INITIAL_CAPACITY = 16;

    public char[][][] data;
    public int xsize, ysize, zsize;
    public int xtotal, ytotal, ztotal;
    public int count;
    public int version = 0;
    
    public transient LinkedList<BlockChange> pending;
    
    public void trackChanges() {
        pending = new LinkedList<>();
    }

    public char get(int x, int y, int z) {
        if(data == null || x < 0 || data.length <= x) {
            return 0;
        }
        char[][] xdata = data[x];
        if(xdata == null || y < 0 || xdata.length <= y) {
            return 0;
        }
        char[] ydata = data[x][y];
        if(ydata == null || z < 0 || ydata.length <= z) {
            return 0;
        }
        return ydata[z];
    }

    public void set(int x, int y, int z, char value) {
        char old = get(x, y, z);
        
        if(value == old) {
            return;
        }
        
        if(value != 0) {
            if(x > xsize) xsize = x;
            if(y > ysize) ysize = y;
            if(z > zsize) zsize = z;

            xtotal += x;
            ytotal += y;
            ztotal += z;

            count++;
        } else {
            xtotal -= x;
            ytotal -= y;
            ztotal -= z;
            
            count--;
        }
        
        if(data == null || data.length <= x) {
            char[][][] newdata = new char[getNewCapacity(data, x)][][];
            if(data != null) {
                System.arraycopy(data, 0, newdata, 0, data.length);
            }
            data = newdata;
        }

        if(data[x] == null || data[x].length <= y) {
            char[][] newdata = new char[getNewCapacity(data[x], y)][];
            if(data[x] != null) {
                System.arraycopy(data[x], 0, newdata, 0, data[x].length);
            }
            data[x] = newdata;            
        }

        if(data[x][y] == null || data[x][y].length <= z) {
            char[] newdata = new char[getNewCapacity(data[x][y], z)];
            if(data[x][y] != null) {
                System.arraycopy(data[x][y], 0, newdata, 0, data[x][y].length);
            }
            data[x][y] = newdata;            
        }

        data[x][y][z] = value;
        if(pending != null) pending.add(new BlockChange(x, y, z, value));
        version++;
    }

    public Vector3f getCenter() {
        return new Vector3f(
                getCenter(xtotal),
                getCenter(ytotal),
                getCenter(ztotal));
    }
    
    private float getCenter(float total) {
        return total / count + 0.5f;
    }

    public Vector3d getCenter3d() {
        return new Vector3d(
                getCenter3d(xtotal),
                getCenter3d(ytotal),
                getCenter3d(ztotal));
    }
    
    private double getCenter3d(double total) {
        return total / count + 0.5f;
    }

    private int getNewCapacity(char[][][] arr, int index) {
        if(arr == null) return calcNewCapacity(INITIAL_CAPACITY, index);
        return calcNewCapacity(arr.length, index);
    }

    private int getNewCapacity(char[][] arr, int index) {
        if(arr == null) return calcNewCapacity(INITIAL_CAPACITY, index);
        return calcNewCapacity(arr.length, index);
    }

    private int getNewCapacity(char[] arr, int index) {
        if(arr == null) return calcNewCapacity(INITIAL_CAPACITY, index);
        return calcNewCapacity(arr.length, index);
    }

    private int calcNewCapacity(int capacity, int index) {
        while(capacity <= index) {
            capacity *= 2;
        }

        return capacity;
    }

    public double getCollisionRadius() {
        return getCenter().length();
    }

    public double getMass() {
        return 1000.0 * count;
    }

    public double getInertia(Vector3d axis) {
        // I = mr2
        double xc = getCenter(xtotal);
        double yc = getCenter(ytotal);
        double zc = getCenter(ztotal);
        
        double inertia = 0;
        for(int x=0; x <= xsize; x++) {
            for(int y=0; y <= ysize; y++) {
                for(int z=0; z <= zsize; z++) {
                    if(get(x, y, z) == '#') {
                        // mass = 1000kg
                        
                        Vector3d point = new Vector3d(x - xc, y - yc, z - zc);
                        double distance = axis.distanceToPoint(point);

                        inertia += 1000.0 * distance * distance;
                    }
                }
            }
        }
        
        return inertia;
    }
    
    public static class BlockChange extends BaseComponent {
        public int x, y, z;
        public char type;

        public BlockChange() {}
        
        private BlockChange(int x, int y, int z, char value) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = value;
        }

        @Override
        public void receive(Connection pc, Entity entity) {
            Log.info(ID.get(entity) + toString());
            ClientGlobals.spatialManager.updateShipBlock(entity, this);
        }
        
        public String toString() {
            return "BlockChange[" + x + ", " + y + ", " + z + "] = " + (int) type;
        }
    }
}

