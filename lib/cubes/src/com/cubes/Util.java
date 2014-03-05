/*    */ package com.cubes;
/*    */ 
/*    */ import com.jme3.math.Vector3f;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class Util
/*    */ {
/*    */   private static final float MAX_FLOAT_ROUNDING_DIFFERENCE = 1.0E-04F;
/*    */ 
/*    */   public static boolean isValidIndex(byte[][][] array, Vector3Int index)
/*    */   {
/* 19 */     return (index.getX() >= 0) && (index.getX() < array.length) && (index.getY() >= 0) && (index.getY() < array[0].length) && (index.getZ() >= 0) && (index.getZ() < array[0][0].length);
/*    */   }
/*    */ 
/*    */   public static boolean isValidIndex(Object[][][] array, Vector3Int index)
/*    */   {
/* 25 */     return (index.getX() >= 0) && (index.getX() < array.length) && (index.getY() >= 0) && (index.getY() < array[0].length) && (index.getZ() >= 0) && (index.getZ() < array[0][0].length);
/*    */   }
/*    */ 
/*    */   public static Vector3f compensateFloatRoundingErrors(Vector3f vector)
/*    */   {
/* 31 */     return new Vector3f(compensateFloatRoundingErrors(vector.getX()), compensateFloatRoundingErrors(vector.getY()), compensateFloatRoundingErrors(vector.getZ()));
/*    */   }
/*    */ 
/*    */   public static float compensateFloatRoundingErrors(float number)
/*    */   {
/* 37 */     float remainder = number % 1.0F;
/* 38 */     if ((remainder < 1.0E-04F) || (remainder > 0.9999F)) {
/* 39 */       number = Math.round(number);
/*    */     }
/* 41 */     return number;
/*    */   }
/*    */ 
/*    */   public static <T, E> T getHashKeyByValue(Map<T, E> map, E value) {
/* 45 */     for (Map.Entry entry : map.entrySet()) {
/* 46 */       if (value.equals(entry.getValue())) {
/* 47 */         return entry.getKey();
/*    */       }
/*    */     }
/* 50 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.Util
 * JD-Core Version:    0.6.2
 */