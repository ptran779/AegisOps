package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Stack;

public class LeakyReluLayer extends AbstractLayer {
  public static final int LAYER_ID = 4;
  protected float[] outPred;
  public static final float alpha=0.01f; // The "leak" slope hard code for simplified

  private Stack<float[]> history;

  // Matching your structure: separate constructor for size and alpha
  protected LeakyReluLayer(int size) {
    super(size, size);
    this.outPred = new float[outputSize];
  }

  @Override
  public AbstractLayer clone() {
    return new LeakyReluLayer(inputSize);
  }

  @Override
  public int getLayerID() { return LAYER_ID; }

  @Override
  public void turnOnTrainMode(boolean train) {
    if (train) {
      history = new Stack<>();
      stopCollection = true;
    } else {
      history = null;
      stopCollection = false;
    }
  }

  @Override
  public float[] forward(float[] input) {
    // Identical loop structure to your ReluLayer
    for (int i = 0; i < outputSize; i++) {
      // Logic: If > 0 keep it, if <= 0 multiply by alpha
      outPred[i] = (input[i] > 0) ? input[i] : (input[i] * alpha);
    }

    // Identical history tracking for backprop
    if (stopCollection) {
      history.push(Arrays.copyOf(input, inputSize));
    }
    return outPred;
  }

  @Override
  public MathUtil.arr2D backward(MathUtil.arr2D dOutArr) {
    // Safety check identical to yours
    if (history == null || history.isEmpty()) { return new MathUtil.arr2D(0,0); }

    int c = 0;
    while (!history.isEmpty()) {
      // Retrieve the input state at this timestamp
      float[] inputAtTimeT = history.pop();

      for (int i = 0; i < inputSize; i++) {
        // Retrieve current gradient
        float currentGrad = dOutArr.get(c, i);

        // Logic: Derivative is 1 if input > 0, else alpha
        // If input > 0: grad * 1
        // If input <= 0: grad * alpha
        float newGrad = (inputAtTimeT[i] > 0) ? currentGrad : (currentGrad * alpha);

        dOutArr.set(c, i, newGrad);
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
    buf.putFloat(alpha); // Save the slope!
    return buf.array();
  }

  @Override
  public void deserialize(ByteBuffer buffer) {
    int size = buffer.getInt();

    this.inputSize = size;
    this.outputSize = size;
    this.outPred = new float[outputSize];
  }
}