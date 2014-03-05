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
/*    */ public class TestTutorial extends SimpleApplication
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 15 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 16 */     TestTutorial app = new TestTutorial();
/* 17 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestTutorial() {
/* 21 */     this.settings = new AppSettings(true);
/* 22 */     this.settings.setWidth(1280);
/* 23 */     this.settings.setHeight(720);
/* 24 */     this.settings.setTitle("Cubes Demo - Tutorial");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 29 */     CubesTestAssets.registerBlocks();
/*    */ 
/* 33 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(1, 1, 1));
/*    */ 
/* 37 */     blockTerrain.setBlock(new Vector3Int(0, 0, 0), Block_Wood.class);
/* 38 */     blockTerrain.setBlock(new Vector3Int(0, 0, 1), Block_Wood.class);
/* 39 */     blockTerrain.setBlock(new Vector3Int(1, 0, 0), Block_Wood.class);
/* 40 */     blockTerrain.setBlock(new Vector3Int(1, 0, 1), Block_Stone.class);
/* 41 */     blockTerrain.setBlock(0, 0, 0, Block_Grass.class);
/*    */ 
/* 47 */     blockTerrain.setBlockArea(new Vector3Int(1, 1, 1), new Vector3Int(1, 3, 1), Block_Stone.class);
/*    */ 
/* 50 */     blockTerrain.removeBlock(new Vector3Int(1, 2, 1));
/* 51 */     blockTerrain.removeBlock(new Vector3Int(1, 3, 1));
/*    */ 
/* 55 */     Node terrainNode = new Node();
/* 56 */     terrainNode.addControl(blockTerrain);
/* 57 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 59 */     this.cam.setLocation(new Vector3f(-10.0F, 10.0F, 16.0F));
/* 60 */     this.cam.lookAtDirection(new Vector3f(1.0F, -0.56F, -1.0F), Vector3f.UNIT_Y);
/* 61 */     this.flyCam.setMoveSpeed(50.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestTutorial
 * JD-Core Version:    0.6.2
 */