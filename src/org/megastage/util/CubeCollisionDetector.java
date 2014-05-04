package org.megastage.util;

import com.cubes.Vector3Int;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.ShipStructureHit;
import org.megastage.server.Target;

public class CubeCollisionDetector {

    public static Hit hit(World world, Target target, Vector3d attackVector, float attackRange) {
        //Log.info("ATTACK " + target.toString() + " " + attackVector.toString() + " " + attackRange);
        long startTime = System.currentTimeMillis();

        Position pos = (Position) world.getComponent(target.eid, CompType.Position);
        ShipGeometry geom = (ShipGeometry) world.getComponent(target.eid, CompType.Geometry);
        Rotation rot = (Rotation) world.getComponent(target.eid, CompType.Rotation);

        // calculate coordinates for center of block
        // center of mass is not equal to center of block!
        Vector3d coord = new Vector3d(8,8,8).sub(geom.map.getCenter3d());
        coord = coord.multiply(rot.getQuaternion4d());
        coord = coord.add(target.coord);
                
        Bag<Block> candidates = new Bag<>(1);
        final Block block = new Block(coord, new Vector3Int(0, 0, 0));
        //Log.info(block.toString());
        candidates.add(block);
        
        candidates = iteration(attackVector, geom.map, candidates, 16, getCoordOffsets(16, rot.getQuaternion4d()));

        if(candidates.size == 0) {
            //Log.info(TargetManager.NO_HIT.toString());
            return new NoHit();
        }

        candidates.sort();

        Hit hit = new ShipStructureHit(
                target,
                candidates.first().base,
                candidates.first().center,
                Math.sqrt(candidates.first().distance));
        
        long endTime = System.currentTimeMillis();
        //Log.info("Collisions: " + candidates.size + ", delay: " + (endTime - startTime));
        //Log.info(hit.toString());
        
        return hit;
    }

    public static Vector3Int getCollision(Vector3d shipCenter, Quaternion shipRot, Cube3dMap map, Vector3d rayPos, Vector3d rayDir) {
        long startTime = System.currentTimeMillis();
        // calculate ship position relative to laser
        Vector3d shipLocalPos = shipCenter.sub(rayPos);

        Bag<Block> candidates = new Bag<>(1);
        candidates.add(new Block(shipLocalPos, new Vector3Int(0, 0, 0)));
        
        candidates = iteration(rayDir, map, candidates, 32, getCoordOffsets(32, shipRot));

        if(candidates.size == 0) {
            return null;
        }

        candidates.sort();
        
        long endTime = System.currentTimeMillis();
        //Log.info("Collisions: " + candidates.size + ", delay: " + (endTime - startTime));
        return candidates.first().base;
    }
    
    private static Bag<Block> iteration(Vector3d rayDir, Cube3dMap map, Bag<Block> candidates, int chunkSize, Vector3d[] coordOffset) {
        //Log.info("Chunk size: " + chunkSize + ", block count: " + candidates.size);
        // (d/2)^2 + (d/2)^2 + (d/2)^2 = 3(d/2)^2 = 3(d*d/4)
        chunkSize /= 2;

        double radius = Math.sqrt(3 * chunkSize * chunkSize);
        
        Bag<Block> next = new Bag<>(8 * candidates.size);
        
        for(Block block: candidates) {
            // TODO replace chunksize with double type chunksize 0 -> 0.5
            //            if(Math.sqrt(block.distance) - chunkSize > range) {
            //                continue;
            //            }
            
            if(block.check(rayDir, radius)) {
                for(int i=0; i < BLOCK_OFFSET.length; i++) {
                    Block ublock = block.createChild(coordOffset[i], BLOCK_OFFSET[i].mult(chunkSize)); 

                    if(chunkSize > 1 || (map.get(ublock.base) > 0 && ublock.check(rayDir, radius/2))) {
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

        // calculate unit vectors for target ship
        Vector3d xstep = new Vector3d(step, 0, 0).multiply(shipRot);
        Vector3d ystep = new Vector3d(0, step, 0).multiply(shipRot);
        Vector3d zstep = new Vector3d(0, 0, step).multiply(shipRot);

        // use unit vectors to calculate 8 sub block vectors
        return new Vector3d[] {
            xstep.add(ystep).add(zstep),
            xstep.add(ystep).sub(zstep),
            xstep.sub(ystep).add(zstep),
            xstep.sub(ystep).sub(zstep),
            Vector3d.ZERO.sub(xstep).add(ystep).add(zstep),
            Vector3d.ZERO.sub(xstep).add(ystep).sub(zstep),
            Vector3d.ZERO.sub(xstep).sub(ystep).add(zstep),
            Vector3d.ZERO.sub(xstep).sub(ystep).sub(zstep),
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

        public boolean check(Vector3d ray, double collisionRadius) {
            return ray.distanceToPoint(center) < collisionRadius;
            
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
