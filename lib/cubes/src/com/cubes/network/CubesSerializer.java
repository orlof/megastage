/*    */ package com.cubes.network;
/*    */ 
/*    */ import com.cubes.BlockChunkControl;
/*    */ import com.cubes.BlockTerrainControl;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class CubesSerializer
/*    */ {
/*    */   public static byte[][][][] writeChunksToBytes(BlockTerrainControl blockTerrain)
/*    */   {
/* 19 */     BlockChunkControl[][][] chunks = blockTerrain.getChunks();
/* 20 */     byte[][][][] bytes = new byte[chunks.length][chunks[0].length][chunks[0][0].length];
/* 21 */     for (int x = 0; x < chunks.length; x++) {
/* 22 */       for (int y = 0; y < chunks[x].length; y++) {
/* 23 */         for (int z = 0; z < chunks[x][y].length; z++) {
/* 24 */           bytes[x][y][z] = writeToBytes(chunks[x][y][z]);
/*    */         }
/*    */       }
/*    */     }
/* 28 */     return bytes;
/*    */   }
/*    */ 
/*    */   public static byte[] writeToBytes(BitSerializable bitSerializable) {
/* 32 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 33 */     BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
/* 34 */     bitSerializable.write(bitOutputStream);
/* 35 */     bitOutputStream.close();
/* 36 */     return byteArrayOutputStream.toByteArray();
/*    */   }
/*    */ 
/*    */   public static void readFromBytes(BitSerializable bitSerializable, byte[] bytes) {
/* 40 */     BitInputStream bitInputStream = new BitInputStream(new ByteArrayInputStream(bytes));
/*    */     try {
/* 42 */       bitSerializable.read(bitInputStream);
/*    */     } catch (IOException ex) {
/* 44 */       ex.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.network.CubesSerializer
 * JD-Core Version:    0.6.2
 */