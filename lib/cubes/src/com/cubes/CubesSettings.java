/*    */ package com.cubes;
/*    */ 
/*    */ import com.jme3.app.Application;
/*    */ import com.jme3.asset.AssetManager;
/*    */ import com.jme3.material.Material;
/*    */ 
/*    */ public class CubesSettings
/*    */ {
/*    */   private AssetManager assetManager;
/* 21 */   private float blockSize = 3.0F;
/* 22 */   private int chunkSizeX = 16;
/* 23 */   private int chunkSizeY = 256;
/* 24 */   private int chunkSizeZ = 16;
/*    */   private Material blockMaterial;
/*    */ 
/*    */   public CubesSettings(Application application)
/*    */   {
/* 18 */     this.assetManager = application.getAssetManager();
/*    */   }
/*    */ 
/*    */   public AssetManager getAssetManager()
/*    */   {
/* 28 */     return this.assetManager;
/*    */   }
/*    */ 
/*    */   public float getBlockSize() {
/* 32 */     return this.blockSize;
/*    */   }
/*    */ 
/*    */   public void setBlockSize(float blockSize) {
/* 36 */     this.blockSize = blockSize;
/*    */   }
/*    */ 
/*    */   public int getChunkSizeX() {
/* 40 */     return this.chunkSizeX;
/*    */   }
/*    */ 
/*    */   public void setChunkSizeX(int chunkSizeX) {
/* 44 */     this.chunkSizeX = chunkSizeX;
/*    */   }
/*    */ 
/*    */   public int getChunkSizeY() {
/* 48 */     return this.chunkSizeY;
/*    */   }
/*    */ 
/*    */   public void setChunkSizeY(int chunkSizeY) {
/* 52 */     this.chunkSizeY = chunkSizeY;
/*    */   }
/*    */ 
/*    */   public int getChunkSizeZ() {
/* 56 */     return this.chunkSizeZ;
/*    */   }
/*    */ 
/*    */   public void setChunkSizeZ(int chunkSizeZ) {
/* 60 */     this.chunkSizeZ = chunkSizeZ;
/*    */   }
/*    */ 
/*    */   public Material getBlockMaterial() {
/* 64 */     return this.blockMaterial;
/*    */   }
/*    */ 
/*    */   public void setDefaultBlockMaterial(String textureFilePath) {
/* 68 */     setBlockMaterial(new BlockChunk_Material(this.assetManager, textureFilePath));
/*    */   }
/*    */ 
/*    */   public void setBlockMaterial(Material blockMaterial) {
/* 72 */     this.blockMaterial = blockMaterial;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.CubesSettings
 * JD-Core Version:    0.6.2
 */