package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;

public class DenseLayer extends AbstractLayer {
  public float[][] weights; // [inputSize][outputSize]
  public float[] biases;    // [outputSize]
  public float[] out;

  public DenseLayer(int inputSize, int outputSize) {
    super(inputSize, outputSize);
    weights = new float[inputSize][outputSize];
    biases = new float[outputSize];
    out = new float[outputSize];
  }

  // Optional: random initialization
  public void randomInit(float min, float max) {
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weights[i][j] = min + (float) Math.random() * (max - min);
      }
    }
    for (int j = 0; j < outputSize; j++) {
      biases[j] = min + (float) Math.random() * (max - min);
    }
  }

  @Override
  public float[] forward(float[] input) {
    for (int j = 0; j < outputSize; j++) {
      float sum = biases[j];
      for (int i = 0; i < inputSize; i++)
        sum += input[i] * weights[i][j];
      out[j] = sum; // ReLU
    }
    return out;
  }

  @Override
  public byte[] serialize() {
    ByteBuffer buf = ByteBuffer.allocate(4 + 4 + inputSize * outputSize * 4 + outputSize * 4);
    buf.putInt(inputSize);
    buf.putInt(outputSize);
    for (int i = 0; i < inputSize; i++)
      for (int j = 0; j < outputSize; j++)
        buf.putFloat(weights[i][j]);
    for (int j = 0; j < outputSize; j++)
      buf.putFloat(biases[j]);
    return buf.array();
  }

  @Override
  public void deserialize(ByteBuffer buffer) {
    // Read input/output sizes
    int inSize = buffer.getInt();
    int outSize = buffer.getInt();

    // Re-allocate arrays in case they weren't already sized
    this.inputSize = inSize;
    this.outputSize = outSize;
    this.weights = new float[inputSize][outputSize];
    this.biases = new float[outputSize];
    this.out = new float[outputSize];

    // Read weights
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weights[i][j] = buffer.getFloat();
      }
    }

    // Read biases
    for (int j = 0; j < outputSize; j++) {
      biases[j] = buffer.getFloat();
    }
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("DenseLayer [inputSize=").append(inputSize).append(", outputSize=").append(outputSize).append("\n");
    out.append("weights\n");
    for (int i = 0; i < inputSize; i++) {
      out.append(i).append(" : ");
      for (int j = 0; j < outputSize; j++) {
        out.append(String.format("%.3f", weights[i][j])).append(" ,");
      }
      out.append("\n");
    }
    out.append("biases\n");
    for (int i = 0; i < outputSize; i++) {
      out.append(i).append(" : ");
      out.append(String.format("%.3f", biases[i]));
      out.append("\n");
    }

    return out.toString();
  }
}