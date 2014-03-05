/*    */ package com.cubes.models;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.CubesSettings;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.jme3.asset.AssetManager;
/*    */ import com.jme3.bounding.BoundingBox;
/*    */ import com.jme3.collision.CollisionResult;
/*    */ import com.jme3.collision.CollisionResults;
/*    */ import com.jme3.material.Material;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.scene.Geometry;
/*    */ import com.jme3.scene.Spatial;
/*    */ import com.jme3.scene.shape.Box;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class BlockModel
/*    */ {
/*    */   private String modelPath;
/*    */   private Class[] blockClasses;
/* 30 */   private int nextMaterialIndex = 0;
/* 31 */   private HashMap<Material, Class> materialBlocks = new HashMap();
/*    */ 
/*    */   public BlockModel(String modelPath, Class[] blockClasses)
/*    */   {
/* 25 */     this.modelPath = modelPath;
/* 26 */     this.blockClasses = blockClasses;
/*    */   }
/*    */ 
/*    */   public void addToBlockTerrain(BlockTerrainControl blockTerrain, Vector3Int location, Vector3Int size)
/*    */   {
/* 34 */     Spatial spatial = blockTerrain.getSettings().getAssetManager().loadModel(this.modelPath);
/* 35 */     Vector3f bounds = getBounds(spatial);
/* 36 */     Vector3f relativeBlockSize = new Vector3f(bounds.getX() / size.getX(), bounds.getY() / size.getY(), bounds.getZ() / size.getZ());
/* 37 */     Geometry testBlockBox = new Geometry("", new Box(relativeBlockSize.divide(2.0F), relativeBlockSize.getX(), relativeBlockSize.getY(), relativeBlockSize.getZ()));
/* 38 */     Vector3Int tmpLocation = new Vector3Int();
/* 39 */     for (int x = 0; x < size.getX(); x++)
/* 40 */       for (int y = 0; y < size.getY(); y++)
/* 41 */         for (int z = 0; z < size.getZ(); z++) {
/* 42 */           testBlockBox.setLocalTranslation(relativeBlockSize.getX() * x - bounds.getX() / 2.0F, relativeBlockSize.getY() * y, relativeBlockSize.getZ() * z - bounds.getZ() / 2.0F);
/*    */ 
/* 47 */           CollisionResults collisionResults = new CollisionResults();
/* 48 */           spatial.collideWith(testBlockBox.getWorldBound(), collisionResults);
/* 49 */           CollisionResult collisionResult = collisionResults.getClosestCollision();
/* 50 */           if (collisionResult != null) {
/* 51 */             tmpLocation.set(location).addLocal(x, y, z);
/* 52 */             Class blockClass = getMaterialBlockClass(collisionResult.getGeometry().getMaterial());
/* 53 */             blockTerrain.setBlock(tmpLocation, blockClass);
/*    */           }
/*    */         }
/*    */   }
/*    */ 
/*    */   private Class getMaterialBlockClass(Material material)
/*    */   {
/* 61 */     Class blockClass = (Class)this.materialBlocks.get(material);
/* 62 */     if (blockClass == null) {
/* 63 */       blockClass = this.blockClasses[this.nextMaterialIndex];
/* 64 */       if (this.nextMaterialIndex < this.blockClasses.length - 1) {
/* 65 */         this.nextMaterialIndex += 1;
/*    */       }
/* 67 */       this.materialBlocks.put(material, blockClass);
/*    */     }
/* 69 */     return blockClass;
/*    */   }
/*    */ 
/*    */   private static Vector3f getBounds(Spatial spatial) {
/* 73 */     if ((spatial.getWorldBound() instanceof BoundingBox)) {
/* 74 */       BoundingBox boundingBox = (BoundingBox)spatial.getWorldBound();
/* 75 */       return new Vector3f(2.0F * boundingBox.getXExtent(), 2.0F * boundingBox.getYExtent(), 2.0F * boundingBox.getZExtent());
/*    */     }
/* 77 */     return new Vector3f(0.0F, 0.0F, 0.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.models.BlockModel
 * JD-Core Version:    0.6.2
 */