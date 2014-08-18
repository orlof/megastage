package com.cubes;

public class BlockType
{
  private byte type;
  private BlockSkin skin;

  public BlockType(byte type, BlockSkin skin)
  {
    this.type = type;
    this.skin = skin;
  }

  public byte getType()
  {
    return this.type;
  }

  public BlockSkin getSkin() {
    return this.skin;
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.BlockType
 * JD-Core Version:    0.6.2
 */