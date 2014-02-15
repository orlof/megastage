/*    */ package com.cubes;
/*    */ 
/*    */ public class BlockChunk_TransparencyMerger
/*    */   implements BlockChunk_MeshMerger
/*    */ {
/* 18 */   public static final BlockChunk_TransparencyMerger OPAQUE = new BlockChunk_TransparencyMerger(false);
/* 19 */   public static final BlockChunk_TransparencyMerger TRANSPARENT = new BlockChunk_TransparencyMerger(true);
/*    */   private boolean isGeometryTransparent;
/*    */ 
/*    */   private BlockChunk_TransparencyMerger(boolean isGeometryTransparent)
/*    */   {
/* 16 */     this.isGeometryTransparent = isGeometryTransparent;
/*    */   }
/*    */ 
/*    */   public boolean shouldFaceBeAdded(BlockChunkControl chunk, Vector3Int location, Block.Face face)
/*    */   {
/* 24 */     BlockType block = chunk.getBlock(location);
/* 25 */     if (block.getSkin().isTransparent() == this.isGeometryTransparent) {
/* 26 */       BlockType neighborBlock = chunk.getNeighborBlock_Local(location, face);
/* 27 */       if (neighborBlock != null) {
/* 28 */         if (block.getSkin().isTransparent() != neighborBlock.getSkin().isTransparent()) {
/* 29 */           return true;
/*    */         }
/* 31 */         return false;
/*    */       }
/* 33 */       return true;
/*    */     }
/* 35 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockChunk_TransparencyMerger
 * JD-Core Version:    0.6.2
 */