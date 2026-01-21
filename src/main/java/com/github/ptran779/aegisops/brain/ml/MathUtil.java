package com.github.ptran779.aegisops.brain.ml;

import java.util.Arrays;

public class MathUtil {
  // optimized 2d array
  public static class arr2D{
    protected int r;
    protected int c;
    public float[] data;
    public arr2D(int r, int c) {
      this.r = r;
      this.c = c;
      data = new float[r * c];
    }
    public arr2D clone(){
      arr2D copy = new arr2D(r, c);
      System.arraycopy(data, 0, copy.data, 0, data.length);
      return copy;
    }
    public float get(int r, int c) {return data[r * this.c + c];}
    public void set(int r, int c, float value) {data[r * this.c + c] = value;}
    public void ensureSize(int newRows, int newCols) {
      // Only resize if actually needed
      if (newRows > this.r || newCols > this.c) {
        float[] newData = new float[newRows * newCols];
        if (newCols == this.c) {
          System.arraycopy(this.data, 0, newData, 0, this.data.length);
        }
        else {
          for (int i = 0; i < this.r; i++) {System.arraycopy(this.data, i * this.c,newData, i * newCols,this.c);}
        }

        // Update the dimensions!
        this.data = newData;
        this.r = newRows;
        this.c = newCols;
      }
    }
    public void zeroAll(){Arrays.fill(this.data, 0);}
  }
}
