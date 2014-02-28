package org.megastage.util;

import com.badlogic.gdx.utils.Array;

public class CubeFinder {
    public static final int CHUNK_SIZE = 8;
    
    // this is optimized with cross
    public static void find(Vector3d mapCenter, Quaternion mapRot, Cube3dMap map, Vector3d rayPos, Vector3d rayDir) {
        Vector3d chunkSizes = new Vector3d(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
        Vector3d halfChunkSizes = chunkSizes.divide(2);
        double radiusSquared = halfChunkSizes.lengthSquared();

        Vector3d mapOrigin = map.getCenter3d().negate().multiply(mapRot).add(mapCenter).sub(rayPos);

        Vector3d xunit = Vector3d.UNIT_X.multiply(mapRot);
        Vector3d yunit = Vector3d.UNIT_Y.multiply(mapRot);
        Vector3d zunit = Vector3d.UNIT_Z.multiply(mapRot);
        
        Vector3d xunit2 = xunit.multiply(2);
        Vector3d yunit2 = yunit.multiply(2);
        Vector3d zunit2 = zunit.multiply(2);

        Array<Vector3d> next = new Array<>();
        for(int x=0; x < map.xsize; x += CHUNK_SIZE) {
            for(int y=0; y < map.ysize; y += CHUNK_SIZE) {
                for(int z=0; z < map.zsize; z += CHUNK_SIZE) {
                    Vector3d dx = xunit.multiply(x + halfChunkSizes.x);
                    Vector3d dy = yunit.multiply(y + halfChunkSizes.y);
                    Vector3d dz = zunit.multiply(z + halfChunkSizes.z);
                    
                    Vector3d blockCenter = mapOrigin.add(dx).add(dy).add(dz);
                    
                    boolean mh = checkBlock(blockCenter, radiusSquared, rayDir);
                    if(mh) {
                        next.add(blockCenter.add(xunit2).add(yunit2).add(zunit2));
                        next.add(blockCenter.add(xunit2).add(yunit2).sub(zunit2));
                        next.add(blockCenter.add(xunit2).sub(yunit2).add(zunit2));
                        next.add(blockCenter.add(xunit2).sub(yunit2).sub(zunit2));
                        next.add(blockCenter.sub(xunit2).add(yunit2).add(zunit2));
                        next.add(blockCenter.sub(xunit2).add(yunit2).sub(zunit2));
                        next.add(blockCenter.sub(xunit2).sub(yunit2).add(zunit2));
                        next.add(blockCenter.sub(xunit2).sub(yunit2).sub(zunit2));
                    }
                }
            }
        }
        
        for(Vector3d block4: next) {
            
        }
    }
    
        
    public static void find3(Vector3d mapCenter, Quaternion mapRot, Cube3dMap map, Vector3d rayPos, Vector3d rayDir) {
        Vector3d chunkSizes = new Vector3d(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
        Vector3d halfChunkSizes = chunkSizes.divide(2);
        double radius = halfChunkSizes.length();
        
        Vector3d blockCenterOffset = halfChunkSizes.sub(map.getCenter3d());
        
        for(int x=0; x < map.xsize; x += CHUNK_SIZE) {
            for(int y=0; y < map.ysize; y += CHUNK_SIZE) {
                for(int z=0; z < map.zsize; z += CHUNK_SIZE) {
                    Vector3d blockCenter = new Vector3d(x, y, z).add(blockCenterOffset).multiply(mapRot).add(mapCenter);
                    
                    boolean mh = checkBlock2(blockCenter, radius, rayPos, rayDir);
                }
            }
        }
    }

    // this is optimized using JippoRangeFinder
    public static void find2(Vector3d mapCenter, Quaternion mapRot, Cube3dMap map, Vector3d rayPos, Vector3d rayDir) {
        Vector3d chunkSizes = new Vector3d(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
        Vector3d halfChunkSizes = chunkSizes.divide(2);
        double radius = halfChunkSizes.length();

        Vector3d mapOrigin = map.getCenter3d().negate().multiply(mapRot).add(mapCenter);

        Vector3d xunit = Vector3d.UNIT_X.multiply(mapRot);
        Vector3d yunit = Vector3d.UNIT_Y.multiply(mapRot);
        Vector3d zunit = Vector3d.UNIT_Z.multiply(mapRot);
        
        Vector3d xunit2 = xunit.multiply(2);
        Vector3d yunit2 = yunit.multiply(2);
        Vector3d zunit2 = zunit.multiply(2);

        Array<Vector3d> next = new Array<>();
        for(int x=0; x < map.xsize; x += CHUNK_SIZE) {
            for(int y=0; y < map.ysize; y += CHUNK_SIZE) {
                for(int z=0; z < map.zsize; z += CHUNK_SIZE) {
                    Vector3d dx = xunit.multiply(x + halfChunkSizes.x);
                    Vector3d dy = yunit.multiply(y + halfChunkSizes.y);
                    Vector3d dz = zunit.multiply(z + halfChunkSizes.z);
                    
                    Vector3d blockCenter = mapOrigin.add(dx).add(dy).add(dz);
                    
                    boolean mh = checkBlock2(blockCenter, radius, rayPos, rayDir);
                    if(mh) {
                        next.add(blockCenter.add(xunit2).add(yunit2).add(zunit2));
                        next.add(blockCenter.add(xunit2).add(yunit2).sub(zunit2));
                        next.add(blockCenter.add(xunit2).sub(yunit2).add(zunit2));
                        next.add(blockCenter.add(xunit2).sub(yunit2).sub(zunit2));
                        next.add(blockCenter.sub(xunit2).add(yunit2).add(zunit2));
                        next.add(blockCenter.sub(xunit2).add(yunit2).sub(zunit2));
                        next.add(blockCenter.sub(xunit2).sub(yunit2).add(zunit2));
                        next.add(blockCenter.sub(xunit2).sub(yunit2).sub(zunit2));
                    }
                }
            }
        }
    }
    
    private static boolean checkBlock(Vector3d blockCenter, double blockRadiusSquared, Vector3d rayDir) {
        double distance = rayDir.distanceToPointSquared(blockCenter);
        return distance <= blockRadiusSquared;
    }

    private static boolean checkBlock2(Vector3d blockCenter, double blockRadius, Vector3d rayPos, Vector3d rayDir) {
        double distance = blockCenter.distance(rayPos);
        Vector3d rayPoint = rayPos.add(rayDir.multiply(distance));
        double distance2 = rayPoint.distance(blockCenter);
        return distance2 <= blockRadius;
    }
    
    private static boolean checkBlock3(Vector3d blockCenter, double blockRadius, Vector3d rayDir) {
        double distance = rayDir.distanceToPoint(blockCenter);
        return distance <= blockRadius;
    }

    public static void main(String[] args) throws Exception {
        Vector3d mapCenter = new Vector3d(1000, 1000, 1000);
        Vector3d rayPos = new Vector3d(1100, 2000, 1100);
        Vector3d rayDir = new Vector3d(-100, -1000, -100).normalize();
        Vector3d mapOffset = mapCenter.sub(rayPos);
        for(int x =0; x < 10; x++) {
        long a = System.currentTimeMillis();
        for(int i =0; i  < 1000000000; i++) {
            //checkBlock2(mapCenter, 10, rayPos, rayDir);
            checkBlock3(mapOffset, 10, rayDir);
            //System.out.println(checkBlock(mapOffset, 10, rayDir));
            //System.out.println(checkBlock2(mapCenter, 10, rayPos, rayDir));
        }
        long b = System.currentTimeMillis();
        System.out.println("" + (b- a));
        }
    }
    
}
