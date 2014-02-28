package org.megastage.util;

import com.badlogic.gdx.utils.Array;
import com.cubes.Vector3Int;
import com.esotericsoftware.minlog.Log;

public class CubeCollisionDetector {
    private final Vector3d rayPos;
    private final Vector3d rayDir;

    public CubeCollisionDetector(Vector3d rayPos, Vector3d rayDir) {
        this.rayPos = rayPos;
        this.rayDir = rayDir;
    }

    public Vector3Int collision(Vector3d shipCenter, Quaternion shipRot, Cube3dMap map) {
        long startTime = System.currentTimeMillis();
        // calculate ship position relative to laser
        Vector3d shipLocalPos = shipCenter.sub(rayPos);

        Array<Block> candidates = new Array<>(1);
        candidates.add(new Block(shipLocalPos, new Vector3Int(0, 0, 0)));
        
        candidates=process(map, candidates, 32, getCoordOffsets(32, shipRot));

        if(candidates.size == 0) {
            return null;
        }

        candidates.sort();
        
        long endTime = System.currentTimeMillis();
        Log.info("Collisions: " + candidates.size + ", delay: " + (endTime - startTime));
        return candidates.first().base;
    }
    
    
    public Array<Block> process(Cube3dMap map, Array<Block> candidates, int chunkSize, Vector3d[] coordOffset) {
        Log.info("Chunk size: " + chunkSize + ", block count: " + candidates.size);
        // (d/2)^2 + (d/2)^2 + (d/2)^2 = 3(d/2)^2 = 3(d*d/4)
        double radiusSquared = 3.0 * chunkSize * chunkSize / 4.0;

        chunkSize /= 2;
        
        Array<Block> next = new Array<>(8 * candidates.size);
        
        for(Block block: candidates) {
            if(block.check(rayDir, radiusSquared)) {
                for(int i=0; i < BLOCK_OFFSET.length; i++) {
                    if(chunkSize > 0 || map.get(block.base.getX(), block.base.getY(), block.base.getZ()) > 0) {
                        next.add(block.createChild(coordOffset[i], BLOCK_OFFSET[i].mult(chunkSize)));
                    }
                }
            }
        }
        
        if(chunkSize == 0 || next.size == 0) return next;
        
        return process(map, next, chunkSize, divBy2Local(coordOffset));
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
    
    private Vector3d[] getCoordOffsets(int chunkSize, Quaternion shipRot) {
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

    private Vector3d[] divBy2Local(Vector3d[] vecs) {
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
    }
}
