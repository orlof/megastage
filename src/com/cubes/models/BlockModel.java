package com.cubes.models;

import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.HashMap;

public class BlockModel
{
  private String modelPath;
  private Class[] blockClasses;
  private int nextMaterialIndex = 0;
  private HashMap<Material, Class> materialBlocks = new HashMap();

  public BlockModel(String modelPath, Class[] blockClasses)
  {
    this.modelPath = modelPath;
    this.blockClasses = blockClasses;
  }

  public void addToBlockTerrain(BlockTerrainControl blockTerrain, Vector3Int location, Vector3Int size)
  {
    Spatial spatial = blockTerrain.getSettings().getAssetManager().loadModel(this.modelPath);
    Vector3f bounds = getBounds(spatial);
    Vector3f relativeBlockSize = new Vector3f(bounds.getX() / size.getX(), bounds.getY() / size.getY(), bounds.getZ() / size.getZ());
    Geometry testBlockBox = new Geometry("", new Box(relativeBlockSize.divide(2.0F), relativeBlockSize.getX(), relativeBlockSize.getY(), relativeBlockSize.getZ()));
    Vector3Int tmpLocation = new Vector3Int();
    for (int x = 0; x < size.getX(); x++)
      for (int y = 0; y < size.getY(); y++)
        for (int z = 0; z < size.getZ(); z++) {
          testBlockBox.setLocalTranslation(relativeBlockSize.getX() * x - bounds.getX() / 2.0F, relativeBlockSize.getY() * y, relativeBlockSize.getZ() * z - bounds.getZ() / 2.0F);

          CollisionResults collisionResults = new CollisionResults();
          spatial.collideWith(testBlockBox.getWorldBound(), collisionResults);
          CollisionResult collisionResult = collisionResults.getClosestCollision();
          if (collisionResult != null) {
            tmpLocation.set(location).addLocal(x, y, z);
            Class blockClass = getMaterialBlockClass(collisionResult.getGeometry().getMaterial());
            blockTerrain.setBlock(tmpLocation, blockClass);
          }
        }
  }

  private Class getMaterialBlockClass(Material material)
  {
    Class blockClass = (Class)this.materialBlocks.get(material);
    if (blockClass == null) {
      blockClass = this.blockClasses[this.nextMaterialIndex];
      if (this.nextMaterialIndex < this.blockClasses.length - 1) {
        this.nextMaterialIndex += 1;
      }
      this.materialBlocks.put(material, blockClass);
    }
    return blockClass;
  }

  private static Vector3f getBounds(Spatial spatial) {
    if ((spatial.getWorldBound() instanceof BoundingBox)) {
      BoundingBox boundingBox = (BoundingBox)spatial.getWorldBound();
      return new Vector3f(2.0F * boundingBox.getXExtent(), 2.0F * boundingBox.getYExtent(), 2.0F * boundingBox.getZExtent());
    }
    return new Vector3f(0.0F, 0.0F, 0.0F);
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.models.BlockModel
 * JD-Core Version:    0.6.2
 */