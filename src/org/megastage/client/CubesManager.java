package org.megastage.client;

import com.cubes.Block;
import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.jme3.app.SimpleApplication;

public class CubesManager {
    private static CubesSettings settings;
    private static final int CHUNK_SIZE = 16;
    
    public static void init(SimpleApplication appl) {
        settings = new CubesSettings(appl);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setBlockSize(1);
        settings.setChunkSizeX(CHUNK_SIZE);
        settings.setChunkSizeY(CHUNK_SIZE);
        settings.setChunkSizeZ(CHUNK_SIZE);

        int fx=6,fy=0,wx=6,wy=1;
        BlockManager.register(Combi.class, new BlockSkin(new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation(fx, fy), new BlockSkin_TextureLocation(fx, fy), new BlockSkin_TextureLocation(wx, wy), new BlockSkin_TextureLocation(wx, wy), new BlockSkin_TextureLocation(wx, wy), new BlockSkin_TextureLocation(wx, wy) }, false));
        BlockManager.register(Floor.class, new BlockSkin(new BlockSkin_TextureLocation(0, 0), false));
        BlockManager.register(Wall.class, new BlockSkin(new BlockSkin_TextureLocation(0, 3), false));
    }

    public static BlockTerrainControl getControl(int size) {
        int numChunks = size / CHUNK_SIZE + (size % CHUNK_SIZE == 0 ? 0 : 1);
        Vector3Int chunks = new Vector3Int(numChunks, numChunks, numChunks);
        return new BlockTerrainControl(settings, chunks);
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
