package org.megastage.util;

import com.cubes.Block;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.esotericsoftware.minlog.Log;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import java.util.LinkedList;
import org.megastage.client.CubesManager;
import org.megastage.client.EntityNode;
import org.megastage.client.SoundManager;
import org.megastage.client.SpatialManager;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.ToStringComponent;
import org.megastage.ecs.World;

public class Cube3dMap extends ToStringComponent {
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

    public char get(Vector3Int block) {
        return get(block.getX(), block.getY(), block.getZ());
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

    public void set(int x, int y, int z, char value, char event) {
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
        if(pending != null) pending.add(new BlockChange(x, y, z, value, event));
        version++;
    }

    public Vector3f getCenterOfMass() {
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
        return getCenterOfMass().length();
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
    
    public static class BlockChange extends ReplicatedComponent {
        public static final transient char UNBUILD = 0;
        public static final transient char BREAK = 1;
        public static final transient char BUILD = 2;
        
        public int x, y, z;
        public char type;
        public char event;

        public BlockChange() {}
        
        private BlockChange(int x, int y, int z, char value, char event) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = value;
            this.event = event;
        }

        @Override
        public void receive(int eid) {
            Log.info(ID.get(eid) + toString());

            ShipGeometry sg = (ShipGeometry) World.INSTANCE.getComponent(eid, CompType.ShipGeometry);
            Cube3dMap theMap = sg.map;
            theMap.set(x, y, z, type, event);

            EntityNode node = SpatialManager.getOrCreateNode(eid);
            BlockTerrainControl ctrl = node.offset.getControl(BlockTerrainControl.class);

            if(type == 0) {
                ctrl.removeBlock(x, y, z);
                if(event == BlockChange.BREAK) {
                    ParticleEmitter pe = (ParticleEmitter) node.getChild("BlockSparks");

                    SoundManager.get(SoundManager.EXPLOSION_3).playInstance();

                    pe.killAllParticles();
                    pe.setLocalTranslation(x, y, z);
                    pe.emitAllParticles();
                    //pe.addControl(new DeleteControl(3000));
                }
            } else {
                Class<? extends Block> block = CubesManager.getBlock(type);
                ctrl.setBlock(x, y, z, block);
            }
        }
    }
}
