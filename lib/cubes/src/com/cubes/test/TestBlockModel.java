/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.jme3.app.SimpleApplication;
/*    */ import com.jme3.input.FlyByCamera;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.renderer.Camera;
/*    */ import com.jme3.scene.Node;
/*    */ import com.jme3.system.AppSettings;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public class TestBlockModel extends SimpleApplication
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 16 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 17 */     TestBlockModel app = new TestBlockModel();
/* 18 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestBlockModel() {
/* 22 */     this.settings = new AppSettings(true);
/* 23 */     this.settings.setWidth(1280);
/* 24 */     this.settings.setHeight(720);
/* 25 */     this.settings.setTitle("Cubes Demo - BlockModel");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 30 */     CubesTestAssets.registerBlocks();
/*    */ 
/* 32 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(11, 1, 10));
/*    */ 
/* 36 */     Node terrainNode = new Node();
/* 37 */     terrainNode.addControl(blockTerrain);
/* 38 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 40 */     this.cam.setLocation(new Vector3f(-3.0F, 88.0F, 100.0F));
/* 41 */     this.cam.lookAtDirection(new Vector3f(0.44F, -0.35F, -0.83F), Vector3f.UNIT_Y);
/* 42 */     this.flyCam.setMoveSpeed(300.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestBlockModel
 * JD-Core Version:    0.6.2
 */