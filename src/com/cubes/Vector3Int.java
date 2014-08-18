package com.cubes;

public class Vector3Int
{
  private int x;
  private int y;
  private int z;

  public Vector3Int(int x, int y, int z)
  {
    this();
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3Int()
  {
  }

  public int getX()
  {
    return this.x;
  }

  public Vector3Int setX(int x) {
    this.x = x;
    return this;
  }

  public int getY() {
    return this.y;
  }

  public Vector3Int setY(int y) {
    this.y = y;
    return this;
  }

  public int getZ() {
    return this.z;
  }

  public Vector3Int setZ(int z) {
    this.z = z;
    return this;
  }

  public boolean hasNegativeCoordinate() {
    return (this.x < 0) || (this.y < 0) || (this.z < 0);
  }

  public Vector3Int set(Vector3Int vector3Int) {
    return set(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
  }

  public Vector3Int set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  public Vector3Int add(Vector3Int vector3Int) {
    return add(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
  }

  public Vector3Int add(int x, int y, int z) {
    return new Vector3Int(this.x + x, this.y + y, this.z + z);
  }

  public Vector3Int addLocal(Vector3Int vector3Int) {
    return addLocal(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
  }

  public Vector3Int addLocal(int x, int y, int z) {
    this.x += x;
    this.y += y;
    this.z += z;
    return this;
  }

  public Vector3Int subtract(Vector3Int vector3Int) {
    return subtract(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
  }

  public Vector3Int subtract(int x, int y, int z) {
    return new Vector3Int(this.x - x, this.y - y, this.z - z);
  }

  public Vector3Int subtractLocal(Vector3Int vector3Int) {
    return subtractLocal(vector3Int.getX(), vector3Int.getY(), vector3Int.getZ());
  }

  public Vector3Int subtractLocal(int x, int y, int z) {
    this.x -= x;
    this.y -= y;
    this.z -= z;
    return this;
  }

  public Vector3Int negate() {
    return mult(-1);
  }

  public Vector3Int mult(int factor) {
    return mult(factor, factor, factor);
  }

  public Vector3Int mult(int x, int y, int z) {
    return new Vector3Int(this.x * x, this.y * y, this.z * z);
  }

  public Vector3Int negateLocal() {
    return multLocal(-1);
  }

  public Vector3Int multLocal(int factor) {
    return multLocal(factor, factor, factor);
  }

  public Vector3Int multLocal(int x, int y, int z) {
    this.x *= x;
    this.y *= y;
    this.z *= z;
    return this;
  }

  public Vector3Int clone()
  {
    return new Vector3Int(this.x, this.y, this.z);
  }

  public boolean equals(Object object)
  {
    if ((object instanceof Vector3Int)) {
      Vector3Int vector3Int = (Vector3Int)object;
      return (this.x == vector3Int.getX()) && (this.y == vector3Int.getY()) && (this.z == vector3Int.getZ());
    }
    return false;
  }

  public String toString()
  {
    return "[Vector3Int x=" + this.x + " y=" + this.y + " z=" + this.z + "]";
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.Vector3Int
 * JD-Core Version:    0.6.2
 */