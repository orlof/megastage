package com.cubes;

import java.io.PrintStream;
import java.util.Random;

public class Noise
{
  private Random rand_;
  float roughness_;
  private float[][] grid_;

  public Noise(Random rand, float roughness, int width, int height)
  {
    this.roughness_ = (roughness / width);
    this.grid_ = new float[width][height];
    this.rand_ = (rand == null ? new Random() : rand);
    initialise();
  }

  public void initialise()
  {
    int xh = this.grid_.length - 1;
    int yh = this.grid_[0].length - 1;

    this.grid_[0][0] = (this.rand_.nextFloat() - 0.5F);
    this.grid_[0][yh] = (this.rand_.nextFloat() - 0.5F);
    this.grid_[xh][0] = (this.rand_.nextFloat() - 0.5F);
    this.grid_[xh][yh] = (this.rand_.nextFloat() - 0.5F);

    generate(0, 0, xh, yh);
  }

  private void generate(int xl, int yl, int xh, int yh)
  {
    int xm = (xl + xh) / 2;
    int ym = (yl + yh) / 2;
    if ((xl == xm) && (yl == ym)) {
      return;
    }
    this.grid_[xm][yl] = (0.5F * (this.grid_[xl][yl] + this.grid_[xh][yl]));
    this.grid_[xm][yh] = (0.5F * (this.grid_[xl][yh] + this.grid_[xh][yh]));
    this.grid_[xl][ym] = (0.5F * (this.grid_[xl][yl] + this.grid_[xl][yh]));
    this.grid_[xh][ym] = (0.5F * (this.grid_[xh][yl] + this.grid_[xh][yh]));
    float v = roughen(0.5F * (this.grid_[xm][yl] + this.grid_[xm][yh]), xl + yl, yh + xh);
    this.grid_[xm][ym] = v;
    this.grid_[xm][yl] = roughen(this.grid_[xm][yl], xl, xh);
    this.grid_[xm][yh] = roughen(this.grid_[xm][yh], xl, xh);
    this.grid_[xl][ym] = roughen(this.grid_[xl][ym], yl, yh);
    this.grid_[xh][ym] = roughen(this.grid_[xh][ym], yl, yh);
    generate(xl, yl, xm, ym);
    generate(xm, yl, xh, ym);
    generate(xl, ym, xm, yh);
    generate(xm, ym, xh, yh);
  }

  private float roughen(float v, int l, int h)
  {
    return (float)(v + this.roughness_ * (this.rand_.nextGaussian() * (h - l)));
  }

  public void printAsCSV()
  {
    for (int i = 0; i < this.grid_.length; i++) {
      for (int j = 0; j < this.grid_[0].length; j++) {
        System.out.print(this.grid_[i][j]);
        System.out.print(",");
      }
      System.out.println();
    }
  }

  public boolean[][] toBooleans()
  {
    int w = this.grid_.length;
    int h = this.grid_[0].length;
    boolean[][] ret = new boolean[w][h];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        ret[i][j] = this.grid_[i][j] < 0.0F;
      }
    }
    return ret;
  }

  public float[][] getGrid() {
    return this.grid_;
  }

  public float getGridValue(int x, int y) {
    return this.grid_[x][y];
  }

  public float getMinimum() {
    float minimum = 3.4028235E+38F;
    for (int i = 0; i < this.grid_.length; i++) {
      float[] row = this.grid_[i];
      for (int r = 0; r < row.length; r++) {
        if (row[r] < minimum) {
          minimum = row[r];
        }
      }
    }
    return minimum;
  }

  public float getMaximum() {
    float maximum = 1.4E-45F;
    for (int i = 0; i < this.grid_.length; i++) {
      float[] row = this.grid_[i];
      for (int r = 0; r < row.length; r++) {
        if (row[r] > maximum) {
          maximum = row[r];
        }
      }
    }
    return maximum;
  }
}

/* Location:           Cubes.jar
 * Qualified Name:     com.cubes.Noise
 * JD-Core Version:    0.6.2
 */