package com.cubes;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class BlockTerrainControl extends AbstractControl
{
  private CubesSettings settings;
  private BlockChunkControl[][][] chunks;
  private ArrayList<BlockChunkListener> chunkListeners = new ArrayList();

  public BlockTerrainControl(CubesSettings settings, Vector3Int chunksCount)
  {
    this.settings = settings;
    initializeChunks(chunksCount);
  }

  private void initializeChunks(Vector3Int chunksCount)
  {
    this.chunks = new BlockChunkControl[chunksCount.getX()][chunksCount.getY()][chunksCount.getZ()];
    for (int x = 0; x < this.chunks.length; x++)
      for (int y = 0; y < this.chunks[0].length; y++)
        for (int z = 0; z < this.chunks[0][0].length; z++) {
          BlockChunkControl chunk = new BlockChunkControl(this, x, y, z);
          this.chunks[x][y][z] = chunk;
        }
  }

  public void setSpatial(Spatial spatial)
  {
    Spatial oldSpatial = this.spatial;
    super.setSpatial(spatial);
    for (int x = 0; x < this.chunks.length; x++)
      for (int y = 0; y < this.chunks[0].length; y++)
        for (int z = 0; z < this.chunks[0][0].length; z++)
          if (spatial == null) {
            oldSpatial.removeControl(this.chunks[x][y][z]);
          }
          else
            spatial.addControl(this.chunks[x][y][z]);
  }

  protected void controlUpdate(float lastTimePerFrame)
  {
    updateSpatial();
  }

  protected void controlRender(RenderManager renderManager, ViewPort viewPort)
  {
  }

  public Control cloneForSpatial(Spatial spatial)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public BlockType getBlock(int x, int y, int z) {
    return getBlock(new Vector3Int(x, y, z));
  }

  public BlockType getBlock(Vector3Int location) {
    BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
    if (localBlockState != null) {
      return localBlockState.getBlock();
    }
    return null;
  }

  public void setBlockArea(Vector3Int location, Vector3Int size, Class<? extends Block> blockClass) {
    Vector3Int tmpLocation = new Vector3Int();
    for (int x = 0; x < size.getX(); x++)
      for (int y = 0; y < size.getY(); y++)
        for (int z = 0; z < size.getZ(); z++) {
          tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
          setBlock(tmpLocation, blockClass);
        }
  }

  public void setBlock(int x, int y, int z, Class<? extends Block> blockClass)
  {
    setBlock(new Vector3Int(x, y, z), blockClass);
  }

  public void setBlock(Vector3Int location, Class<? extends Block> blockClass) {
    BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
    if (localBlockState != null)
      localBlockState.setBlock(blockClass);
  }

  public void removeBlockArea(Vector3Int location, Vector3Int size)
  {
    Vector3Int tmpLocation = new Vector3Int();
    for (int x = 0; x < size.getX(); x++)
      for (int y = 0; y < size.getY(); y++)
        for (int z = 0; z < size.getZ(); z++) {
          tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
          removeBlock(tmpLocation);
        }
  }

  public void removeBlock(int x, int y, int z)
  {
    removeBlock(new Vector3Int(x, y, z));
  }

  public void removeBlock(Vector3Int location) {
    BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
    if (localBlockState != null)
      localBlockState.removeBlock();
  }

  private BlockTerrain_LocalBlockState getLocalBlockState(Vector3Int blockLocation)
  {
    if (blockLocation.hasNegativeCoordinate()) {
      return null;
    }
    BlockChunkControl chunk = getChunk(blockLocation);
    if (chunk != null) {
      Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
      return new BlockTerrain_LocalBlockState(chunk, localBlockLocation);
    }
    return null;
  }

  public BlockChunkControl getChunk(Vector3Int blockLocation) {
    if (blockLocation.hasNegativeCoordinate()) {
      return null;
    }
    Vector3Int chunkLocation = getChunkLocation(blockLocation);
    if (isValidChunkLocation(chunkLocation)) {
      return this.chunks[chunkLocation.getX()][chunkLocation.getY()][chunkLocation.getZ()];
    }
    return null;
  }

  private boolean isValidChunkLocation(Vector3Int location) {
    return Util.isValidIndex(this.chunks, location);
  }

  private Vector3Int getChunkLocation(Vector3Int blockLocation) {
    Vector3Int chunkLocation = new Vector3Int();
    int chunkX = blockLocation.getX() / this.settings.getChunkSizeX();
    int chunkY = blockLocation.getY() / this.settings.getChunkSizeY();
    int chunkZ = blockLocation.getZ() / this.settings.getChunkSizeZ();
    chunkLocation.set(chunkX, chunkY, chunkZ);
    return chunkLocation;
  }

  public static Vector3Int getLocalBlockLocation(Vector3Int blockLocation, BlockChunkControl chunk) {
    Vector3Int localLocation = new Vector3Int();
    int localX = blockLocation.getX() - chunk.getBlockLocation().getX();
    int localY = blockLocation.getY() - chunk.getBlockLocation().getY();
    int localZ = blockLocation.getZ() - chunk.getBlockLocation().getZ();
    localLocation.set(localX, localY, localZ);
    return localLocation;
  }

  public boolean updateSpatial() {
    boolean wasUpdatedNeeded = false;
    for (int x = 0; x < this.chunks.length; x++) {
      for (int y = 0; y < this.chunks[0].length; y++) {
        for (int z = 0; z < this.chunks[0][0].length; z++) {
          BlockChunkControl chunk = this.chunks[x][y][z];
          if (chunk.updateSpatial()) {
            wasUpdatedNeeded = true;
            for (int i = 0; i < this.chunkListeners.size(); i++) {
              BlockChunkListener blockTerrainListener = (BlockChunkListener)this.chunkListeners.get(i);
              blockTerrainListener.onSpatialUpdated(chunk);
            }
          }
        }
      }
    }
    return wasUpdatedNeeded;
  }

  public void updateBlockMaterial() {
    for (int x = 0; x < this.chunks.length; x++)
      for (int y = 0; y < this.chunks[0].length; y++)
        for (int z = 0; z < this.chunks[0][0].length; z++)
          this.chunks[x][y][z].updateBlockMaterial();
  }

  public void addChunkListener(BlockChunkListener blockChunkListener)
  {
    this.chunkListeners.add(blockChunkListener);
  }

  public void removeChunkListener(BlockChunkListener blockChunkListener) {
    this.chunkListeners.remove(blockChunkListener);
  }

  public CubesSettings getSettings() {
    return this.settings;
  }

  public BlockChunkControl[][][] getChunks() {
    return this.chunks;
  }

  public void setBlocksFromHeightmap(Vector3Int location, String heightmapPath, int maximumHeight, Class<? extends Block> blockClass)
  {
    try
    {
      Texture heightmapTexture = this.settings.getAssetManager().loadTexture(heightmapPath);
      ImageBasedHeightMap heightmap = new ImageBasedHeightMap(heightmapTexture.getImage(), 1.0F);
      heightmap.load();
      heightmap.setHeightScale(maximumHeight / 255.0F);
      setBlocksFromHeightmap(location, getHeightmapBlockData(heightmap.getScaledHeightMap(), heightmap.getSize()), blockClass);
    } catch (Exception ex) {
      System.out.println("Error while loading heightmap '" + heightmapPath + "'.");
    }
  }

  private static int[][] getHeightmapBlockData(float[] heightmapData, int length) {
    int[][] data = new int[heightmapData.length / length][length];
    int x = 0;
    int z = 0;
    for (int i = 0; i < heightmapData.length; i++) {
      data[x][z] = Math.round(heightmapData[i]);
      x++;
      if ((x != 0) && (x % length == 0)) {
        x = 0;
        z++;
      }
    }
    return data;
  }

  public void setBlocksFromHeightmap(Vector3Int location, int[][] heightmap, Class<? extends Block> blockClass) {
    Vector3Int tmpLocation = new Vector3Int();
    Vector3Int tmpSize = new Vector3Int();
    for (int x = 0; x < heightmap.length; x++)
      for (int z = 0; z < heightmap[0].length; z++) {
        tmpLocation.set(location.getX() + x, location.getY(), location.getZ() + z);
        tmpSize.set(1, heightmap[x][z], 1);
        setBlockArea(tmpLocation, tmpSize, blockClass);
      }
  }

  public void setBlocksFromNoise(Vector3Int location, Vector3Int size, float roughness, Class<? extends Block> blockClass)
  {
    Noise noise = new Noise(null, roughness, size.getX(), size.getZ());
    noise.initialise();
    float gridMinimum = noise.getMinimum();
    float gridLargestDifference = noise.getMaximum() - gridMinimum;
    float[][] grid = noise.getGrid();
    for (int x = 0; x < grid.length; x++) {
      float[] row = grid[x];
      for (int z = 0; z < row.length; z++)
      {
        int blockHeight = (int)((row[z] - gridMinimum) * 100.0F / gridLargestDifference / 100.0F * size.getY()) + 1;
        Vector3Int tmpLocation = new Vector3Int();
        for (int y = 0; y < blockHeight; y++) {
          tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
          setBlock(tmpLocation, blockClass);
        }
      }
    }
  }

  public void setBlocksForMaximumFaces(Vector3Int location, Vector3Int size, Class<? extends Block> blockClass) {
    Vector3Int tmpLocation = new Vector3Int();
    for (int x = 0; x < size.getX(); x++)
      for (int y = 0; y < size.getY(); y++)
        for (int z = 0; z < size.getZ(); z++)
          if (((x ^ y ^ z) & 0x1) == 1) {
            tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
            setBlock(tmpLocation, blockClass);
          }
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.BlockTerrainControl
 * JD-Core Version:    0.6.2
 */