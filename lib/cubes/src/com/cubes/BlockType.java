/*    */ package com.cubes;
/*    */ 
/*    */ public class BlockType
/*    */ {
/*    */   private byte type;
/*    */   private BlockSkin skin;
/*    */ 
/*    */   public BlockType(byte type, BlockSkin skin)
/*    */   {
/* 14 */     this.type = type;
/* 15 */     this.skin = skin;
/*    */   }
/*    */ 
/*    */   public byte getType()
/*    */   {
/* 21 */     return this.type;
/*    */   }
/*    */ 
/*    */   public BlockSkin getSkin() {
/* 25 */     return this.skin;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockType
 * JD-Core Version:    0.6.2
 */