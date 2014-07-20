package org.megastage.util;

import com.cubes.Vector3Int;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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

    public static Hit hit(World world, Target target, Vector3f attackVector, float attackRange) {
        //Log.info("ATTACK " + target.toString() + " " + attackVector.toString() + " " + attackRange);
        long startTime = System.currentTimeMillis();

        Position pos = (Position) world.getComponent(target.eid, CompType.Position);
        ShipGeometry geom = (ShipGeometry) world.getComponent(target.eid, CompType.GeometryComponent);
        Rotation rot = (Rotation) world.getComponent(target.eid, CompType.Rotation);

        // calculate coordinates for center of block
        // center of mass is not equal to center of block!
        Vector3f coord = new Vector3f(8,8,8).subtractLocal(geom.map.getCenterOfMass());
        rot.rotateLocal(coord).addLocal(target.coord);
                
        Bag<Block> candidates = new Bag<>(1);
        final Block block = new Block(coord, new Vector3Int(0, 0, 0));
        //Log.info(block.toString());
        candidates.add(block);
        
        candidates = iteration(attackVector, geom.map, candidates, 16, getCoordOffsets(16, rot.get()));

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

    public static Vector3Int getCollision(Vector3f shipCenter, Quaternion shipRot, Cube3dMap map, Vector3f rayPos, Vector3f rayDir) {
        long startTime = System.currentTimeMillis();
        // calculate ship position relative to laser
        Vector3f shipLocalPos = shipCenter.subtract(rayPos);

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
    
    private static Bag<Block> iteration(Vector3f rayDir, Cube3dMap map, Bag<Block> candidates, int chunkSize, Vector3f[] coordOffset) {
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
    
    private static Vector3f[] getCoordOffsets(int chunkSize, Quaternion shipRot) {
        float step = chunkSize / 4.0f;

        // calculate unit vectors for target ship
        Vector3f xstep = shipRot.multLocal(new Vector3f(step, 0, 0));
        Vector3f ystep = shipRot.multLocal(new Vector3f(0, step, 0));
        Vector3f zstep = shipRot.multLocal(new Vector3f(0, 0, step));

        // use unit vectors to calculate 8 sub block vectors
        return new Vector3f[] {
            new Vector3f().addLocal(xstep).addLocal(ystep).addLocal(zstep),
            new Vector3f().addLocal(xstep).addLocal(ystep).subtractLocal(zstep),
            new Vector3f().addLocal(xstep).subtractLocal(ystep).addLocal(zstep),
            new Vector3f().addLocal(xstep).subtractLocal(ystep).subtractLocal(zstep),
            new Vector3f().subtractLocal(xstep).addLocal(ystep).addLocal(zstep),
            new Vector3f().subtractLocal(xstep).addLocal(ystep).subtractLocal(zstep),
            new Vector3f().subtractLocal(xstep).subtractLocal(ystep).addLocal(zstep),
            new Vector3f().subtractLocal(xstep).subtractLocal(ystep).subtractLocal(zstep),
        };
    }

    private static Vector3f[] divBy2Local(Vector3f[] vecs) {
        for(int i=0; i < vecs.length; i++) {
            vecs[i].divideLocal(2.0f);
        }
        return vecs;
    }

    public static class Block implements Comparable<Block> {
        Vector3f center;
        Vector3Int base;
        double distance;
        
        public Block(Vector3f center, Vector3Int base) {
            this.center = center;
            this.base = base;
            this.distance = center.lengthSquared();
        }
        
        public Block createChild(Vector3f coordOffset, Vector3Int blockOffset) {
            return new Block(
                    center.add(coordOffset),
                    base.add(blockOffset));            
        }

        public boolean check(Vector3f ray, double collisionRadius) {
            return MathUtil.distancePointToLine(center, ray) < collisionRadius;
            
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
