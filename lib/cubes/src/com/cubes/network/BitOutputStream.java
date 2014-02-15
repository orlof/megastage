/*     */ package com.cubes.network;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class BitOutputStream
/*     */ {
/*     */   private OutputStream out;
/*  21 */   private int currentByte = 0;
/*  22 */   private int bits = 8;
/*     */ 
/*     */   public BitOutputStream(OutputStream out)
/*     */   {
/*  18 */     this.out = out;
/*     */   }
/*     */ 
/*     */   public <T extends Enum<T>> void writeEnum(T value)
/*     */   {
/*  25 */     Enum[] enumConstants = (Enum[])value.getDeclaringClass().getEnumConstants();
/*  26 */     int bitsCount = BitUtil.getNeededBitsCount(enumConstants.length);
/*  27 */     writeBits(value.ordinal(), bitsCount);
/*     */   }
/*     */ 
/*     */   public void writeString_UTF8(String string, int maximumBytesCountBits) {
/*     */     try {
/*  32 */       byte[] bytes = string.getBytes("UTF-8");
/*  33 */       writeBits(bytes.length, maximumBytesCountBits);
/*  34 */       writeBytes(bytes, bytes.length);
/*     */     } catch (UnsupportedEncodingException ex) {
/*  36 */       System.out.println("Error while encoding string: " + ex.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeBytes(byte[] bytes, int count) {
/*  41 */     for (int i = 0; i < count; i++) {
/*  42 */       byte value = i < bytes.length ? bytes[i] : 0;
/*  43 */       writeBits(value, 8);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeFloat(float value) {
/*  48 */     writeBits(Float.floatToIntBits(value), 32);
/*     */   }
/*     */ 
/*     */   public void writeInteger(int value) {
/*  52 */     writeBits(value, 32);
/*     */   }
/*     */ 
/*     */   public void writeBoolean(boolean value) {
/*  56 */     writeBits(value ? 1 : 0, 1);
/*     */   }
/*     */ 
/*     */   public void writeBits(int value, int count) {
/*  60 */     if (count == 0) {
/*  61 */       throw new IllegalArgumentException("Cannot write 0 bits.");
/*     */     }
/*     */ 
/*  64 */     value &= -1 >>> 32 - count;
/*     */ 
/*  66 */     int remaining = count;
/*  67 */     while (remaining > 0) {
/*  68 */       int bitsToCopy = this.bits < remaining ? this.bits : remaining;
/*     */ 
/*  70 */       int sourceShift = remaining - bitsToCopy;
/*  71 */       int targetShift = this.bits - bitsToCopy;
/*     */ 
/*  73 */       this.currentByte |= value >>> sourceShift << targetShift;
/*     */ 
/*  75 */       remaining -= bitsToCopy;
/*  76 */       this.bits -= bitsToCopy;
/*     */ 
/*  78 */       value &= -1 >>> 32 - remaining;
/*     */ 
/*  82 */       if (this.bits == 0)
/*  83 */         flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeLongBits(long value, int count)
/*     */   {
/*  89 */     if (count == 0) {
/*  90 */       throw new IllegalArgumentException("Cannot write 0 bits.");
/*     */     }
/*     */ 
/*  94 */     value &= -1L >>> 64 - count;
/*     */ 
/*  96 */     int remaining = count;
/*  97 */     while (remaining > 0) {
/*  98 */       int bitsToCopy = this.bits < remaining ? this.bits : remaining;
/*     */ 
/* 100 */       int sourceShift = remaining - bitsToCopy;
/* 101 */       int targetShift = this.bits - bitsToCopy;
/*     */ 
/* 103 */       this.currentByte = ((int)(this.currentByte | value >>> sourceShift << targetShift));
/*     */ 
/* 105 */       remaining -= bitsToCopy;
/* 106 */       this.bits -= bitsToCopy;
/*     */ 
/* 108 */       value &= -1L >>> 64 - remaining;
/*     */ 
/* 112 */       if (this.bits == 0)
/* 113 */         flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void flush()
/*     */   {
/*     */     try {
/* 120 */       this.out.write(this.currentByte);
/* 121 */       this.bits = 8;
/* 122 */       this.currentByte = 0;
/*     */     } catch (IOException ex) {
/* 124 */       System.out.println("Error while flushing bit stream: " + ex.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() {
/* 129 */     flush();
/*     */     try {
/* 131 */       this.out.close();
/*     */     } catch (IOException ex) {
/* 133 */       System.out.println("Error while closing bit stream: " + ex.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPendingBits() {
/* 138 */     return this.bits;
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.network.BitOutputStream
 * JD-Core Version:    0.6.2
 */