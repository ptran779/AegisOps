package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Stack;

public class DenseLayer extends AbstractLayer {
  public static final int LAYER_ID = 1;
  public MathUtil.arr2D weights; // [inputSize][outputSize]
  public float[] biases;    // [outputSize]
  public float[] outPred;       // [outputSize]

  //for training
  private MathUtil.arr2D dW; // weight gradients
  private float[] dB;   // bias gradients

  // History Stack for BPTT / Data Packs
  Stack<float[]> history;
  MathUtil.arr2D dInArr;

  // Adam state (for training)
  private MathUtil.arr2D mW, vW;  // first/second moments for weights
  private float[] mB, vB;     // first/second moments for biases
  private int adamT = 0;      // timestep

  public DenseLayer(int inputSize, int outputSize) {
    super(inputSize, outputSize);
    weights = new MathUtil.arr2D(inputSize, outputSize);
    biases = new float[outputSize];
    outPred = new float[outputSize];
  }

  public AbstractLayer clone() {
    DenseLayer copy = new DenseLayer(0, 0);
    copy.weights = weights.clone();
    copy.biases = biases.clone();
    copy.outPred = outPred.clone();
    copy.inputSize = inputSize;
    copy.outputSize = outputSize;
    return copy;
  }

  @Override
  public int getLayerID() {return LAYER_ID;}

  @Override
  public void turnOnTrainMode(boolean train) {
    if (train){
      infAcc = true;
      initAdam();
      dW = new MathUtil.arr2D(inputSize, outputSize);
      dB = new float[outputSize];
      history = new Stack<>();
      dInArr = new MathUtil.arr2D(0, 0); // just tmp obj... will get scale up later

      // Explicitly zero out gradients
      dW.zeroAll();
      Arrays.fill(dB, 0f);

    } else {
      infAcc = false;
      clearAdam();
      dW = null;
      dB = null;
      history = null;
      dInArr = null;
    }
  }

  // Optional: random initialization
  public void randomInit(float min, float max) {
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weights.set(i,j,min + (float) Math.random() * (max - min));
      }
    }
    for (int j = 0; j < outputSize; j++) {
      biases[j] = min + (float) Math.random() * (max - min);
    }
  }

  @Override
  public float[] forward(float[] input) {
    // 1. Compute Forward
    for (int j = 0; j < outputSize; j++) {
      float sum = biases[j];
      for (int i = 0; i < inputSize; i++) sum += input[i] * weights.get(i,j);
      outPred[j] = sum;
    }

    // 2. Store History (Train only)
    if (infAcc) {
      history.push(Arrays.copyOf(input, inputSize));
    }

    return outPred;
  }

  @Override
  public MathUtil.arr2D backward(MathUtil.arr2D dOutArr) {
    // Safety: If no history, we can't compute gradients.
    if (history == null || history.isEmpty()) return new MathUtil.arr2D(0, 0);

    // We create a new 2D array for the gradients to pass to the previous layer.
    // Size: [TimeSteps][InputSize]
    dInArr.ensureSize(history.size(),inputSize);
    dInArr.zeroAll();
    int t = 0;
    // Process the batch / time-sequence
    while (!history.isEmpty()) {
//      float[] dOut = dOutArr[t];         // Gradient from next layer
      float[] inputT = history.pop();    // Input at this step
//      float[] dIn = dInArr[t];           // Gradient to previous layer (fill this)

      // --- OPTIMIZED LOOP ---
      // We iterate 'i' then 'j' to access weights[i][j] and dW[i][j] sequentially (Cache Friendly)
      for (int i = 0; i < inputSize; i++) {
        float inVal = inputT[i];
        float dInSum = 0; // Accumulator for dInput[i]

        for (int j = 0; j < outputSize; j++) {
          float dOutVal = dOutArr.get(t, j);
          // 1. Accumulate Weight Gradient
          dW.set(i,j,dW.get(i, j) + inVal * dOutVal);
          // 2. Compute Input Gradient (Chain Rule)
          // dIn[i] = sum( dOut[j] * weight[i][j] )
          dInSum += dOutVal * weights.get(i,j);
        }
        dInArr.set(t, i, dInSum);
      }

      // 3. Accumulate Bias Gradient (1D loop is fast)
      for (int j = 0; j < outputSize; j++) {
        dB[j] += dOutArr.get(t, j);
      }

      t++;
    }

    return dInArr;
  }

  //Initialize Adam optimizer states for this layer
  public void initAdam() {
    mW = new MathUtil.arr2D(inputSize, outputSize);
    vW = new MathUtil.arr2D(inputSize, outputSize);
    mB = new float[outputSize];
    vB = new float[outputSize];
    adamT = 0;
  }

  // Clear Adam optimizer states to free memory
  public void clearAdam() {
    mW = null; vW = null; mB = null; vB = null;
  }

  /**
   * Apply Adam update to weights and biases
   * @param lr learning rate
   * @param beta1 momentum decay (default 0.9)
   * @param beta2 second moment decay (default 0.999)
   * @param eps small epsilon (default 1e-8)
   */
  public void updateWeight(float lr, float beta1, float beta2, float eps) {
    if (mW == null || vW == null) throw new IllegalStateException("Adam not initialized");

    adamT++;
    float biasCorr1 = 1 - (float) Math.pow(beta1, adamT);
    float biasCorr2 = 1 - (float) Math.pow(beta2, adamT);
    float invBeta1 = 1-beta1;
    float invBeta2 = 1-beta2;

    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        mW.set(i,j,beta1 * mW.get(i,j) + invBeta1 * dW.get(i,j));
        float g = dW.get(i,j);
        vW.set(i,j, beta2 * vW.get(i,j) + invBeta2 * (g * g));
        float mHat = mW.get(i,j) / biasCorr1;
        float vHat = vW.get(i,j) / biasCorr2;
        weights.set(i,j,weights.get(i,j) - (float) (lr * mHat / (Math.sqrt(vHat) + eps)));
      }
    }
    // CRITICAL: Reset gradient after using it
    dW.zeroAll();

    for (int j = 0; j < outputSize; j++) {
      mB[j] = beta1 * mB[j] + invBeta1 * dB[j];
      vB[j] = beta2 * vB[j] + invBeta2 * dB[j] * dB[j];
      float mHat = mB[j] / biasCorr1;
      float vHat = vB[j] / biasCorr2;
      biases[j] -= (float) (lr * mHat / (Math.sqrt(vHat) + eps));

      // CRITICAL: Reset gradient after using it
      dB[j] = 0;
    }
  }

  @Override
  public byte[] serialize() {
    ByteBuffer buf = ByteBuffer.allocate(4 + 4 + inputSize * outputSize * 4 + outputSize * 4);
    buf.putInt(inputSize);
    buf.putInt(outputSize);
    for (int i = 0; i < inputSize; i++)
      for (int j = 0; j < outputSize; j++)
        buf.putFloat(weights.get(i,j));
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
    this.weights = new MathUtil.arr2D(inSize, outSize);
    this.biases = new float[outputSize];
    this.outPred = new float[outputSize];

    // Read weights
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weights.set(i,j,buffer.getFloat());
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
        out.append(String.format("%.3f", weights.get(i,j))).append(" ,");
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