package com.cubes;

public class BlockChunk_TransparencyMerger
  implements BlockChunk_MeshMerger
{
  public static final BlockChunk_TransparencyMerger OPAQUE = new BlockChunk_TransparencyMerger(false);
  public static final BlockChunk_TransparencyMerger TRANSPARENT = new BlockChunk_TransparencyMerger(true);
  private boolean isGeometryTransparent;

  private BlockChunk_TransparencyMerger(boolean isGeometryTransparent)
  {
    this.isGeometryTransparent = isGeometryTransparent;
  }

  public boolean shouldFaceBeAdded(BlockChunkControl chunk, Vector3Int location, Block.Face face)
  {
    BlockType block = chunk.getBlock(location);
    if (block.getSkin().isTransparent() == this.isGeometryTransparent) {
      BlockType neighborBlock = chunk.getNeighborBlock_Local(location, face);
      if (neighborBlock != null) {
        if (block.getSkin().isTransparent() != neighborBlock.getSkin().isTransparent()) {
          return true;
        }
        return false;
      }
      return true;
    }
    return false;
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.BlockChunk_TransparencyMerger
 * JD-Core Version:    0.6.2
 */