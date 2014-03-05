/*    */ package com.cubes;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class BlockManager
/*    */ {
/* 15 */   private static HashMap<Class<? extends Block>, BlockType> BLOCK_TYPES = new HashMap();
/* 16 */   private static BlockType[] TYPES_BLOCKS = new BlockType[256];
/* 17 */   private static byte nextBlockType = 1;
/*    */ 
/*    */   public static void register(Class<? extends Block> blockClass, BlockSkin skin) {
/* 20 */     BlockType blockType = new BlockType(nextBlockType, skin);
/* 21 */     BLOCK_TYPES.put(blockClass, blockType);
/* 22 */     TYPES_BLOCKS[nextBlockType] = blockType;
/* 23 */     nextBlockType = (byte)(nextBlockType + 1);
/*    */   }
/*    */ 
/*    */   public static BlockType getType(Class<? extends Block> blockClass) {
/* 27 */     return (BlockType)BLOCK_TYPES.get(blockClass);
/*    */   }
/*    */ 
/*    */   public static Class<? extends Block> getClass(byte type) {
/* 31 */     return (Class)Util.getHashKeyByValue(BLOCK_TYPES, getType(type));
/*    */   }
/*    */ 
/*    */   public static BlockType getType(byte type) {
/* 35 */     return TYPES_BLOCKS[type];
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockManager
 * JD-Core Version:    0.6.2
 */