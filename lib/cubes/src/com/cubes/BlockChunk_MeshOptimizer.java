/*     */ package com.cubes;
/*     */ 
/*     */ import com.jme3.math.Vector2f;
/*     */ import com.jme3.math.Vector3f;
/*     */ import com.jme3.scene.Mesh;
/*     */ import com.jme3.scene.VertexBuffer.Type;
/*     */ import com.jme3.util.BufferUtils;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class BlockChunk_MeshOptimizer
/*     */ {
/*     */   private static Vector3f[] vertices;
/*     */   private static Vector2f[] textureCoordinates;
/*     */   private static int[] indices;
/*     */ 
/*     */   public static Mesh generateOptimizedMesh(BlockChunkControl blockChunk, BlockChunk_MeshMerger meshMerger)
/*     */   {
/*  25 */     loadMeshData(blockChunk, meshMerger);
/*  26 */     return generateMesh();
/*     */   }
/*     */ 
/*     */   private static void loadMeshData(BlockChunkControl chunk, BlockChunk_MeshMerger meshMerger) {
/*  30 */     ArrayList verticeList = new ArrayList();
/*  31 */     ArrayList textureCoordinateList = new ArrayList();
/*  32 */     ArrayList indicesList = new ArrayList();
/*  33 */     BlockTerrainControl blockTerrain = chunk.getTerrain();
/*  34 */     Vector3Int tmpLocation = new Vector3Int();
/*  35 */     for (int x = 0; x < blockTerrain.getSettings().getChunkSizeX(); x++) {
/*  36 */       for (int y = 0; y < blockTerrain.getSettings().getChunkSizeY(); y++) {
/*  37 */         for (int z = 0; z < blockTerrain.getSettings().getChunkSizeZ(); z++) {
/*  38 */           tmpLocation.set(x, y, z);
/*  39 */           BlockType block = chunk.getBlock(tmpLocation);
/*  40 */           if (block != null) {
/*  41 */             BlockSkin blockSkin = block.getSkin();
/*  42 */             Vector3f blockLocation = new Vector3f(x, y, z);
/*     */ 
/*  44 */             Vector3f faceLoc_Bottom_TopLeft = blockLocation.add(new Vector3f(0.0F, 0.0F, 0.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  45 */             Vector3f faceLoc_Bottom_TopRight = blockLocation.add(new Vector3f(1.0F, 0.0F, 0.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  46 */             Vector3f faceLoc_Bottom_BottomLeft = blockLocation.add(new Vector3f(0.0F, 0.0F, 1.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  47 */             Vector3f faceLoc_Bottom_BottomRight = blockLocation.add(new Vector3f(1.0F, 0.0F, 1.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  48 */             Vector3f faceLoc_Top_TopLeft = blockLocation.add(new Vector3f(0.0F, 1.0F, 0.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  49 */             Vector3f faceLoc_Top_TopRight = blockLocation.add(new Vector3f(1.0F, 1.0F, 0.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  50 */             Vector3f faceLoc_Top_BottomLeft = blockLocation.add(new Vector3f(0.0F, 1.0F, 1.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*  51 */             Vector3f faceLoc_Top_BottomRight = blockLocation.add(new Vector3f(1.0F, 1.0F, 1.0F)).mult(blockTerrain.getSettings().getBlockSize());
/*     */ 
/*  53 */             if (meshMerger.shouldFaceBeAdded(chunk, tmpLocation, Block.Face.Top)) {
/*  54 */               addVerticeIndexes(verticeList, indicesList);
/*  55 */               verticeList.add(faceLoc_Top_BottomLeft);
/*  56 */               verticeList.add(faceLoc_Top_BottomRight);
/*  57 */               verticeList.add(faceLoc_Top_TopLeft);
/*  58 */               verticeList.add(faceLoc_Top_TopRight);
/*  59 */               addBlockTextureCoordinates(textureCoordinateList, blockSkin.getTextureLocation(chunk, tmpLocation, Block.Face.Top));
/*     */             }
/*  61 */             if (meshMerger.shouldFaceBeAdded(chunk, tmpLocation, Block.Face.Bottom)) {
/*  62 */               addVerticeIndexes(verticeList, indicesList);
/*  63 */               verticeList.add(faceLoc_Bottom_BottomRight);
/*  64 */               verticeList.add(faceLoc_Bottom_BottomLeft);
/*  65 */               verticeList.add(faceLoc_Bottom_TopRight);
/*  66 */               verticeList.add(faceLoc_Bottom_TopLeft);
/*  67 */               addBlockTextureCoordinates(textureCoordinateList, blockSkin.getTextureLocation(chunk, tmpLocation, Block.Face.Bottom));
/*     */             }
/*  69 */             if (meshMerger.shouldFaceBeAdded(chunk, tmpLocation, Block.Face.Left)) {
/*  70 */               addVerticeIndexes(verticeList, indicesList);
/*  71 */               verticeList.add(faceLoc_Bottom_TopLeft);
/*  72 */               verticeList.add(faceLoc_Bottom_BottomLeft);
/*  73 */               verticeList.add(faceLoc_Top_TopLeft);
/*  74 */               verticeList.add(faceLoc_Top_BottomLeft);
/*  75 */               addBlockTextureCoordinates(textureCoordinateList, blockSkin.getTextureLocation(chunk, tmpLocation, Block.Face.Left));
/*     */             }
/*  77 */             if (meshMerger.shouldFaceBeAdded(chunk, tmpLocation, Block.Face.Right)) {
/*  78 */               addVerticeIndexes(verticeList, indicesList);
/*  79 */               verticeList.add(faceLoc_Bottom_BottomRight);
/*  80 */               verticeList.add(faceLoc_Bottom_TopRight);
/*  81 */               verticeList.add(faceLoc_Top_BottomRight);
/*  82 */               verticeList.add(faceLoc_Top_TopRight);
/*  83 */               addBlockTextureCoordinates(textureCoordinateList, blockSkin.getTextureLocation(chunk, tmpLocation, Block.Face.Right));
/*     */             }
/*  85 */             if (meshMerger.shouldFaceBeAdded(chunk, tmpLocation, Block.Face.Front)) {
/*  86 */               addVerticeIndexes(verticeList, indicesList);
/*  87 */               verticeList.add(faceLoc_Bottom_BottomLeft);
/*  88 */               verticeList.add(faceLoc_Bottom_BottomRight);
/*  89 */               verticeList.add(faceLoc_Top_BottomLeft);
/*  90 */               verticeList.add(faceLoc_Top_BottomRight);
/*  91 */               addBlockTextureCoordinates(textureCoordinateList, blockSkin.getTextureLocation(chunk, tmpLocation, Block.Face.Front));
/*     */             }
/*  93 */             if (meshMerger.shouldFaceBeAdded(chunk, tmpLocation, Block.Face.Back)) {
/*  94 */               addVerticeIndexes(verticeList, indicesList);
/*  95 */               verticeList.add(faceLoc_Bottom_TopRight);
/*  96 */               verticeList.add(faceLoc_Bottom_TopLeft);
/*  97 */               verticeList.add(faceLoc_Top_TopRight);
/*  98 */               verticeList.add(faceLoc_Top_TopLeft);
/*  99 */               addBlockTextureCoordinates(textureCoordinateList, blockSkin.getTextureLocation(chunk, tmpLocation, Block.Face.Back));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 106 */     vertices = new Vector3f[verticeList.size()];
/* 107 */     for (int i = 0; i < verticeList.size(); i++) {
/* 108 */       vertices[i] = ((Vector3f)verticeList.get(i));
/*     */     }
/* 110 */     textureCoordinates = new Vector2f[textureCoordinateList.size()];
/* 111 */     for (int i = 0; i < textureCoordinateList.size(); i++) {
/* 112 */       textureCoordinates[i] = ((Vector2f)textureCoordinateList.get(i));
/*     */     }
/* 114 */     indices = new int[indicesList.size()];
/* 115 */     for (int i = 0; i < indicesList.size(); i++)
/* 116 */       indices[i] = ((Integer)indicesList.get(i)).intValue();
/*     */   }
/*     */ 
/*     */   private static void addBlockTextureCoordinates(ArrayList<Vector2f> textureCoordinatesList, BlockSkin_TextureLocation textureLocation)
/*     */   {
/* 121 */     textureCoordinatesList.add(getTextureCoordinates(textureLocation, 0, 0));
/* 122 */     textureCoordinatesList.add(getTextureCoordinates(textureLocation, 1, 0));
/* 123 */     textureCoordinatesList.add(getTextureCoordinates(textureLocation, 0, 1));
/* 124 */     textureCoordinatesList.add(getTextureCoordinates(textureLocation, 1, 1));
/*     */   }
/*     */ 
/*     */   private static Vector2f getTextureCoordinates(BlockSkin_TextureLocation textureLocation, int xUnitsToAdd, int yUnitsToAdd) {
/* 128 */     float textureCount = 16.0F;
/* 129 */     float textureUnit = 1.0F / textureCount;
/* 130 */     float x = (textureLocation.getColumn() + xUnitsToAdd) * textureUnit;
/* 131 */     float y = (-1 * textureLocation.getRow() + (yUnitsToAdd - 1)) * textureUnit + 1.0F;
/* 132 */     return new Vector2f(x, y);
/*     */   }
/*     */ 
/*     */   private static void addVerticeIndexes(ArrayList<Vector3f> verticeList, ArrayList<Integer> indexesList) {
/* 136 */     int verticesCount = verticeList.size();
/* 137 */     indexesList.add(Integer.valueOf(verticesCount + 2));
/* 138 */     indexesList.add(Integer.valueOf(verticesCount + 0));
/* 139 */     indexesList.add(Integer.valueOf(verticesCount + 1));
/* 140 */     indexesList.add(Integer.valueOf(verticesCount + 1));
/* 141 */     indexesList.add(Integer.valueOf(verticesCount + 3));
/* 142 */     indexesList.add(Integer.valueOf(verticesCount + 2));
/*     */   }
/*     */ 
/*     */   private static Mesh generateMesh() {
/* 146 */     Mesh mesh = new Mesh();
/* 147 */     mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
/* 148 */     mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoordinates));
/* 149 */     mesh.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer(indices));
/* 150 */     mesh.updateBound();
/* 151 */     return mesh;
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.BlockChunk_MeshOptimizer
 * JD-Core Version:    0.6.2
 */