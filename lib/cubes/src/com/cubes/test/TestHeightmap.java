/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.cubes.test.blocks.Block_Grass;
/*    */ import com.cubes.test.blocks.Block_Stone;
/*    */ import com.jme3.app.SimpleApplication;
/*    */ import com.jme3.input.FlyByCamera;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.renderer.Camera;
/*    */ import com.jme3.scene.Node;
/*    */ import com.jme3.system.AppSettings;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public class TestHeightmap extends SimpleApplication
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 15 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 16 */     TestHeightmap app = new TestHeightmap();
/* 17 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestHeightmap() {
/* 21 */     this.settings = new AppSettings(true);
/* 22 */     this.settings.setWidth(1280);
/* 23 */     this.settings.setHeight(720);
/* 24 */     this.settings.setTitle("Cubes Demo - Heightmap");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 29 */     CubesTestAssets.registerBlocks();
/*    */ 
/* 31 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(4, 1, 4));
/* 32 */     blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(64, 1, 64), Block_Stone.class);
/* 33 */     blockTerrain.setBlocksFromHeightmap(new Vector3Int(0, 1, 0), "Textures/cubes/heightmap.jpg", 20, Block_Grass.class);
/* 34 */     Node terrainNode = new Node();
/* 35 */     terrainNode.addControl(blockTerrain);
/* 36 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 38 */     this.cam.setLocation(new Vector3f(-3.0F, 88.0F, 300.0F));
/* 39 */     this.cam.lookAtDirection(new Vector3f(0.44F, -0.35F, -0.83F), Vector3f.UNIT_Y);
/* 40 */     this.flyCam.setMoveSpeed(300.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestHeightmap
 * JD-Core Version:    0.6.2
 */