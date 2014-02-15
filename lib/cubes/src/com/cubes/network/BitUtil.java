/*    */ package com.cubes.network;
/*    */ 
/*    */ public class BitUtil
/*    */ {
/*    */   public static int getNeededBitsCount(int value)
/*    */   {
/* 14 */     return 32 - Integer.numberOfLeadingZeros(value);
/*    */   }
/*    */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.network.BitUtil
 * JD-Core Version:    0.6.2
 */