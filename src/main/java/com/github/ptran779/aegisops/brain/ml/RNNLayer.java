package com.github.ptran779.aegisops.brain.ml;
import org.apache.commons.math3.util.FastMath;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RNNLayer extends AbstractLayer {
  public float[][] weightIn; // input -> hidden
  public float[] biases;    // bias
  public float[][] weightHid;
  public float[] hidden;    // hidden state
  private float[] hiddenBuffer;  // for output buffering to prevent gc overkill

  public RNNLayer(int inputSize, int outputSize) {
    super(inputSize, outputSize);
    weightIn = new float[inputSize][outputSize];
    biases = new float[outputSize];
    weightHid = new float[outputSize][outputSize];
    hidden = new float[outputSize]; // initially zero
    hiddenBuffer = new float[outputSize];
  }

  public void randomInit(float min, float max) {
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightIn[i][j] = min + (float) Math.random() * (max - min);
      }
    }
    for (int j = 0; j < outputSize; j++) {
      biases[j] = min + (float) Math.random() * (max - min);
    }
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightHid[i][j] = min + (float) Math.random() * (max - min);
      }
    }
  }

  public void zeroHidden() {Arrays.fill(hidden, 0);}

  @Override
  public float[] forward(float[] input) {
    float[] newH = hiddenBuffer;

    for (int j = 0; j < outputSize; j++) {
      float sum = biases[j];

      // input contribution
      for (int i = 0; i < inputSize; i++) {
        sum += input[i] * weightIn[i][j];
      }

      // hidden contribution
      for (int k = 0; k < outputSize; k++) {
        sum += hidden[k] * weightHid[k][j];
      }

      // Tanh activation
      newH[j] = (float) FastMath.tanh(sum);
    }

    float[] bufHolder = hidden;
    // update hidden state
    hidden = newH;
    hiddenBuffer = bufHolder;

    // return hidden as output
    return hidden;
  }

  @Override
  public byte[] serialize() {
    // compute total size:
    // weightIn + weightHid + biases, all floats = 4 bytes each
    int totalFloats = 2 // size in, size out
        + inputSize * outputSize        // weightIn
        + outputSize * outputSize      // weightHid
        + outputSize;                  // biases

    ByteBuffer buffer = ByteBuffer.allocate(totalFloats * 4);

    buffer.putInt(inputSize);
    buffer.putInt(outputSize);
    // write input->hidden weights
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        buffer.putFloat(weightIn[i][j]);
      }
    }

    // write hidden->hidden weights
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        buffer.putFloat(weightHid[i][j]);
      }
    }

    // write biases
    for (int j = 0; j < outputSize; j++) {
      buffer.putFloat(biases[j]);
    }

    return buffer.array();
  }

  @Override
  public void deserialize(ByteBuffer buffer) {
    // Read sizes
    int inSize = buffer.getInt();
    int outSize = buffer.getInt();

    // Re-allocate arrays
    this.inputSize = inSize;
    this.outputSize = outSize;

    weightIn = new float[inputSize][outputSize];
    weightHid = new float[outputSize][outputSize];
    biases = new float[outputSize];
    hidden = new float[outputSize];
    hiddenBuffer = new float[outputSize];

    // Read input->hidden weights
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightIn[i][j] = buffer.getFloat();
      }
    }

    // Read hidden->hidden weights
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightHid[i][j] = buffer.getFloat();
      }
    }

    // Read biases
    for (int j = 0; j < outputSize; j++) {
      biases[j] = buffer.getFloat();
    }

    // Hidden state starts zeroed after deserialization
    Arrays.fill(hidden, 0);
    Arrays.fill(hiddenBuffer, 0);
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("RNNLayer [inputSize=").append(inputSize).append(", outputSize=").append(outputSize).append("\n");
    out.append("weights\n");
    for (int i = 0; i < inputSize; i++) {
      out.append(i).append(" : ");
      for (int j = 0; j < outputSize; j++) {
        out.append(String.format("%.3f", weightIn[i][j])).append(" ,");
      }
      out.append("\n");
    }
    out.append("biases\n");
    for (int i = 0; i < outputSize; i++) {
      out.append(i).append(" : ");
      out.append(String.format("%.3f", biases[i]));
      out.append("\n");
    }
    out.append("Hidden Weight \n");
    for (int i = 0; i < outputSize; i++) {
      out.append(i).append(" : ");
      for (int j = 0; j < outputSize; j++) {
        out.append(String.format("%.3f", weightHid[i][j])).append(" ,");
      }
      out.append("\n");
    }
    out.append("Hidden State \n");
    for (int i = 0; i < outputSize; i++) {
      out.append(i).append(" : ");
      out.append(String.format("%.3f", hidden[i]));
      out.append("\n");
    }

    return out.toString();
  }
}
