/*    */ package com.cubes;
/*    */ 
/*    */ public class Block
/*    */ {
/*    */   private BlockType type;
/*    */ 
/*    */   public Block()
/*    */   {
/* 14 */     this.type = BlockManager.getType(getClass());
/*    */   }
/*    */ 
/*    */   public BlockType getType()
/*    */   {
/* 22 */     return this.type;
/*    */   }
/*    */ 
/*    */   public static enum Face
/*    */   {
/* 17 */     Top, Bottom, Left, Right, Front, Back;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.Block
 * JD-Core Version:    0.6.2
 */