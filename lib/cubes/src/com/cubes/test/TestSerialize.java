/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.cubes.network.CubesSerializer;
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
/*    */ public class TestSerialize extends SimpleApplication
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 16 */     Logger.getLogger("").setLevel(Level.SEVERE);
/* 17 */     TestSerialize app = new TestSerialize();
/* 18 */     app.start();
/*    */   }
/*    */ 
/*    */   public TestSerialize() {
/* 22 */     this.settings = new AppSettings(true);
/* 23 */     this.settings.setWidth(1280);
/* 24 */     this.settings.setHeight(720);
/* 25 */     this.settings.setTitle("Cubes Demo - Serialize");
/*    */   }
/*    */ 
/*    */   public void simpleInitApp()
/*    */   {
/* 30 */     CubesTestAssets.registerBlocks();
/*    */ 
/* 32 */     BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(1, 1, 1));
/* 33 */     blockTerrain.setBlocksFromNoise(new Vector3Int(0, 0, 0), new Vector3Int(16, 10, 16), 0.5F, Block_Grass.class);
/* 34 */     Node terrainNode = new Node();
/* 35 */     terrainNode.addControl(blockTerrain);
/* 36 */     terrainNode.setLocalTranslation(40.0F, 0.0F, 0.0F);
/* 37 */     this.rootNode.attachChild(terrainNode);
/*    */ 
/* 40 */     BlockTerrainControl blockTerrainClone = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int());
/*    */ 
/* 42 */     byte[] serializedBlockTerrain = CubesSerializer.writeToBytes(blockTerrain);
/* 43 */     CubesSerializer.readFromBytes(blockTerrainClone, serializedBlockTerrain);
/*    */ 
/* 45 */     Node terrainNodeClone = new Node();
/* 46 */     terrainNodeClone.addControl(blockTerrainClone);
/* 47 */     terrainNodeClone.setLocalTranslation(-40.0F, 0.0F, 0.0F);
/* 48 */     this.rootNode.attachChild(terrainNodeClone);
/*    */ 
/* 50 */     this.cam.setLocation(new Vector3f(23.5F, 46.0F, -103.0F));
/* 51 */     this.cam.lookAtDirection(new Vector3f(0.0F, -0.25F, 1.0F), Vector3f.UNIT_Y);
/* 52 */     this.flyCam.setMoveSpeed(300.0F);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestSerialize
 * JD-Core Version:    0.6.2
 */