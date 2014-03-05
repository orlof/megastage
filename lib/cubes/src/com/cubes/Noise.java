/*     */ package com.cubes;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class Noise
/*     */ {
/*     */   private Random rand_;
/*     */   float roughness_;
/*     */   private float[][] grid_;
/*     */ 
/*     */   public Noise(Random rand, float roughness, int width, int height)
/*     */   {
/*  15 */     this.roughness_ = (roughness / width);
/*  16 */     this.grid_ = new float[width][height];
/*  17 */     this.rand_ = (rand == null ? new Random() : rand);
/*  18 */     initialise();
/*     */   }
/*     */ 
/*     */   public void initialise()
/*     */   {
/*  28 */     int xh = this.grid_.length - 1;
/*  29 */     int yh = this.grid_[0].length - 1;
/*     */ 
/*  31 */     this.grid_[0][0] = (this.rand_.nextFloat() - 0.5F);
/*  32 */     this.grid_[0][yh] = (this.rand_.nextFloat() - 0.5F);
/*  33 */     this.grid_[xh][0] = (this.rand_.nextFloat() - 0.5F);
/*  34 */     this.grid_[xh][yh] = (this.rand_.nextFloat() - 0.5F);
/*     */ 
/*  36 */     generate(0, 0, xh, yh);
/*     */   }
/*     */ 
/*     */   private void generate(int xl, int yl, int xh, int yh)
/*     */   {
/*  41 */     int xm = (xl + xh) / 2;
/*  42 */     int ym = (yl + yh) / 2;
/*  43 */     if ((xl == xm) && (yl == ym)) {
/*  44 */       return;
/*     */     }
/*  46 */     this.grid_[xm][yl] = (0.5F * (this.grid_[xl][yl] + this.grid_[xh][yl]));
/*  47 */     this.grid_[xm][yh] = (0.5F * (this.grid_[xl][yh] + this.grid_[xh][yh]));
/*  48 */     this.grid_[xl][ym] = (0.5F * (this.grid_[xl][yl] + this.grid_[xl][yh]));
/*  49 */     this.grid_[xh][ym] = (0.5F * (this.grid_[xh][yl] + this.grid_[xh][yh]));
/*  50 */     float v = roughen(0.5F * (this.grid_[xm][yl] + this.grid_[xm][yh]), xl + yl, yh + xh);
/*  51 */     this.grid_[xm][ym] = v;
/*  52 */     this.grid_[xm][yl] = roughen(this.grid_[xm][yl], xl, xh);
/*  53 */     this.grid_[xm][yh] = roughen(this.grid_[xm][yh], xl, xh);
/*  54 */     this.grid_[xl][ym] = roughen(this.grid_[xl][ym], yl, yh);
/*  55 */     this.grid_[xh][ym] = roughen(this.grid_[xh][ym], yl, yh);
/*  56 */     generate(xl, yl, xm, ym);
/*  57 */     generate(xm, yl, xh, ym);
/*  58 */     generate(xl, ym, xm, yh);
/*  59 */     generate(xm, ym, xh, yh);
/*     */   }
/*     */ 
/*     */   private float roughen(float v, int l, int h)
/*     */   {
/*  64 */     return (float)(v + this.roughness_ * (this.rand_.nextGaussian() * (h - l)));
/*     */   }
/*     */ 
/*     */   public void printAsCSV()
/*     */   {
/*  71 */     for (int i = 0; i < this.grid_.length; i++) {
/*  72 */       for (int j = 0; j < this.grid_[0].length; j++) {
/*  73 */         System.out.print(this.grid_[i][j]);
/*  74 */         System.out.print(",");
/*     */       }
/*  76 */       System.out.println();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean[][] toBooleans()
/*     */   {
/*  86 */     int w = this.grid_.length;
/*  87 */     int h = this.grid_[0].length;
/*  88 */     boolean[][] ret = new boolean[w][h];
/*  89 */     for (int i = 0; i < w; i++) {
/*  90 */       for (int j = 0; j < h; j++) {
/*  91 */         ret[i][j] = (this.grid_[i][j] < 0.0F ? 1 : 0);
/*     */       }
/*     */     }
/*  94 */     return ret;
/*     */   }
/*     */ 
/*     */   public float[][] getGrid() {
/*  98 */     return this.grid_;
/*     */   }
/*     */ 
/*     */   public float getGridValue(int x, int y) {
/* 102 */     return this.grid_[x][y];
/*     */   }
/*     */ 
/*     */   public float getMinimum() {
/* 106 */     float minimum = 3.4028235E+38F;
/* 107 */     for (int i = 0; i < this.grid_.length; i++) {
/* 108 */       float[] row = this.grid_[i];
/* 109 */       for (int r = 0; r < row.length; r++) {
/* 110 */         if (row[r] < minimum) {
/* 111 */           minimum = row[r];
/*     */         }
/*     */       }
/*     */     }
/* 115 */     return minimum;
/*     */   }
/*     */ 
/*     */   public float getMaximum() {
/* 119 */     float maximum = 1.4E-45F;
/* 120 */     for (int i = 0; i < this.grid_.length; i++) {
/* 121 */       float[] row = this.grid_[i];
/* 122 */       for (int r = 0; r < row.length; r++) {
/* 123 */         if (row[r] > maximum) {
/* 124 */           maximum = row[r];
/*     */         }
/*     */       }
/*     */     }
/* 128 */     return maximum;
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.Noise
 * JD-Core Version:    0.6.2
 */