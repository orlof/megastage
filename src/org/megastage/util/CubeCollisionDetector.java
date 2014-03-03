package org.megastage.util;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.cubes.Vector3Int;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.server.TargetManager;
import org.megastage.server.TargetManager.Hit;
import org.megastage.server.TargetManager.ShipStructureHit;
import org.megastage.server.TargetManager.Target;

public class CubeCollisionDetector {

    public static TargetManager.Hit hit(Target target, Vector3d attackVector, float attackRange) {
        long startTime = System.currentTimeMillis();

        Position pos = Mapper.POSITION.get(target.entity);
        ShipGeometry geom = Mapper.SHIP_GEOMETRY.get(target.entity);
        Rotation rot = Mapper.ROTATION.get(target.entity);

        // calculate coordinates for center of block
        Vector3d coord = new Vector3d(8,8,8).sub(geom.map.getCenter3d());
        coord = coord.multiply(rot.getQuaternion4d());
        coord = coord.add(target.coord);
                
        Array<Block> candidates = new Array<>(1);
        final Block block = new Block(coord, new Vector3Int(0, 0, 0));
        //Log.info(block.toString());
        candidates.add(block);
        
        candidates = iteration(attackVector, geom.map, candidates, 16, getCoordOffsets(16, rot.getQuaternion4d()));

        if(candidates.size == 0) {
            //Log.info(TargetManager.NO_HIT.toString());
            return TargetManager.NO_HIT;
        }

        candidates.sort();

        Hit hit = new ShipStructureHit(
                target,
                candidates.first().base,
                candidates.first().center,
                Math.sqrt(candidates.first().distance));
        
        long endTime = System.currentTimeMillis();
        Log.info("Collisions: " + candidates.size + ", delay: " + (endTime - startTime));
        return hit;
    }

    public static Vector3Int getCollision(Vector3d shipCenter, Quaternion shipRot, Cube3dMap map, Vector3d rayPos, Vector3d rayDir) {
        long startTime = System.currentTimeMillis();
        // calculate ship position relative to laser
        Vector3d shipLocalPos = shipCenter.sub(rayPos);

        Array<Block> candidates = new Array<>(1);
        candidates.add(new Block(shipLocalPos, new Vector3Int(0, 0, 0)));
        
        candidates = iteration(rayDir, map, candidates, 32, getCoordOffsets(32, shipRot));

        if(candidates.size == 0) {
            return null;
        }

        candidates.sort();
        
        long endTime = System.currentTimeMillis();
        Log.info("Collisions: " + candidates.size + ", delay: " + (endTime - startTime));
        return candidates.first().base;
    }
    
    private static Array<Block> iteration(Vector3d rayDir, Cube3dMap map, Array<Block> candidates, int chunkSize, Vector3d[] coordOffset) {
        //Log.info("Chunk size: " + chunkSize + ", block count: " + candidates.size);
        // (d/2)^2 + (d/2)^2 + (d/2)^2 = 3(d/2)^2 = 3(d*d/4)
        // Following line contains the correct value
        //double radiusSquared = 3.0 * chunkSize * chunkSize / 4.0;
        // Following line contains the speed optimized value
        double radiusSquared = 2.0 * chunkSize * chunkSize / 4.0;

        chunkSize /= 2;
        
        Array<Block> next = new Array<>(8 * candidates.size);
        
        for(Block block: candidates) {
            // TODO replace chunksize with double type chunksize 0 -> 0.5
            //            if(Math.sqrt(block.distance) - chunkSize > range) {
            //                continue;
            //            }
            
            if(block.check(rayDir, radiusSquared)) {
                for(int i=0; i < BLOCK_OFFSET.length; i++) {
                    Block ublock = block.createChild(coordOffset[i], BLOCK_OFFSET[i].mult(chunkSize)); 

                    if(chunkSize == 1) {
//                        Log.info(ublock.base.toString() + " " + ublock.center.toString() + " " + ublock.distance);
//                        Log.info("" + (int) map.get(ublock.base.getX(), ublock.base.getY(), ublock.base.getZ()));
                    }
                    if(chunkSize > 1 || map.get(ublock.base.getX(), ublock.base.getY(), ublock.base.getZ()) > 0) {
                        next.add(ublock);
                    }
                }
            }
        }
        
        if(chunkSize == 1 || next.size == 0) return next;
        
        return iteration(rayDir, map, next, chunkSize, divBy2Local(coordOffset));
    }

    private static final Vector3Int[] BLOCK_OFFSET = new Vector3Int[] {
        new Vector3Int(1,1,1),
        new Vector3Int(1,1,0),
        new Vector3Int(1,0,1),
        new Vector3Int(1,0,0),
        new Vector3Int(0,1,1),
        new Vector3Int(0,1,0),
        new Vector3Int(0,0,1),
        new Vector3Int(0,0,0),
    };
    
    private static Vector3d[] getCoordOffsets(int chunkSize, Quaternion shipRot) {
        double step = chunkSize / 4.0;

        Vector3d xstep = new Vector3d(step, 0, 0).multiply(shipRot);
        Vector3d ystep = new Vector3d(0, step, 0).multiply(shipRot);
        Vector3d zstep = new Vector3d(0, 0, step).multiply(shipRot);

        return new Vector3d[] {
            xstep.add(ystep).add(zstep),
            xstep.add(ystep).sub(zstep),
            xstep.sub(ystep).add(zstep),
            xstep.sub(ystep).sub(zstep),
            xstep.negate().add(ystep).add(zstep),
            xstep.negate().add(ystep).sub(zstep),
            xstep.negate().sub(ystep).add(zstep),
            xstep.negate().sub(ystep).sub(zstep),
        };
    }

    private static Vector3d[] divBy2Local(Vector3d[] vecs) {
        for(int i=0; i < vecs.length; i++) {
            vecs[i] = vecs[i].divide(2.0);
        }
        return vecs;
    }

    public static class Block implements Comparable<Block> {
        Vector3d center;
        Vector3Int base;
        double distance;
        
        public Block(Vector3d center, Vector3Int base) {
            this.center = center;
            this.base = base;
            this.distance = center.lengthSquared();
        }
        
        public Block createChild(Vector3d coordOffset, Vector3Int blockOffset) {
            return new Block(
                    center.add(coordOffset),
                    base.add(blockOffset));            
        }

        public boolean check(Vector3d ray, double collisionRadiusSquared) {
            return ray.distanceToPointSquared(center) < collisionRadiusSquared;
            
        }

        @Override
        public int compareTo(Block o) {
            return Double.compare(distance, o.distance);
        }

        @Override
        public String toString() {
            return "Block{" + "center=" + center + ", base=" + base + ", distance=" + distance + '}';
        }
        
        
    }
}
