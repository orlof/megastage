/*    */ package com.cubes;
/*    */ 
/*    */ public class BlockTerrain_LocalBlockState
/*    */ {
/*    */   private BlockChunkControl chunk;
/*    */   private Vector3Int localBlockLocation;
/*    */ 
/*    */   public BlockTerrain_LocalBlockState(BlockChunkControl chunk, Vector3Int localBlockLocation)
/*    */   {
/* 14 */     this.chunk = chunk;
/* 15 */     this.localBlockLocation = localBlockLocation;
/*    */   }
/*    */ 
/*    */   public BlockChunkControl getChunk()
/*    */   {
/* 21 */     return this.chunk;
/*    */   }
/*    */ 
/*    */   public Vector3Int getLocalBlockLocation() {
/* 25 */     return this.localBlockLocation;
/*    */   }
/*    */ 
/*    */   public BlockType getBlock() {
/* 29 */     return this.chunk.getBlock(this.localBlockLocation);
/*    */   }
/*    */ 
/*    */   public void setBlock(Class<? extends Block> blockClass) {
/* 33 */     this.chunk.setBlock(this.localBlockLocation, blockClass);
/*    */   }
/*    */ 
/*    */   public void removeBlock() {
/* 37 */     this.chunk.removeBlock(this.localBlockLocation);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockTerrain_LocalBlockState
 * JD-Core Version:    0.6.2
 */