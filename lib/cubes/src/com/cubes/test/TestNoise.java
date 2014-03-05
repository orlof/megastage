/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.cubes.test.blocks.Block_Grass;
/*    */ import com.jme3.app.SimpleApplication;
/*    */ import com.jme3.input.FlyByCamera;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.renderer.Camera;
/*    */ import com.jme3.scene.Node;
/*    */ import com.jme3.system.AppSettings;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public class TestNoise extends SimpleApplication
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 15 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 16 */     TestNoise app = new TestNoise();
/* 17 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestNoise() {
/* 21 */     this.settings = new AppSettings(true);
/* 22 */     this.settings.setWidth(1280);
/* 23 */     this.settings.setHeight(720);
/* 24 */     this.settings.setTitle("Cubes Demo - Noise");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 29 */     CubesTestAssets.registerBlocks();
/*    */ 
/* 31 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(4, 1, 4));
/* 32 */     blockTerrain.setBlocksFromNoise(new Vector3Int(0, 0, 0), new Vector3Int(64, 50, 64), 0.3F, Block_Grass.class);
/* 33 */     Node terrainNode = new Node();
/* 34 */     terrainNode.addControl(blockTerrain);
/* 35 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 37 */     this.cam.setLocation(new Vector3f(-64.0F, 187.0F, -55.0F));
/* 38 */     this.cam.lookAtDirection(new Vector3f(0.64F, -0.45F, 0.6F), Vector3f.UNIT_Y);
/* 39 */     this.flyCam.setMoveSpeed(300.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestNoise
 * JD-Core Version:    0.6.2
 */