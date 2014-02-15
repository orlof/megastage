/*    */ package com.cubes.test;
/*    */ 
/*    */ import com.cubes.Block.Face;
/*    */ import com.cubes.BlockChunkControl;
/*    */ import com.cubes.BlockManager;
/*    */ import com.cubes.BlockSkin;
/*    */ import com.cubes.BlockSkin_TextureLocation;
/*    */ import com.cubes.CubesSettings;
/*    */ import com.cubes.Vector3Int;
/*    */ import com.cubes.test.blocks.Block_Brick;
/*    */ import com.cubes.test.blocks.Block_Grass;
/*    */ import com.cubes.test.blocks.Block_Stone;
/*    */ import com.cubes.test.blocks.Block_Water;
/*    */ import com.cubes.test.blocks.Block_Wood;
/*    */ import com.jme3.app.Application;
/*    */ import com.jme3.app.SimpleApplication;
/*    */ import com.jme3.light.DirectionalLight;
/*    */ import com.jme3.math.ColorRGBA;
/*    */ import com.jme3.math.Vector3f;
/*    */ import com.jme3.post.FilterPostProcessor;
/*    */ import com.jme3.post.SceneProcessor;
/*    */ import com.jme3.renderer.ViewPort;
/*    */ import com.jme3.scene.Node;
/*    */ import com.jme3.shadow.PssmShadowRenderer;
/*    */ import com.jme3.util.SkyFactory;
/*    */ import com.jme3.water.WaterFilter;
/*    */ import java.util.List;
/*    */ 
/*    */ public class CubesTestAssets
/*    */ {
/* 27 */   private static final Vector3f lightDirection = new Vector3f(-0.8F, -1.0F, -0.8F).normalizeLocal();
/*    */ 
/*    */   public static CubesSettings getSettings(Application application) {
/* 30 */     CubesSettings settings = new CubesSettings(application);
/* 31 */     settings.setDefaultBlockMaterial("Textures/cubes/terrain.png");
/* 32 */     return settings;
/*    */   }
/*    */ 
/*    */   public static void registerBlocks() {
/* 36 */     BlockManager.register(Block_Grass.class, new BlockSkin(new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation(0, 0), new BlockSkin_TextureLocation(1, 0), new BlockSkin_TextureLocation(2, 0) }, false)
/*    */     {
/*    */       protected int getTextureLocationIndex(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face)
/*    */       {
/* 44 */         if (chunk.isBlockOnSurface(blockLocation)) {
/* 45 */           switch (CubesTestAssets.2.$SwitchMap$com$cubes$Block$Face[face.ordinal()]) {
/*    */           case 1:
/* 47 */             return 0;
/*    */           case 2:
/* 50 */             return 2;
/*    */           }
/* 52 */           return 1;
/*    */         }
/* 54 */         return 2;
/*    */       }
/*    */     });
/* 57 */     BlockManager.register(Block_Wood.class, new BlockSkin(new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation(4, 0), new BlockSkin_TextureLocation(4, 0), new BlockSkin_TextureLocation(3, 0), new BlockSkin_TextureLocation(3, 0), new BlockSkin_TextureLocation(3, 0), new BlockSkin_TextureLocation(3, 0) }, false));
/*    */ 
/* 65 */     BlockManager.register(Block_Stone.class, new BlockSkin(new BlockSkin_TextureLocation(9, 0), false));
/* 66 */     BlockManager.register(Block_Water.class, new BlockSkin(new BlockSkin_TextureLocation(0, 1), true));
/* 67 */     BlockManager.register(Block_Brick.class, new BlockSkin(new BlockSkin_TextureLocation(11, 0), false));
/*    */   }
/*    */ 
/*    */   public static void initializeEnvironment(SimpleApplication simpleApplication) {
/* 71 */     DirectionalLight directionalLight = new DirectionalLight();
/* 72 */     directionalLight.setDirection(lightDirection);
/* 73 */     directionalLight.setColor(new ColorRGBA(1.0F, 1.0F, 1.0F, 1.0F));
/* 74 */     simpleApplication.getRootNode().addLight(directionalLight);
/* 75 */     simpleApplication.getRootNode().attachChild(SkyFactory.createSky(simpleApplication.getAssetManager(), "Textures/cubes/sky.jpg", true));
/*    */ 
/* 77 */     PssmShadowRenderer pssmShadowRenderer = new PssmShadowRenderer(simpleApplication.getAssetManager(), 2048, 3);
/* 78 */     pssmShadowRenderer.setDirection(lightDirection);
/* 79 */     pssmShadowRenderer.setShadowIntensity(0.3F);
/* 80 */     simpleApplication.getViewPort().addProcessor(pssmShadowRenderer);
/*    */   }
/*    */ 
/*    */   public static void initializeWater(SimpleApplication simpleApplication) {
/* 84 */     WaterFilter waterFilter = new WaterFilter(simpleApplication.getRootNode(), lightDirection);
/* 85 */     getFilterPostProcessor(simpleApplication).addFilter(waterFilter);
/*    */   }
/*    */ 
/*    */   private static FilterPostProcessor getFilterPostProcessor(SimpleApplication simpleApplication) {
/* 89 */     List sceneProcessors = simpleApplication.getViewPort().getProcessors();
/* 90 */     for (int i = 0; i < sceneProcessors.size(); i++) {
/* 91 */       SceneProcessor sceneProcessor = (SceneProcessor)sceneProcessors.get(i);
/* 92 */       if ((sceneProcessor instanceof FilterPostProcessor)) {
/* 93 */         return (FilterPostProcessor)sceneProcessor;
/*    */       }
/*    */     }
/* 96 */     FilterPostProcessor filterPostProcessor = new FilterPostProcessor(simpleApplication.getAssetManager());
/* 97 */     simpleApplication.getViewPort().addProcessor(filterPostProcessor);
/* 98 */     return filterPostProcessor;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.CubesTestAssets
 * JD-Core Version:    0.6.2
 */