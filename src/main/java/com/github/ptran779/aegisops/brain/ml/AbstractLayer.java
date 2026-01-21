package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;

public abstract class AbstractLayer {
  protected boolean infAcc = false;  // use to signal training data collection for eval
  protected int inputSize;
  protected int outputSize;

  protected AbstractLayer(int inputSize, int outputSize) {
    this.inputSize = inputSize;
    this.outputSize = outputSize;
  }

  public int getInputSize() {return inputSize;}
  public int getOutputSize() {return outputSize;}
  public abstract int getLayerID();
  public void zeroHidden(){};

  /**
   * Forward pass: takes input of type I, returns output of type O
   */
  public void turnOnTrainMode(boolean train){}
  public void pauseTraining(boolean pause){infAcc = !pause;}
  public abstract float[] forward(float[] input);
  public abstract MathUtil.arr2D backward(MathUtil.arr2D dOut);
  public abstract void updateWeight(float lr, float beta1, float beta2, float eps);

  public abstract byte[] serialize();
  public abstract void deserialize(ByteBuffer buffer);

  public abstract AbstractLayer clone();
}
