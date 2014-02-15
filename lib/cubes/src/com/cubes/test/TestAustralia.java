/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.cubes.test.blocks.Block_Grass;
/*    */ import com.jme3.app.SimpleApplication;
/*    */ import com.jme3.input.FlyByCamera;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.renderer.Camera;
/*    */ import com.jme3.renderer.queue.RenderQueue.ShadowMode;
/*    */ import com.jme3.scene.Node;
/*    */ import com.jme3.system.AppSettings;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public class TestAustralia extends SimpleApplication
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 16 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 17 */     TestAustralia app = new TestAustralia();
/* 18 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestAustralia() {
/* 22 */     this.settings = new AppSettings(true);
/* 23 */     this.settings.setWidth(1280);
/* 24 */     this.settings.setHeight(720);
/* 25 */     this.settings.setTitle("Cubes Demo - Heightmap (Australia)");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 30 */     CubesTestAssets.registerBlocks();
/* 31 */     CubesTestAssets.initializeEnvironment(this);
/* 32 */     CubesTestAssets.initializeWater(this);
/*    */ 
/* 34 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(7, 1, 7));
/* 35 */     blockTerrain.setBlocksFromHeightmap(new Vector3Int(0, 1, 0), "Textures/cubes/heightmap_australia.jpg", 10, Block_Grass.class);
/* 36 */     Node terrainNode = new Node();
/* 37 */     terrainNode.addControl(blockTerrain);
/* 38 */     terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
/* 39 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 41 */     this.cam.setLocation(new Vector3f(32.799999F, 111.0F, 379.5F));
/* 42 */     this.cam.lookAtDirection(new Vector3f(0.44F, -0.47F, -0.77F), Vector3f.UNIT_Y);
/* 43 */     this.cam.setFrustumFar(4000.0F);
/* 44 */     this.flyCam.setMoveSpeed(300.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestAustralia
 * JD-Core Version:    0.6.2
 */