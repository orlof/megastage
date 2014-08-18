package com.cubes;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

public class BlockChunkControl extends AbstractControl
{
  private BlockTerrainControl terrain;
  private Vector3Int location = new Vector3Int();
  private Vector3Int blockLocation = new Vector3Int();
  private byte[][][] blockTypes;
  private boolean[][][] blocks_IsOnSurface;
  private Node node = new Node();
  private Geometry optimizedGeometry_Opaque;
  private Geometry optimizedGeometry_Transparent;
  private boolean needsMeshUpdate;

  public BlockChunkControl(BlockTerrainControl terrain, int x, int y, int z)
  {
    this.terrain = terrain;
    this.location.set(x, y, z);
    this.blockLocation.set(this.location.mult(terrain.getSettings().getChunkSizeX(), terrain.getSettings().getChunkSizeY(), terrain.getSettings().getChunkSizeZ()));
    this.node.setLocalTranslation(new Vector3f(this.blockLocation.getX(), this.blockLocation.getY(), this.blockLocation.getZ()).mult(terrain.getSettings().getBlockSize()));
    this.blockTypes = new byte[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
    this.blocks_IsOnSurface = new boolean[terrain.getSettings().getChunkSizeX()][terrain.getSettings().getChunkSizeY()][terrain.getSettings().getChunkSizeZ()];
  }

  public void setSpatial(Spatial spatial)
  {
    Spatial oldSpatial = this.spatial;
    super.setSpatial(spatial);
    if ((spatial instanceof Node)) {
      Node parentNode = (Node)spatial;
      parentNode.attachChild(this.node);
    }
    else if ((oldSpatial instanceof Node)) {
      Node oldNode = (Node)oldSpatial;
      oldNode.detachChild(this.node);
    }
  }

  protected void controlUpdate(float lastTimePerFrame)
  {
  }

  protected void controlRender(RenderManager renderManager, ViewPort viewPort)
  {
  }

  public Control cloneForSpatial(Spatial spatial)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public BlockType getNeighborBlock_Local(Vector3Int location, Block.Face face) {
    Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
    return getBlock(neighborLocation);
  }

  public BlockType getNeighborBlock_Global(Vector3Int location, Block.Face face) {
    return this.terrain.getBlock(getNeighborBlockGlobalLocation(location, face));
  }

  private Vector3Int getNeighborBlockGlobalLocation(Vector3Int location, Block.Face face) {
    Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
    neighborLocation.addLocal(this.blockLocation);
    return neighborLocation;
  }

  public BlockType getBlock(Vector3Int location) {
    if (isValidBlockLocation(location)) {
      byte blockType = this.blockTypes[location.getX()][location.getY()][location.getZ()];
      return BlockManager.getType(blockType);
    }
    return null;
  }

  public void setBlock(Vector3Int location, Class<? extends Block> blockClass) {
    if (isValidBlockLocation(location)) {
      BlockType blockType = BlockManager.getType(blockClass);
      this.blockTypes[location.getX()][location.getY()][location.getZ()] = blockType.getType();
      updateBlockState(location);
      this.needsMeshUpdate = true;
    }
  }

  public void removeBlock(Vector3Int location) {
    if (isValidBlockLocation(location)) {
      this.blockTypes[location.getX()][location.getY()][location.getZ()] = 0;
      updateBlockState(location);
      this.needsMeshUpdate = true;
    }
  }

  private boolean isValidBlockLocation(Vector3Int location) {
    return Util.isValidIndex(this.blockTypes, location);
  }

  public boolean updateSpatial() {
    if (this.needsMeshUpdate) {
      if (this.optimizedGeometry_Opaque == null) {
        this.optimizedGeometry_Opaque = new Geometry("");
        this.optimizedGeometry_Opaque.setQueueBucket(RenderQueue.Bucket.Opaque);
        this.node.attachChild(this.optimizedGeometry_Opaque);
        updateBlockMaterial();
      }
      if (this.optimizedGeometry_Transparent == null) {
        this.optimizedGeometry_Transparent = new Geometry("");
        this.optimizedGeometry_Transparent.setQueueBucket(RenderQueue.Bucket.Transparent);
        this.node.attachChild(this.optimizedGeometry_Transparent);
        updateBlockMaterial();
      }
      this.optimizedGeometry_Opaque.setMesh(BlockChunk_MeshOptimizer.generateOptimizedMesh(this, BlockChunk_TransparencyMerger.OPAQUE));
      this.optimizedGeometry_Transparent.setMesh(BlockChunk_MeshOptimizer.generateOptimizedMesh(this, BlockChunk_TransparencyMerger.TRANSPARENT));
      this.needsMeshUpdate = false;
      return true;
    }
    return false;
  }

  public void updateBlockMaterial() {
    if (this.optimizedGeometry_Opaque != null) {
      this.optimizedGeometry_Opaque.setMaterial(this.terrain.getSettings().getBlockMaterial());
    }
    if (this.optimizedGeometry_Transparent != null)
      this.optimizedGeometry_Transparent.setMaterial(this.terrain.getSettings().getBlockMaterial());
  }

  private void updateBlockState(Vector3Int location)
  {
    updateBlockInformation(location);
    for (int i = 0; i < Block.Face.values().length; i++) {
      Vector3Int neighborLocation = getNeighborBlockGlobalLocation(location, Block.Face.values()[i]);
      BlockChunkControl chunk = this.terrain.getChunk(neighborLocation);
      if (chunk != null)
        chunk.updateBlockInformation(neighborLocation.subtract(chunk.getBlockLocation()));
    }
  }

  private void updateBlockInformation(Vector3Int location)
  {
    BlockType neighborBlock_Top = this.terrain.getBlock(getNeighborBlockGlobalLocation(location, Block.Face.Top));
    this.blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()] = neighborBlock_Top == null;
  }

  public boolean isBlockOnSurface(Vector3Int location) {
    return this.blocks_IsOnSurface[location.getX()][location.getY()][location.getZ()];
  }

  public BlockTerrainControl getTerrain() {
    return this.terrain;
  }

  public Vector3Int getLocation() {
    return this.location;
  }

  public Vector3Int getBlockLocation() {
    return this.blockLocation;
  }

  public Node getNode() {
    return this.node;
  }

  public Geometry getOptimizedGeometry_Opaque() {
    return this.optimizedGeometry_Opaque;
  }

  public Geometry getOptimizedGeometry_Transparent() {
    return this.optimizedGeometry_Transparent;
  }

  private Vector3Int getNeededBlockChunks(Vector3Int blocksCount) {
    int chunksCountX = (int)Math.ceil(blocksCount.getX() / this.terrain.getSettings().getChunkSizeX());
    int chunksCountY = (int)Math.ceil(blocksCount.getY() / this.terrain.getSettings().getChunkSizeY());
    int chunksCountZ = (int)Math.ceil(blocksCount.getZ() / this.terrain.getSettings().getChunkSizeZ());
    return new Vector3Int(chunksCountX, chunksCountY, chunksCountZ);
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.BlockChunkControl
 * JD-Core Version:    0.6.2
 */