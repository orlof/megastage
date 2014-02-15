/*     */ package com.cubes;
/*     */ 
/*     */ import com.cubes.network.BitInputStream;
/*     */ import com.cubes.network.BitOutputStream;
/*     */ import com.cubes.network.BitSerializable;
/*     */ import com.jme3.math.Vector3f;
/*     */ import com.jme3.renderer.RenderManager;
/*     */ import com.jme3.renderer.ViewPort;
/*     */ import com.jme3.renderer.queue.RenderQueue.Bucket;
/*     */ import com.jme3.scene.Geometry;
/*     */ import com.jme3.scene.Node;
/*     */ import com.jme3.scene.Spatial;
/*     */ import com.jme3.scene.control.AbstractControl;
/*     */ import com.jme3.scene.control.Control;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class BlockChunkControl extends AbstractControl
/*     */   implements BitSerializable
/*     */ {
/*     */   private BlockTerrainControl terrain;
/*  34 */   private Vector3Int location = new Vector3Int();
/*  35 */   private Vector3Int blockLocation = new Vector3Int();
/*     */   private byte[][][] blockTypes;
/*     */   private boolean[][][] blocks_IsOnSurface;
/*  38 */   private Node node = new Node();
/*     */   private Geometry optimizedGeometry_Opaque;
/*     */   private Geometry optimizedGeometry_Transparent;
/*     */   private boolean needsMeshUpdate;
/*     */ 
/*     */   public BlockChunkControl(BlockTerrainControl terrain, int x, int y, int z)
/*     */   {
/*  26 */     this.terrain = terrain;
/*  27 */     this.location.set(x, y, z);
/*  28 */     this.blockLocation.set(this.location.mult(terrain.getSettings().getChunkSizeX(), terrain.getSettings().getChunkSizeY(), terrain.getSettings().getChunkSizeZ()));
/*  29 */     this.node.setLocalTranslation(new Vector3f(this.blockLocation.getX(), this.blockLocation.getY(), this.blockLocation.getZ()).mult(terrain.getSettings().getBlockSize()));
/*  30 */     this.blockTypes = new byte[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
/*  31 */     this.blocks_IsOnSurface = new boolean[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
/*     */   }
/*     */ 
/*     */   public void setSpatial(Spatial spatial)
/*     */   {
/*  45 */     Spatial oldSpatial = this.spatial;
/*  46 */     super.setSpatial(spatial);
/*  47 */     if ((spatial instanceof Node)) {
/*  48 */       Node parentNode = (Node)spatial;
/*  49 */       parentNode.attachChild(this.node);
/*     */     }
/*  51 */     else if ((oldSpatial instanceof Node)) {
/*  52 */       Node oldNode = (Node)oldSpatial;
/*  53 */       oldNode.detachChild(this.node);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void controlUpdate(float lastTimePerFrame)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void controlRender(RenderManager renderManager, ViewPort viewPort)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Control cloneForSpatial(Spatial spatial)
/*     */   {
/*  69 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   public BlockType getNeighborBlock_Local(Vector3Int location, Block.Face face) {
/*  73 */     Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
/*  74 */     return getBlock(neighborLocation);
/*     */   }
/*     */ 
/*     */   public BlockType getNeighborBlock_Global(Vector3Int location, Block.Face face) {
/*  78 */     return this.terrain.getBlock(getNeighborBlockGlobalLocation(location, face));
/*     */   }
/*     */ 
/*     */   private Vector3Int getNeighborBlockGlobalLocation(Vector3Int location, Block.Face face) {
/*  82 */     Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
/*  83 */     neighborLocation.addLocal(this.blockLocation);
/*  84 */     return neighborLocation;
/*     */   }
/*     */ 
/*     */   public BlockType getBlock(Vector3Int location) {
/*  88 */     if (isValidBlockLocation(location)) {
/*  89 */       byte blockType = this.blockTypes[location.getX()][location.getY()][location.getZ()];
/*  90 */       return BlockManager.getType(blockType);
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   public void setBlock(Vector3Int location, Class<? extends Block> blockClass) {
/*  96 */     if (isValidBlockLocation(location)) {
/*  97 */       BlockType blockType = BlockManager.getType(blockClass);
/*  98 */       this.blockTypes[location.getX()][location.getY()][location.getZ()] = blockType.getType();
/*  99 */       updateBlockState(location);
/* 100 */       this.needsMeshUpdate = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeBlock(Vector3Int location) {
/* 105 */     if (isValidBlockLocation(location)) {
/* 106 */       this.blockTypes[location.getX()][location.getY()][location.getZ()] = 0;
/* 107 */       updateBlockState(location);
/* 108 */       this.needsMeshUpdate = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isValidBlockLocation(Vector3Int location) {
/* 113 */     return Util.isValidIndex(this.blockTypes, location);
/*     */   }
/*     */ 
/*     */   public boolean updateSpatial() {
/* 117 */     if (this.needsMeshUpdate) {
/* 118 */       if (this.optimizedGeometry_Opaque == null) {
/* 119 */         this.optimizedGeometry_Opaque = new Geometry("");
/* 120 */         this.optimizedGeometry_Opaque.setQueueBucket(RenderQueue.Bucket.Opaque);
/* 121 */         this.node.attachChild(this.optimizedGeometry_Opaque);
/* 122 */         updateBlockMaterial();
/*     */       }
/* 124 */       if (this.optimizedGeometry_Transparent == null) {
/* 125 */         this.optimizedGeometry_Transparent = new Geometry("");
/* 126 */         this.optimizedGeometry_Transparent.setQueueBucket(RenderQueue.Bucket.Transparent);
/* 127 */         this.node.attachChild(this.optimizedGeometry_Transparent);
/* 128 */         updateBlockMaterial();
/*     */       }
/* 130 */       this.optimizedGeometry_Opaque.setMesh(BlockChunk_MeshOptimizer.generateOptimizedMesh(this, BlockChunk_TransparencyMerger.OPAQUE));
/* 131 */       this.optimizedGeometry_Transparent.setMesh(BlockChunk_MeshOptimizer.generateOptimizedMesh(this, BlockChunk_TransparencyMerger.TRANSPARENT));
/* 132 */       this.needsMeshUpdate = false;
/* 133 */       return true;
/*     */     }
/* 135 */     return false;
/*     */   }
/*     */ 
/*     */   public void updateBlockMaterial() {
/* 139 */     if (this.optimizedGeometry_Opaque != null) {
/* 140 */       this.optimizedGeometry_Opaque.setMaterial(this.terrain.getSettings().getBlockMaterial());
/*     */     }
/* 142 */     if (this.optimizedGeometry_Transparent != null)
/* 143 */       this.optimizedGeometry_Transparent.setMaterial(this.terrain.getSettings().getBlockMaterial());
/*     */   }
/*     */ 
/*     */   private void updateBlockState(Vector3Int location)
/*     */   {
/* 148 */     updateBlockInformation(location);
/* 149 */     for (int i = 0; i < Block.Face.values().length; i++) {
/* 150 */       Vector3Int neighborLocation = getNeighborBlockGlobalLocation(location, Block.Face.values()[i]);
/* 151 */       BlockChunkControl chunk = this.terrain.getChunk(neighborLocation);
/* 152 */       if (chunk != null)
/* 153 */         chunk.updateBlockInformation(neighborLocation.subtract(chunk.getBlockLocation()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateBlockInformation(Vector3Int location)
/*     */   {
/* 159 */     BlockType neighborBlock_Top = this.terrain.getBlock(getNeighborBlockGlobalLocation(location, Block.Face.Top));
/* 160 */     this.blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()] = (neighborBlock_Top == null ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public boolean isBlockOnSurface(Vector3Int location) {
/* 164 */     return this.blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()];
/*     */   }
/*     */ 
/*     */   public BlockTerrainControl getTerrain() {
/* 168 */     return this.terrain;
/*     */   }
/*     */ 
/*     */   public Vector3Int getLocation() {
/* 172 */     return this.location;
/*     */   }
/*     */ 
/*     */   public Vector3Int getBlockLocation() {
/* 176 */     return this.blockLocation;
/*     */   }
/*     */ 
/*     */   public Node getNode() {
/* 180 */     return this.node;
/*     */   }
/*     */ 
/*     */   public Geometry getOptimizedGeometry_Opaque() {
/* 184 */     return this.optimizedGeometry_Opaque;
/*     */   }
/*     */ 
/*     */   public Geometry getOptimizedGeometry_Transparent() {
/* 188 */     return this.optimizedGeometry_Transparent;
/*     */   }
/*     */ 
/*     */   public void write(BitOutputStream outputStream)
/*     */   {
/* 193 */     for (int x = 0; x < this.blockTypes.length; x++)
/* 194 */       for (int y = 0; y < this.blockTypes[0].length; y++)
/* 195 */         for (int z = 0; z < this.blockTypes[0][0].length; z++)
/* 196 */           outputStream.writeBits(this.blockTypes[x][y][z], 8);
/*     */   }
/*     */ 
/*     */   public void read(BitInputStream inputStream)
/*     */     throws IOException
/*     */   {
/* 204 */     for (int x = 0; x < this.blockTypes.length; x++) {
/* 205 */       for (int y = 0; y < this.blockTypes[0].length; y++) {
/* 206 */         for (int z = 0; z < this.blockTypes[0][0].length; z++) {
/* 207 */           this.blockTypes[x][y][z] = ((byte)inputStream.readBits(8));
/*     */         }
/*     */       }
/*     */     }
/* 211 */     Vector3Int tmpLocation = new Vector3Int();
/* 212 */     for (int x = 0; x < this.blockTypes.length; x++) {
/* 213 */       for (int y = 0; y < this.blockTypes[0].length; y++) {
/* 214 */         for (int z = 0; z < this.blockTypes[0][0].length; z++) {
/* 215 */           tmpLocation.set(x, y, z);
/* 216 */           updateBlockInformation(tmpLocation);
/*     */         }
/*     */       }
/*     */     }
/* 220 */     this.needsMeshUpdate = true;
/*     */   }
/*     */ 
/*     */   private Vector3Int getNeededBlockChunks(Vector3Int blocksCount) {
/* 224 */     int chunksCountX = (int)Math.ceil(blocksCount.getX() / this.terrain.getSettings().getChunkSizeX());
/* 225 */     int chunksCountY = (int)Math.ceil(blocksCount.getY() / this.terrain.getSettings().getChunkSizeY());
/* 226 */     int chunksCountZ = (int)Math.ceil(blocksCount.getZ() / this.terrain.getSettings().getChunkSizeZ());
/* 227 */     return new Vector3Int(chunksCountX, chunksCountY, chunksCountZ);
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockChunkControl
 * JD-Core Version:    0.6.2
 */