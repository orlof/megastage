/*     */ package com.cubes.network;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class BitInputStream
/*     */ {
/*     */   private InputStream in;
/*     */   private int lastByte;
/*  21 */   private int bits = 0;
/*     */ 
/*     */   public BitInputStream(InputStream in)
/*     */   {
/*  17 */     this.in = in;
/*     */   }
/*     */ 
/*     */   public <T extends Enum<T>> T readEnum(Class<T> enumClass)
/*     */     throws IOException
/*     */   {
/*  24 */     Enum[] enumConstants = (Enum[])enumClass.getEnumConstants();
/*  25 */     int bitsCount = BitUtil.getNeededBitsCount(enumConstants.length);
/*  26 */     return enumConstants[readBits(bitsCount)];
/*     */   }
/*     */ 
/*     */   public String readString_UTF8(int maximumBytesCountBits) throws IOException {
/*  30 */     int bytesCount = readBits(maximumBytesCountBits);
/*  31 */     byte[] bytes = readBytes(bytesCount);
/*  32 */     return new String(bytes, "UTF-8");
/*     */   }
/*     */ 
/*     */   public byte[] readBytes(int bytesCount) throws IOException {
/*  36 */     byte[] bytes = new byte[bytesCount];
/*  37 */     for (int i = 0; i < bytes.length; i++) {
/*  38 */       bytes[i] = ((byte)readBits(8));
/*     */     }
/*  40 */     return bytes;
/*     */   }
/*     */ 
/*     */   public float readFloat() throws IOException {
/*  44 */     return Float.intBitsToFloat(readBits(32));
/*     */   }
/*     */ 
/*     */   public int readInteger() throws IOException {
/*  48 */     return readBits(32);
/*     */   }
/*     */ 
/*     */   public boolean readBoolean() throws IOException {
/*  52 */     return readBits(1) == 1;
/*     */   }
/*     */ 
/*     */   public int readBits(int count) throws IOException {
/*  56 */     if (count == 0) {
/*  57 */       throw new IllegalArgumentException("Cannot read 0 bits.");
/*     */     }
/*  59 */     if (count > 32) {
/*  60 */       throw new IllegalArgumentException("Bit count overflow: " + count);
/*     */     }
/*     */ 
/*  63 */     int result = 0;
/*     */ 
/*  66 */     int remainingCount = count;
/*  67 */     while (remainingCount > 0)
/*     */     {
/*  69 */       if (this.bits == 0) {
/*  70 */         int b = this.in.read();
/*  71 */         if (b < 0) {
/*  72 */           throw new IOException("End of stream reached.");
/*     */         }
/*  74 */         this.lastByte = b;
/*  75 */         this.bits = 8;
/*     */       }
/*     */ 
/*  80 */       int bitsToCopy = this.bits < remainingCount ? this.bits : remainingCount;
/*     */ 
/*  84 */       int sourceShift = this.bits - bitsToCopy;
/*     */ 
/*  88 */       int targetShift = remainingCount - bitsToCopy;
/*     */ 
/*  91 */       result |= this.lastByte >> sourceShift << targetShift;
/*     */ 
/*  94 */       remainingCount -= bitsToCopy;
/*  95 */       this.bits -= bitsToCopy;
/*     */ 
/*  99 */       this.lastByte &= 255 >> 8 - this.bits;
/*     */     }
/* 101 */     return result;
/*     */   }
/*     */ 
/*     */   public long readLongBits(int count) throws IOException {
/* 105 */     if (count == 0) {
/* 106 */       throw new IllegalArgumentException("Cannot read 0 bits.");
/*     */     }
/* 108 */     if (count > 64) {
/* 109 */       throw new IllegalArgumentException("Bit count overflow: " + count);
/*     */     }
/*     */ 
/* 112 */     long result = 0L;
/*     */ 
/* 115 */     int remainingCount = count;
/* 116 */     while (remainingCount > 0)
/*     */     {
/* 118 */       if (this.bits == 0) {
/* 119 */         int b = this.in.read();
/* 120 */         if (b < 0) {
/* 121 */           throw new IOException("End of stream reached.");
/*     */         }
/* 123 */         this.lastByte = b;
/* 124 */         this.bits = 8;
/*     */       }
/*     */ 
/* 129 */       int bitsToCopy = this.bits < remainingCount ? this.bits : remainingCount;
/*     */ 
/* 133 */       int sourceShift = this.bits - bitsToCopy;
/*     */ 
/* 137 */       int targetShift = remainingCount - bitsToCopy;
/*     */ 
/* 140 */       result |= this.lastByte >> sourceShift << targetShift;
/*     */ 
/* 143 */       remainingCount -= bitsToCopy;
/* 144 */       this.bits -= bitsToCopy;
/*     */ 
/* 148 */       this.lastByte &= 255 >> 8 - this.bits;
/*     */     }
/*     */ 
/* 151 */     return result;
/*     */   }
/*     */ 
/*     */   public void close() {
/*     */     try {
/* 156 */       this.in.close();
/*     */     } catch (IOException ex) {
/* 158 */       System.out.println("Error while closing bit stream: " + ex.toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.network.BitInputStream
 * JD-Core Version:    0.6.2
 */