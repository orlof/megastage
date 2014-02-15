/*     */ package com.cubes;
/*     */ 
/*     */ public class Vector3Int
/*     */ {
/*     */   private int x;
/*     */   private int y;
/*     */   private int z;
/*     */ 
/*     */   public Vector3Int(int x, int y, int z)
/*     */   {
/*  14 */     this();
/*  15 */     this.x = x;
/*  16 */     this.y = y;
/*  17 */     this.z = z;
/*     */   }
/*     */ 
/*     */   public Vector3Int()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getX()
/*     */   {
/*  28 */     return this.x;
/*     */   }
/*     */ 
/*     */   public Vector3Int setX(int x) {
/*  32 */     this.x = x;
/*  33 */     return this;
/*     */   }
/*     */ 
/*     */   public int getY() {
/*  37 */     return this.y;
/*     */   }
/*     */ 
/*     */   public Vector3Int setY(int y) {
/*  41 */     this.y = y;
/*  42 */     return this;
/*     */   }
/*     */ 
/*     */   public int getZ() {
/*  46 */     return this.z;
/*     */   }
/*     */ 
/*     */   public Vector3Int setZ(int z) {
/*  50 */     this.z = z;
/*  51 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean hasNegativeCoordinate() {
/*  55 */     return (this.x < 0) || (this.y < 0) || (this.z < 0);
/*     */   }
/*     */ 
/*     */   public Vector3Int set(Vector3Int vector3Int) {
/*  59 */     return set(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
/*     */   }
/*     */ 
/*     */   public Vector3Int set(int x, int y, int z) {
/*  63 */     this.x = x;
/*  64 */     this.y = y;
/*  65 */     this.z = z;
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */   public Vector3Int add(Vector3Int vector3Int) {
/*  70 */     return add(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
/*     */   }
/*     */ 
/*     */   public Vector3Int add(int x, int y, int z) {
/*  74 */     return new Vector3Int(this.x + x, this.y + y, this.z + z);
/*     */   }
/*     */ 
/*     */   public Vector3Int addLocal(Vector3Int vector3Int) {
/*  78 */     return addLocal(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
/*     */   }
/*     */ 
/*     */   public Vector3Int addLocal(int x, int y, int z) {
/*  82 */     this.x += x;
/*  83 */     this.y += y;
/*  84 */     this.z += z;
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   public Vector3Int subtract(Vector3Int vector3Int) {
/*  89 */     return subtract(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
/*     */   }
/*     */ 
/*     */   public Vector3Int subtract(int x, int y, int z) {
/*  93 */     return new Vector3Int(this.x - x, this.y - y, this.z - z);
/*     */   }
/*     */ 
/*     */   public Vector3Int subtractLocal(Vector3Int vector3Int) {
/*  97 */     return subtractLocal(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
/*     */   }
/*     */ 
/*     */   public Vector3Int subtractLocal(int x, int y, int z) {
/* 101 */     this.x -= x;
/* 102 */     this.y -= y;
/* 103 */     this.z -= z;
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   public Vector3Int negate() {
/* 108 */     return mult(-1);
/*     */   }
/*     */ 
/*     */   public Vector3Int mult(int factor) {
/* 112 */     return mult(factor, factor, factor);
/*     */   }
/*     */ 
/*     */   public Vector3Int mult(int x, int y, int z) {
/* 116 */     return new Vector3Int(this.x * x, this.y * y, this.z * z);
/*     */   }
/*     */ 
/*     */   public Vector3Int negateLocal() {
/* 120 */     return multLocal(-1);
/*     */   }
/*     */ 
/*     */   public Vector3Int multLocal(int factor) {
/* 124 */     return multLocal(factor, factor, factor);
/*     */   }
/*     */ 
/*     */   public Vector3Int multLocal(int x, int y, int z) {
/* 128 */     this.x *= x;
/* 129 */     this.y *= y;
/* 130 */     this.z *= z;
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   public Vector3Int clone()
/*     */   {
/* 136 */     return new Vector3Int(this.x, this.y, this.z);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/* 141 */     if ((object instanceof Vector3Int)) {
/* 142 */       Vector3Int vector3Int = (Vector3Int)object;
/* 143 */       return (this.x == vector3Int.getX()) && (this.y == vector3Int.getY()) && (this.z == vector3Int.getZ());
/*     */     }
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 150 */     return "[Vector3Int x=" + this.x + " y=" + this.y + " z=" + this.z + "]";
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.Vector3Int
 * JD-Core Version:    0.6.2
 */