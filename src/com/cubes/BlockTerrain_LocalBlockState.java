package com.cubes;

public class BlockTerrain_LocalBlockState
{
  private BlockChunkControl chunk;
  private Vector3Int localBlockLocation;

  public BlockTerrain_LocalBlockState(BlockChunkControl chunk, Vector3Int localBlockLocation)
  {
    this.chunk = chunk;
    this.localBlockLocation = localBlockLocation;
  }

  public BlockChunkControl getChunk()
  {
    return this.chunk;
  }

  public Vector3Int getLocalBlockLocation() {
    return this.localBlockLocation;
  }

  public BlockType getBlock() {
    return this.chunk.getBlock(this.localBlockLocation);
  }

  public void setBlock(Class<? extends Block> blockClass) {
    this.chunk.setBlock(this.localBlockLocation, blockClass);
  }

  public void removeBlock() {
    this.chunk.removeBlock(this.localBlockLocation);
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.BlockTerrain_LocalBlockState
 * JD-Core Version:    0.6.2
 */