package com.cubes;

public class Block
{
  private BlockType type;

  public Block()
  {
    this.type = BlockManager.getType(getClass());
  }

  public BlockType getType()
  {
    return this.type;
  }

  public static enum Face
  {
    Top, Bottom, Left, Right, Front, Back;
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.Block
 * JD-Core Version:    0.6.2
 */