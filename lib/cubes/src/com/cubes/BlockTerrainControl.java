/*     */ package com.cubes;
/*     */ 
/*     */ import com.cubes.network.BitInputStream;
/*     */ import com.cubes.network.BitOutputStream;
/*     */ import com.cubes.network.BitSerializable;
/*     */ import com.cubes.network.CubesSerializer;
/*     */ import com.jme3.asset.AssetManager;
/*     */ import com.jme3.renderer.RenderManager;
/*     */ import com.jme3.renderer.ViewPort;
/*     */ import com.jme3.scene.Spatial;
/*     */ import com.jme3.scene.control.AbstractControl;
/*     */ import com.jme3.scene.control.Control;
/*     */ import com.jme3.terrain.heightmap.ImageBasedHeightMap;
/*     */ import com.jme3.texture.Texture;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class BlockTerrainControl extends AbstractControl
/*     */   implements BitSerializable
/*     */ {
/*     */   private CubesSettings settings;
/*     */   private BlockChunkControl[][][] chunks;
/*  30 */   private ArrayList<BlockChunkListener> chunkListeners = new ArrayList();
/*     */ 
/*     */   public BlockTerrainControl(CubesSettings settings, Vector3Int chunksCount)
/*     */   {
/*  25 */     this.settings = settings;
/*  26 */     initializeChunks(chunksCount);
/*     */   }
/*     */ 
/*     */   private void initializeChunks(Vector3Int chunksCount)
/*     */   {
/*  33 */     this.chunks = new BlockChunkControl[chunksCount.getX()][chunksCount.getY()][chunksCount.getZ()];
/*  34 */     for (int x = 0; x < this.chunks.length; x++)
/*  35 */       for (int y = 0; y < this.chunks[0].length; y++)
/*  36 */         for (int z = 0; z < this.chunks[0][0].length; z++) {
/*  37 */           BlockChunkControl chunk = new BlockChunkControl(this, x, y, z);
/*  38 */           this.chunks[x][y][z] = chunk;
/*     */         }
/*     */   }
/*     */ 
/*     */   public void setSpatial(Spatial spatial)
/*     */   {
/*  46 */     Spatial oldSpatial = this.spatial;
/*  47 */     super.setSpatial(spatial);
/*  48 */     for (int x = 0; x < this.chunks.length; x++)
/*  49 */       for (int y = 0; y < this.chunks[0].length; y++)
/*  50 */         for (int z = 0; z < this.chunks[0][0].length; z++)
/*  51 */           if (spatial == null) {
/*  52 */             oldSpatial.removeControl(this.chunks[x][y][z]);
/*     */           }
/*     */           else
/*  55 */             spatial.addControl(this.chunks[x][y][z]);
/*     */   }
/*     */ 
/*     */   protected void controlUpdate(float lastTimePerFrame)
/*     */   {
/*  64 */     updateSpatial();
/*     */   }
/*     */ 
/*     */   protected void controlRender(RenderManager renderManager, ViewPort viewPort)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Control cloneForSpatial(Spatial spatial)
/*     */   {
/*  74 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   public BlockType getBlock(int x, int y, int z) {
/*  78 */     return getBlock(new Vector3Int(x, y, z));
/*     */   }
/*     */ 
/*     */   public BlockType getBlock(Vector3Int location) {
/*  82 */     BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
/*  83 */     if (localBlockState != null) {
/*  84 */       return localBlockState.getBlock();
/*     */     }
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public void setBlockArea(Vector3Int location, Vector3Int size, Class<? extends Block> blockClass) {
/*  90 */     Vector3Int tmpLocation = new Vector3Int();
/*  91 */     for (int x = 0; x < size.getX(); x++)
/*  92 */       for (int y = 0; y < size.getY(); y++)
/*  93 */         for (int z = 0; z < size.getZ(); z++) {
/*  94 */           tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
/*  95 */           setBlock(tmpLocation, blockClass);
/*     */         }
/*     */   }
/*     */ 
/*     */   public void setBlock(int x, int y, int z, Class<? extends Block> blockClass)
/*     */   {
/* 102 */     setBlock(new Vector3Int(x, y, z), blockClass);
/*     */   }
/*     */ 
/*     */   public void setBlock(Vector3Int location, Class<? extends Block> blockClass) {
/* 106 */     BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
/* 107 */     if (localBlockState != null)
/* 108 */       localBlockState.setBlock(blockClass);
/*     */   }
/*     */ 
/*     */   public void removeBlockArea(Vector3Int location, Vector3Int size)
/*     */   {
/* 113 */     Vector3Int tmpLocation = new Vector3Int();
/* 114 */     for (int x = 0; x < size.getX(); x++)
/* 115 */       for (int y = 0; y < size.getY(); y++)
/* 116 */         for (int z = 0; z < size.getZ(); z++) {
/* 117 */           tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
/* 118 */           removeBlock(tmpLocation);
/*     */         }
/*     */   }
/*     */ 
/*     */   public void removeBlock(int x, int y, int z)
/*     */   {
/* 125 */     removeBlock(new Vector3Int(x, y, z));
/*     */   }
/*     */ 
/*     */   public void removeBlock(Vector3Int location) {
/* 129 */     BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
/* 130 */     if (localBlockState != null)
/* 131 */       localBlockState.removeBlock();
/*     */   }
/*     */ 
/*     */   private BlockTerrain_LocalBlockState getLocalBlockState(Vector3Int blockLocation)
/*     */   {
/* 136 */     if (blockLocation.hasNegativeCoordinate()) {
/* 137 */       return null;
/*     */     }
/* 139 */     BlockChunkControl chunk = getChunk(blockLocation);
/* 140 */     if (chunk != null) {
/* 141 */       Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
/* 142 */       return new BlockTerrain_LocalBlockState(chunk, localBlockLocation);
/*     */     }
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   public BlockChunkControl getChunk(Vector3Int blockLocation) {
/* 148 */     if (blockLocation.hasNegativeCoordinate()) {
/* 149 */       return null;
/*     */     }
/* 151 */     Vector3Int chunkLocation = getChunkLocation(blockLocation);
/* 152 */     if (isValidChunkLocation(chunkLocation)) {
/* 153 */       return this.chunks[chunkLocation.getX()][chunkLocation.getY()][chunkLocation.getZ()];
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean isValidChunkLocation(Vector3Int location) {
/* 159 */     return Util.isValidIndex(this.chunks, location);
/*     */   }
/*     */ 
/*     */   private Vector3Int getChunkLocation(Vector3Int blockLocation) {
/* 163 */     Vector3Int chunkLocation = new Vector3Int();
/* 164 */     int chunkX = blockLocation.getX() / this.settings.getChunkSizeX();
/* 165 */     int chunkY = blockLocation.getY() / this.settings.getChunkSizeY();
/* 166 */     int chunkZ = blockLocation.getZ() / this.settings.getChunkSizeZ();
/* 167 */     chunkLocation.set(chunkX, chunkY, chunkZ);
/* 168 */     return chunkLocation;
/*     */   }
/*     */ 
/*     */   public static Vector3Int getLocalBlockLocation(Vector3Int blockLocation, BlockChunkControl chunk) {
/* 172 */     Vector3Int localLocation = new Vector3Int();
/* 173 */     int localX = blockLocation.getX() - chunk.getBlockLocation().getX();
/* 174 */     int localY = blockLocation.getY() - chunk.getBlockLocation().getY();
/* 175 */     int localZ = blockLocation.getZ() - chunk.getBlockLocation().getZ();
/* 176 */     localLocation.set(localX, localY, localZ);
/* 177 */     return localLocation;
/*     */   }
/*     */ 
/*     */   public boolean updateSpatial() {
/* 181 */     boolean wasUpdatedNeeded = false;
/* 182 */     for (int x = 0; x < this.chunks.length; x++) {
/* 183 */       for (int y = 0; y < this.chunks[0].length; y++) {
/* 184 */         for (int z = 0; z < this.chunks[0][0].length; z++) {
/* 185 */           BlockChunkControl chunk = this.chunks[x][y][z];
/* 186 */           if (chunk.updateSpatial()) {
/* 187 */             wasUpdatedNeeded = true;
/* 188 */             for (int i = 0; i < this.chunkListeners.size(); i++) {
/* 189 */               BlockChunkListener blockTerrainListener = (BlockChunkListener)this.chunkListeners.get(i);
/* 190 */               blockTerrainListener.onSpatialUpdated(chunk);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 196 */     return wasUpdatedNeeded;
/*     */   }
/*     */ 
/*     */   public void updateBlockMaterial() {
/* 200 */     for (int x = 0; x < this.chunks.length; x++)
/* 201 */       for (int y = 0; y < this.chunks[0].length; y++)
/* 202 */         for (int z = 0; z < this.chunks[0][0].length; z++)
/* 203 */           this.chunks[x][y][z].updateBlockMaterial();
/*     */   }
/*     */ 
/*     */   public void addChunkListener(BlockChunkListener blockChunkListener)
/*     */   {
/* 210 */     this.chunkListeners.add(blockChunkListener);
/*     */   }
/*     */ 
/*     */   public void removeChunkListener(BlockChunkListener blockChunkListener) {
/* 214 */     this.chunkListeners.remove(blockChunkListener);
/*     */   }
/*     */ 
/*     */   public CubesSettings getSettings() {
/* 218 */     return this.settings;
/*     */   }
/*     */ 
/*     */   public BlockChunkControl[][][] getChunks() {
/* 222 */     return this.chunks;
/*     */   }
/*     */ 
/*     */   public void setBlocksFromHeightmap(Vector3Int location, String heightmapPath, int maximumHeight, Class<? extends Block> blockClass)
/*     */   {
/*     */     try
/*     */     {
/* 229 */       Texture heightmapTexture = this.settings.getAssetManager().loadTexture(heightmapPath);
/* 230 */       ImageBasedHeightMap heightmap = new ImageBasedHeightMap(heightmapTexture.getImage(), 1.0F);
/* 231 */       heightmap.load();
/* 232 */       heightmap.setHeightScale(maximumHeight / 255.0F);
/* 233 */       setBlocksFromHeightmap(location, getHeightmapBlockData(heightmap.getScaledHeightMap(), heightmap.getSize()), blockClass);
/*     */     } catch (Exception ex) {
/* 235 */       System.out.println("Error while loading heightmap '" + heightmapPath + "'.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int[][] getHeightmapBlockData(float[] heightmapData, int length) {
/* 240 */     int[][] data = new int[heightmapData.length / length][length];
/* 241 */     int x = 0;
/* 242 */     int z = 0;
/* 243 */     for (int i = 0; i < heightmapData.length; i++) {
/* 244 */       data[x][z] = Math.round(heightmapData[i]);
/* 245 */       x++;
/* 246 */       if ((x != 0) && (x % length == 0)) {
/* 247 */         x = 0;
/* 248 */         z++;
/*     */       }
/*     */     }
/* 251 */     return data;
/*     */   }
/*     */ 
/*     */   public void setBlocksFromHeightmap(Vector3Int location, int[][] heightmap, Class<? extends Block> blockClass) {
/* 255 */     Vector3Int tmpLocation = new Vector3Int();
/* 256 */     Vector3Int tmpSize = new Vector3Int();
/* 257 */     for (int x = 0; x < heightmap.length; x++)
/* 258 */       for (int z = 0; z < heightmap[0].length; z++) {
/* 259 */         tmpLocation.set(location.getX() + x, location.getY(), location.getZ() + z);
/* 260 */         tmpSize.set(1, heightmap[x][z], 1);
/* 261 */         setBlockArea(tmpLocation, tmpSize, blockClass);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setBlocksFromNoise(Vector3Int location, Vector3Int size, float roughness, Class<? extends Block> blockClass)
/*     */   {
/* 267 */     Noise noise = new Noise(null, roughness, size.getX(), size.getZ());
/* 268 */     noise.initialise();
/* 269 */     float gridMinimum = noise.getMinimum();
/* 270 */     float gridLargestDifference = noise.getMaximum() - gridMinimum;
/* 271 */     float[][] grid = noise.getGrid();
/* 272 */     for (int x = 0; x < grid.length; x++) {
/* 273 */       float[] row = grid[x];
/* 274 */       for (int z = 0; z < row.length; z++)
/*     */       {
/* 280 */         int blockHeight = (int)((row[z] - gridMinimum) * 100.0F / gridLargestDifference / 100.0F * size.getY()) + 1;
/* 281 */         Vector3Int tmpLocation = new Vector3Int();
/* 282 */         for (int y = 0; y < blockHeight; y++) {
/* 283 */           tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
/* 284 */           setBlock(tmpLocation, blockClass);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBlocksForMaximumFaces(Vector3Int location, Vector3Int size, Class<? extends Block> blockClass) {
/* 291 */     Vector3Int tmpLocation = new Vector3Int();
/* 292 */     for (int x = 0; x < size.getX(); x++)
/* 293 */       for (int y = 0; y < size.getY(); y++)
/* 294 */         for (int z = 0; z < size.getZ(); z++)
/* 295 */           if (((x ^ y ^ z) & 0x1) == 1) {
/* 296 */             tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
/* 297 */             setBlock(tmpLocation, blockClass);
/*     */           }
/*     */   }
/*     */ 
/*     */   public BlockTerrainControl clone()
/*     */   {
/* 306 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(this.settings, new Vector3Int());
/* 307 */     blockTerrain.setBlocksFromTerrain(this);
/* 308 */     return blockTerrain;
/*     */   }
/*     */ 
/*     */   public void setBlocksFromTerrain(BlockTerrainControl blockTerrain) {
/* 312 */     CubesSerializer.readFromBytes(this, CubesSerializer.writeToBytes(blockTerrain));
/*     */   }
/*     */ 
/*     */   public void write(BitOutputStream outputStream)
/*     */   {
/* 317 */     outputStream.writeInteger(this.chunks.length);
/* 318 */     outputStream.writeInteger(this.chunks[0].length);
/* 319 */     outputStream.writeInteger(this.chunks[0][0].length);
/* 320 */     for (int x = 0; x < this.chunks.length; x++)
/* 321 */       for (int y = 0; y < this.chunks[0].length; y++)
/* 322 */         for (int z = 0; z < this.chunks[0][0].length; z++)
/* 323 */           this.chunks[x][y][z].write(outputStream);
/*     */   }
/*     */ 
/*     */   public void read(BitInputStream inputStream)
/*     */     throws IOException
/*     */   {
/* 331 */     int chunksCountX = inputStream.readInteger();
/* 332 */     int chunksCountY = inputStream.readInteger();
/* 333 */     int chunksCountZ = inputStream.readInteger();
/* 334 */     initializeChunks(new Vector3Int(chunksCountX, chunksCountY, chunksCountZ));
/* 335 */     for (int x = 0; x < chunksCountX; x++)
/* 336 */       for (int y = 0; y < chunksCountY; y++)
/* 337 */         for (int z = 0; z < chunksCountZ; z++)
/* 338 */           this.chunks[x][y][z].read(inputStream);
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockTerrainControl
 * JD-Core Version:    0.6.2
 */