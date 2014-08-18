package com.cubes;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

public class CubesSettings
{
  private AssetManager assetManager;
  private float blockSize = 3.0F;
  private int chunkSizeX = 16;
  private int chunkSizeY = 256;
  private int chunkSizeZ = 16;
  private Material blockMaterial;

  public CubesSettings(Application application)
  {
    this.assetManager = application.getAssetManager();
  }

  public AssetManager getAssetManager()
  {
    return this.assetManager;
  }

  public float getBlockSize() {
    return this.blockSize;
  }

  public void setBlockSize(float blockSize) {
    this.blockSize = blockSize;
  }

  public int getChunkSizeX() {
    return this.chunkSizeX;
  }

  public void setChunkSizeX(int chunkSizeX) {
    this.chunkSizeX = chunkSizeX;
  }

  public int getChunkSizeY() {
    return this.chunkSizeY;
  }

  public void setChunkSizeY(int chunkSizeY) {
    this.chunkSizeY = chunkSizeY;
  }

  public int getChunkSizeZ() {
    return this.chunkSizeZ;
  }

  public void setChunkSizeZ(int chunkSizeZ) {
    this.chunkSizeZ = chunkSizeZ;
  }

  public Material getBlockMaterial() {
    return this.blockMaterial;
  }

  public void setDefaultBlockMaterial(String textureFilePath) {
    setBlockMaterial(new BlockChunk_Material(this.assetManager, textureFilePath));
  }

  public void setBlockMaterial(Material blockMaterial) {
    this.blockMaterial = blockMaterial;
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.CubesSettings
 * JD-Core Version:    0.6.2
 */