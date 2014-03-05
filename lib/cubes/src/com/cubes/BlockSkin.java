/*    */ package com.cubes;
/*    */ 
/*    */ public class BlockSkin
/*    */ {
/*    */   private BlockSkin_TextureLocation[] textureLocations;
/*    */   private boolean isTransparent;
/*    */ 
/*    */   public BlockSkin(BlockSkin_TextureLocation textureLocation, boolean isTransparent)
/*    */   {
/* 14 */     this(new BlockSkin_TextureLocation[] { textureLocation }, isTransparent);
/*    */   }
/*    */ 
/*    */   public BlockSkin(BlockSkin_TextureLocation[] textureLocations, boolean isTransparent) {
/* 18 */     this.textureLocations = textureLocations;
/* 19 */     this.isTransparent = isTransparent;
/*    */   }
/*    */ 
/*    */   public BlockSkin_TextureLocation getTextureLocation(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face)
/*    */   {
/* 25 */     return this.textureLocations[getTextureLocationIndex(chunk, blockLocation, face)];
/*    */   }
/*    */ 
/*    */   protected int getTextureLocationIndex(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face) {
/* 29 */     if (this.textureLocations.length == 6) {
/* 30 */       return face.ordinal();
/*    */     }
/* 32 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean isTransparent() {
/* 36 */     return this.isTransparent;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockSkin
 * JD-Core Version:    0.6.2
 */