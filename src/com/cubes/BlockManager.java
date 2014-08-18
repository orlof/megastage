package com.cubes;

import java.util.HashMap;

public class BlockManager
{
  private static HashMap<Class<? extends Block>, BlockType> BLOCK_TYPES = new HashMap();
  private static BlockType[] TYPES_BLOCKS = new BlockType[256];
  private static byte nextBlockType = 1;

  public static void register(Class<? extends Block> blockClass, BlockSkin skin) {
    BlockType blockType = new BlockType(nextBlockType, skin);
    BLOCK_TYPES.put(blockClass, blockType);
    TYPES_BLOCKS[nextBlockType] = blockType;
    nextBlockType = (byte)(nextBlockType + 1);
  }

  public static BlockType getType(Class<? extends Block> blockClass) {
    return (BlockType)BLOCK_TYPES.get(blockClass);
  }

  public static Class<? extends Block> getClass(byte type) {
    return (Class)Util.getHashKeyByValue(BLOCK_TYPES, getType(type));
  }

  public static BlockType getType(byte type) {
    return TYPES_BLOCKS[type];
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.BlockManager
 * JD-Core Version:    0.6.2
 */