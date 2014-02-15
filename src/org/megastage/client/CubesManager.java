package org.megastage.client;

import com.cubes.Block;
import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.jme3.app.SimpleApplication;
import org.megastage.util.Cube3dMap;

public class CubesManager {
    private static CubesSettings settings;
    private static final int CHUNK_SIZE_X = 24;
    private static final int CHUNK_SIZE_Y = 16;
    private static final int CHUNK_SIZE_Z = 32;
    
    public static void init(SimpleApplication appl) {
        settings = new CubesSettings(appl);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setBlockSize(1);
        settings.setChunkSizeX(CHUNK_SIZE_X);
        settings.setChunkSizeY(CHUNK_SIZE_Y);
        settings.setChunkSizeZ(CHUNK_SIZE_Z);
        
        BlockManager.register(Stone.class, new BlockSkin(new BlockSkin_TextureLocation(6, 0), false));
        BlockManager.register(Metal.class, new BlockSkin(new BlockSkin_TextureLocation(5, 0), false));
    }

    public static BlockTerrainControl getControl(Cube3dMap map) {
        Vector3Int chunkSizes = new Vector3Int(
                map.xsize / CHUNK_SIZE_X + 1, 
                map.ysize / CHUNK_SIZE_Y + 1, 
                map.zsize / CHUNK_SIZE_Z + 1);

        return new BlockTerrainControl(settings, chunkSizes);
    }
    
    public class Stone extends Block {}
    public class Metal extends Block {}
}
