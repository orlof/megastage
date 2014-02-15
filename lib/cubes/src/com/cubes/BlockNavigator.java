/*    */ package com.cubes;
/*    */ 
/*    */ import com.jme3.math.Vector3f;
/*    */ 
/*    */ public class BlockNavigator
/*    */ {
/*    */   public static Vector3Int getNeighborBlockLocalLocation(Vector3Int location, Block.Face face)
/*    */   {
/* 16 */     Vector3Int neighborLocation = getNeighborBlockLocation_Relative(face);
/* 17 */     neighborLocation.addLocal(location);
/* 18 */     return neighborLocation;
/*    */   }
/*    */ 
/*    */   public static Vector3Int getNeighborBlockLocation_Relative(Block.Face face) {
/* 22 */     Vector3Int neighborLocation = new Vector3Int();
/* 23 */     switch (1.$SwitchMap$com$cubes$Block$Face[face.ordinal()]) {
/*    */     case 1:
/* 25 */       neighborLocation.set(0, 1, 0);
/* 26 */       break;
/*    */     case 2:
/* 29 */       neighborLocation.set(0, -1, 0);
/* 30 */       break;
/*    */     case 3:
/* 33 */       neighborLocation.set(-1, 0, 0);
/* 34 */       break;
/*    */     case 4:
/* 37 */       neighborLocation.set(1, 0, 0);
/* 38 */       break;
/*    */     case 5:
/* 41 */       neighborLocation.set(0, 0, 1);
/* 42 */       break;
/*    */     case 6:
/* 45 */       neighborLocation.set(0, 0, -1);
/*    */     }
/*    */ 
/* 48 */     return neighborLocation;
/*    */   }
/*    */ 
/*    */   public static Vector3Int getPointedBlockLocation(BlockTerrainControl blockTerrain, Vector3f collisionContactPoint, boolean getNeighborLocation) {
/* 52 */     Vector3f collisionLocation = Util.compensateFloatRoundingErrors(collisionContactPoint);
/* 53 */     Vector3Int blockLocation = new Vector3Int((int)(collisionLocation.getX() / blockTerrain.getSettings().getBlockSize()), (int)(collisionLocation.getY() / blockTerrain.getSettings().getBlockSize()), (int)(collisionLocation.getZ() / blockTerrain.getSettings().getBlockSize()));
/*    */ 
/* 57 */     if ((blockTerrain.getBlock(blockLocation) != null) == getNeighborLocation) {
/* 58 */       if (collisionLocation.getX() % blockTerrain.getSettings().getBlockSize() == 0.0F) {
/* 59 */         blockLocation.subtractLocal(1, 0, 0);
/*    */       }
/* 61 */       else if (collisionLocation.getY() % blockTerrain.getSettings().getBlockSize() == 0.0F) {
/* 62 */         blockLocation.subtractLocal(0, 1, 0);
/*    */       }
/* 64 */       else if (collisionLocation.getZ() % blockTerrain.getSettings().getBlockSize() == 0.0F) {
/* 65 */         blockLocation.subtractLocal(0, 0, 1);
/*    */       }
/*    */     }
/* 68 */     return blockLocation;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockNavigator
 * JD-Core Version:    0.6.2
 */