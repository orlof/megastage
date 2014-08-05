package org.megastage.client;

import com.cubes.Block;
import com.cubes.BlockManager;
import com.cubes.BlockNavigator;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.megastage.util.Cube3dMap;

public class CubesManager {
    private static CubesSettings settings;
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Y = 16;
    private static final int CHUNK_SIZE_Z = 16;
    
    public static void init(SimpleApplication appl) {
        settings = new CubesSettings(appl);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setBlockSize(1);
        settings.setChunkSizeX(CHUNK_SIZE_X);
        settings.setChunkSizeY(CHUNK_SIZE_Y);
        settings.setChunkSizeZ(CHUNK_SIZE_Z);

        int fx=6,fy=0,wx=6,wy=1;
        BlockManager.register(Combi.class, new BlockSkin(new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation(fx, fy), new BlockSkin_TextureLocation(fx, fy), new BlockSkin_TextureLocation(wx, wy), new BlockSkin_TextureLocation(wx, wy), new BlockSkin_TextureLocation(wx, wy), new BlockSkin_TextureLocation(wx, wy) }, false));
        BlockManager.register(Floor.class, new BlockSkin(new BlockSkin_TextureLocation(0, 0), false));
        BlockManager.register(Wall.class, new BlockSkin(new BlockSkin_TextureLocation(0, 3), false));
    }

    public static BlockTerrainControl getControl(Cube3dMap map) {
//        Vector3Int chunkSizes = new Vector3Int(
//                map.xsize / CHUNK_SIZE_X + 1, 
//                map.ysize / CHUNK_SIZE_Y + 1, 
//                map.zsize / CHUNK_SIZE_Z + 1);

        return new BlockTerrainControl(settings, new Vector3Int(3, 3, 3));
    }

    public static Class<? extends Block> getBlock(char c) {
        switch(c) {
            case '#':
                return Combi.class;
            case 'W':
                return Wall.class;
        }
        return null;
    }

//    public static Vector3Int getCurrentPointedBlockLocation(Node terrainNode, boolean getNeighborLocation){
//        CollisionResults results = ClientGlobals.app.getRayCastingResults(terrainNode);
//        if(results.size() > 0) {
//            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
//            collisionContactPoint.subtractLocal(terrainNode.getLocalTranslation());
//            BlockTerrainControl ctrl = terrainNode.getControl(BlockTerrainControl.class);
//            return BlockNavigator.getPointedBlockLocation(ctrl, collisionContactPoint, getNeighborLocation);
//        }
//        return null;
//    }
//
    public class Floor extends Block {}
    public class Combi extends Block {}
    public class Wall extends Block {}
}
