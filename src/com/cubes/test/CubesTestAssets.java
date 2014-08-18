package com.cubes.test;

import com.cubes.Block;
import com.cubes.Block.Face;
import com.cubes.BlockChunkControl;
import com.cubes.BlockManager;
import com.cubes.BlockSkin;
import com.cubes.BlockSkin_TextureLocation;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.blocks.Block_Brick;
import com.cubes.test.blocks.Block_Grass;
import com.cubes.test.blocks.Block_Stone;
import com.cubes.test.blocks.Block_Water;
import com.cubes.test.blocks.Block_Wood;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import java.util.List;

public class CubesTestAssets
{
  private static final Vector3f lightDirection = new Vector3f(-0.8F, -1.0F, -0.8F).normalizeLocal();

  public static CubesSettings getSettings(Application application) {
    CubesSettings settings = new CubesSettings(application);
    settings.setDefaultBlockMaterial("Textures/cubes/terrain.png");
    return settings;
  }

  public static void registerBlocks() {
    BlockManager.register(Block_Grass.class, new BlockSkin(new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation(0, 0), new BlockSkin_TextureLocation(1, 0), new BlockSkin_TextureLocation(2, 0) }, false)
    {
      protected int getTextureLocationIndex(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face)
      {
        if (chunk.isBlockOnSurface(blockLocation)) {
          switch (face.ordinal()) {
          case 1:
            return 0;
          case 2:
            return 2;
          }
          return 1;
        }
        return 2;
      }
    });
    BlockManager.register(Block_Wood.class, new BlockSkin(new BlockSkin_TextureLocation[] { new BlockSkin_TextureLocation(4, 0), new BlockSkin_TextureLocation(4, 0), new BlockSkin_TextureLocation(3, 0), new BlockSkin_TextureLocation(3, 0), new BlockSkin_TextureLocation(3, 0), new BlockSkin_TextureLocation(3, 0) }, false));

    BlockManager.register(Block_Stone.class, new BlockSkin(new BlockSkin_TextureLocation(9, 0), false));
    BlockManager.register(Block_Water.class, new BlockSkin(new BlockSkin_TextureLocation(0, 1), true));
    BlockManager.register(Block_Brick.class, new BlockSkin(new BlockSkin_TextureLocation(11, 0), false));
  }

  public static void initializeEnvironment(SimpleApplication simpleApplication) {
    DirectionalLight directionalLight = new DirectionalLight();
    directionalLight.setDirection(lightDirection);
    directionalLight.setColor(new ColorRGBA(1.0F, 1.0F, 1.0F, 1.0F));
    simpleApplication.getRootNode().addLight(directionalLight);
    simpleApplication.getRootNode().attachChild(SkyFactory.createSky(simpleApplication.getAssetManager(), "Textures/cubes/sky.jpg", true));

    DirectionalLightShadowRenderer directionalLightShadowRenderer = new DirectionalLightShadowRenderer(simpleApplication.getAssetManager(), 2048, 3);
    directionalLightShadowRenderer.setLight(directionalLight);
    directionalLightShadowRenderer.setShadowIntensity(0.3F);
    simpleApplication.getViewPort().addProcessor(directionalLightShadowRenderer);
  }

  public static void initializeWater(SimpleApplication simpleApplication) {
    WaterFilter waterFilter = new WaterFilter(simpleApplication.getRootNode(), lightDirection);
    getFilterPostProcessor(simpleApplication).addFilter(waterFilter);
  }

  private static FilterPostProcessor getFilterPostProcessor(SimpleApplication simpleApplication) {
    List sceneProcessors = simpleApplication.getViewPort().getProcessors();
    for (int i = 0; i < sceneProcessors.size(); i++) {
      SceneProcessor sceneProcessor = (SceneProcessor)sceneProcessors.get(i);
      if ((sceneProcessor instanceof FilterPostProcessor)) {
        return (FilterPostProcessor)sceneProcessor;
      }
    }
    FilterPostProcessor filterPostProcessor = new FilterPostProcessor(simpleApplication.getAssetManager());
    simpleApplication.getViewPort().addProcessor(filterPostProcessor);
    return filterPostProcessor;
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.test.CubesTestAssets
 * JD-Core Version:    0.6.2
 */