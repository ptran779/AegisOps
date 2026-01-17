package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;

public abstract class AbstractLayer {
  protected int inputSize;
  protected int outputSize;

  protected AbstractLayer(int inputSize, int outputSize) {
    this.inputSize = inputSize;
    this.outputSize = outputSize;
  }

  public int getInputSize() {return inputSize;}
  public int getOutputSize() {return outputSize;}

  /**
   * Forward pass: takes input of type I, returns output of type O
   */
  public abstract float[] forward(float[] input);
//  public abstract float[] backward(float[] input);

  public abstract byte[] serialize();
  public abstract void deserialize(ByteBuffer buffer);
}
