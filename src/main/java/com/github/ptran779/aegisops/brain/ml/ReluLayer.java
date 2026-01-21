package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Stack;

public class ReluLayer extends AbstractLayer {
  public static final int LAYER_ID = 2;
  public float[] outPred;

  private Stack<float[]> history;

  protected ReluLayer(int size) {
    super(size, size);
    outPred = new float[outputSize];
  }

  @Override
  public AbstractLayer clone() {return new ReluLayer(inputSize);}

  @Override
  public int getLayerID() {return LAYER_ID;}

  @Override
  public void turnOnTrainMode(boolean train) {
    if (train) {
      history = new Stack<>();
    } else {
      history = null;
    }
  }

  @Override
  public float[] forward(float[] input) {
    for (int i = 0; i < outputSize; i++) {
      outPred[i] = (input[i] > 0) ? input[i] : 0;
    }
    if (history != null) {
      history.push(Arrays.copyOf(input, inputSize));}
    return outPred;
  }

  @Override
  public MathUtil.arr2D backward(MathUtil.arr2D dOutArr) {
    // Safety check
    if (history == null || history.isEmpty()) {return new MathUtil.arr2D(0,0);}
    // Process the batch / time-sequence
    int c = 0;
    while (!history.isEmpty()) {
      // Retrieve the input state at this timestamp
      float[] inputAtTimeT = history.pop();

      // Calculate Derivative: f'(x) = 1 if x > 0, else 0
      for (int i = 0; i < inputSize; i++) {
        dOutArr.set(c,i,inputAtTimeT[i] > 0 ? dOutArr.get(c,i) : 0);
      }
      c++;
    }
    return dOutArr;
  }

  @Override
  public void updateWeight(float lr, float beta1, float beta2, float eps) {}

  @Override
  public byte[] serialize() {
    ByteBuffer buf = ByteBuffer.allocate(8);
    buf.putInt(inputSize);
    buf.putInt(outputSize);
    return buf.array();
  }

  @Override
  public void deserialize(ByteBuffer buffer) {
    int inSize = buffer.getInt();
    int outSize = buffer.getInt();

    this.inputSize = inSize;
    this.outputSize = outSize;
    this.outPred = new float[outputSize];
  }
}
