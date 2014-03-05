/*    */ package com.cubes;
/*    */ 
/*    */ import com.jme3.asset.AssetManager;
/*    */ import com.jme3.material.Material;
/*    */ import com.jme3.material.RenderState;
/*    */ import com.jme3.material.RenderState.BlendMode;
/*    */ import com.jme3.texture.Texture;
/*    */ import com.jme3.texture.Texture.MagFilter;
/*    */ import com.jme3.texture.Texture.MinFilter;
/*    */ 
/*    */ public class BlockChunk_Material extends Material
/*    */ {
/*    */   public BlockChunk_Material(AssetManager assetManager, String blockTextureFilePath)
/*    */   {
/* 19 */     super(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
/* 20 */     Texture texture = assetManager.loadTexture(blockTextureFilePath);
/* 21 */     texture.setMagFilter(Texture.MagFilter.Nearest);
/* 22 */     texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
/* 23 */     setTexture("ColorMap", texture);
/* 24 */     getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockChunk_Material
 * JD-Core Version:    0.6.2
 */