/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.cubes.test.blocks.Block_Grass;
/*    */ import com.cubes.test.blocks.Block_Stone;
/*    */ import com.cubes.test.blocks.Block_Wood;
/*    */ import com.jme3.app.SimpleApplication;
/*    */ import com.jme3.input.FlyByCamera;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.renderer.Camera;
/*    */ import com.jme3.scene.Node;
/*    */ import com.jme3.system.AppSettings;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public class TestModifications extends SimpleApplication
/*    */ {
/*    */   private BlockTerrainControl blockTerrain;
/*    */   private long lastModificationTimestamp;
/* 28 */   private Vector3Int lastModificationLocation = new Vector3Int(0, 4, 15);
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 15 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 16 */     TestModifications app = new TestModifications();
/* 17 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestModifications() {
/* 21 */     this.settings = new AppSettings(true);
/* 22 */     this.settings.setWidth(1280);
/* 23 */     this.settings.setHeight(720);
/* 24 */     this.settings.setTitle("Cubes Demo - Modifications");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 32 */     CubesTestAssets.registerBlocks();
/*    */ 
/* 34 */     this.blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(2, 1, 2));
/* 35 */     for (int x = 0; x < 32; x++) {
/* 36 */       for (int z = 0; z < 16; z++) {
/* 37 */         int groundHeight = (int)(Math.random() * 4.0D + 8.0D);
/* 38 */         for (int y = 0; y < groundHeight; y++) {
/* 39 */           if ((z != 15) || (y != 4)) {
/* 40 */             this.blockTerrain.setBlock(x, y, z, Block_Stone.class);
/*    */           }
/*    */         }
/* 43 */         int additionalHeight = (int)(Math.random() * 4.0D);
/* 44 */         for (int y = 0; y < additionalHeight; y++) {
/* 45 */           Class blockClass = y > 0 ? Block_Grass.class : Block_Wood.class;
/* 46 */           this.blockTerrain.setBlock(x, groundHeight + y, z, blockClass);
/*    */         }
/*    */       }
/*    */     }
/* 50 */     Node terrainNode = new Node();
/* 51 */     terrainNode.addControl(this.blockTerrain);
/* 52 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 54 */     this.cam.setLocation(new Vector3f(-29.0F, 32.0F, 96.0F));
/* 55 */     this.cam.lookAtDirection(new Vector3f(0.68F, -0.175F, -0.71F), Vector3f.UNIT_Y);
/* 56 */     this.flyCam.setMoveSpeed(250.0F);
/*    */   }
/*    */ 
/*    */   public void simpleUpdate(float lastTimePerFrame)
/*    */   {
/* 61 */     if (System.currentTimeMillis() - this.lastModificationTimestamp > 50L) {
/* 62 */       this.blockTerrain.removeBlock(this.lastModificationLocation);
/* 63 */       this.lastModificationLocation.addLocal(1, 0, 0);
/* 64 */       if (this.lastModificationLocation.getX() > 31) {
/* 65 */         this.lastModificationLocation.setX(0);
/*    */       }
/* 67 */       this.blockTerrain.setBlock(this.lastModificationLocation, Block_Grass.class);
/* 68 */       this.lastModificationTimestamp = System.currentTimeMillis();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestModifications
 * JD-Core Version:    0.6.2
 */