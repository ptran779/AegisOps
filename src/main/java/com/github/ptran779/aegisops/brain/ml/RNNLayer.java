package com.github.ptran779.aegisops.brain.ml;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Stack;
import java.lang.Math;

public class RNNLayer extends AbstractLayer {
  public static final int LAYER_ID = 3;
  protected MathUtil.arr2D weightIn; // input -> hidden
  protected MathUtil.arr2D weightHid;
  protected float[] biases;    // bias

  protected float[] hidden;    // hidden state
  protected float[] hiddenBuffer;  // for output buffering to prevent gc overkill

  // --- TRAINING STATE (Allocated only when turnOnTrainMode(true)) ---
  // Gradients
  private MathUtil.arr2D dWin, dWhid;
  private float[] dBias;
  float[] prevHiddenStateZERO;
  float[] deltaVec;
  float[] futureError;

  // Error from the "Future" (t+1) flowing back to "Now" (t)
  private float[] dNextHidden;

  // The "Tape Recorder" for Backpropagation Through Time (BPTT)
  Stack<RnnState> history;
  MathUtil.arr2D dInputToReturn;

  // Adam Optimizer Moments
  private MathUtil.arr2D mWin, vWin;
  private MathUtil.arr2D mWhid, vWhid;
  private float[] mBias, vBias;
  private int adamT = 0;

  public RNNLayer(int inputSize, int outputSize) {
    super(inputSize, outputSize);
    weightIn = new MathUtil.arr2D(inputSize, outputSize);
    weightHid = new MathUtil.arr2D(outputSize, outputSize);
    biases = new float[outputSize];
    hidden = new float[outputSize]; // initially zero
    hiddenBuffer = new float[outputSize];
  }

  public AbstractLayer clone() {
    RNNLayer copy = new RNNLayer(0, 0);
    copy.weightIn = weightIn.clone();
    copy.weightHid = weightHid.clone();
    copy.biases = biases.clone();
    copy.inputSize = inputSize;
    copy.outputSize = outputSize;
    copy.hidden = hidden.clone();
    copy.hiddenBuffer = hiddenBuffer.clone();
    return copy;
  }

  private void initAdam() {
    if (mWin != null) return; // Already init
    mWin = new MathUtil.arr2D(inputSize,outputSize);
    vWin = new MathUtil.arr2D(inputSize,outputSize);
    mWhid = new MathUtil.arr2D(outputSize,outputSize);
    vWhid = new MathUtil.arr2D(outputSize,outputSize);
    mBias = new float[outputSize];
    vBias = new float[outputSize];
    adamT = 0;
  }

  private void clearAdam() {
    mWin = null; vWin = null; mWhid = null; vWhid = null; mBias = null; vBias = null;
  }

  @Override
  public int getLayerID() {return LAYER_ID;}

  public void turnOnTrainMode(boolean train) {
    if (train) {
      stopCollection = true;
      initAdam();
      dWin = new MathUtil.arr2D(inputSize,outputSize);
      dWhid = new MathUtil.arr2D(outputSize,outputSize);
      dBias = new float[outputSize];
      dNextHidden = new float[outputSize];
      prevHiddenStateZERO = new float[outputSize];
      futureError = new float[outputSize];
      deltaVec = new float[outputSize];
      history = new Stack<>();
      dInputToReturn = new MathUtil.arr2D(0, 0);
    } else {
      stopCollection = false;
      clearAdam();
      dWin = null; dWhid = null; dBias = null; prevHiddenStateZERO=null; futureError=null; deltaVec = null;
      dNextHidden = null; history = null; dInputToReturn = null;
    }
  }

  public void randomInit(float min, float max) {
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightIn.set(i,j,min + (float) Math.random() * (max - min));
      }
    }
    for (int j = 0; j < outputSize; j++) {
      biases[j] = min + (float) Math.random() * (max - min);
    }
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightHid.set(i,j,min + (float) Math.random() * (max - min));
      }
    }
  }

  public void zeroHidden() {Arrays.fill(hidden, 0);}

  @Override
  public float[] forward(float[] input) {
    float[] newH = hiddenBuffer;

    // 1. Initialize with Biases (Linear write)
    System.arraycopy(biases, 0, newH, 0, outputSize);

    // 2. Accumulate Inputs (Sequential Read Optimization)
    // Iterate i (Rows) first, then j (Cols)
    for (int i = 0; i < inputSize; i++) {
      float inVal = input[i];
      if (inVal == 0) continue; // Sparse optimization!
      for (int j = 0; j < outputSize; j++) {
        newH[j] += inVal * weightIn.get(i, j);
      }
    }

    // 3. Accumulate Hidden State (Sequential Read Optimization)
    for (int k = 0; k < outputSize; k++) {
      float hVal = hidden[k];
      if (hVal == 0) continue;
      for (int j = 0; j < outputSize; j++) {
        newH[j] += hVal * weightHid.get(k, j);
      }
    }

    // 4. Apply Activation
    for (int j = 0; j < outputSize; j++) {
      newH[j] = (float) Math.tanh(newH[j]);
    }

    if (stopCollection) {
      history.push(new RnnState(input, newH));
    }

    // ... history push and buffer swap ...
    // update hidden state
    float[] bufHolder = hidden;
    hidden = newH;
    hiddenBuffer = bufHolder;
    return hidden;
  }

  public MathUtil.arr2D backward(MathUtil.arr2D dOutArr) {
    // Safety check: If no memory, we can't learn. Return empty.
    if (history == null || history.isEmpty()) return new MathUtil.arr2D(0,0);
    // This array will hold the gradients to send back to the previous layer (e.g. Dense)
    // We fill it in the same order we process: Reverse (t=3, t=2, t=1)
//    float[][] dInputToReturn = new float[history.size()][inputSize];
    dInputToReturn.ensureSize(history.size(), inputSize);
    dInputToReturn.zeroAll();
    Arrays.fill(dNextHidden, 0);

    int c = 0; // Counter for the sequence steps
    // BPTT Loop: Walk backwards from t=End to t=Start so action 3..2..1
    while (!history.isEmpty()) {
      // If we don't copy this, we lose it when we clear dNextHidden below.
      System.arraycopy(dNextHidden, 0, futureError, 0, outputSize);
      Arrays.fill(dNextHidden, 0);
      // 1. Get the Error Signal for THIS specific timestamp
      // dOutArr[0] is the error at the End (t=3), dOutArr[1] is (t=2)...

      // 2. Get CURRENT State (t)
      RnnState state = history.pop();

      // 3. Peek PREVIOUS hidden state (t-1)

      float[] prevHiddenState;
      if (!history.isEmpty()) {prevHiddenState = history.peek().hidden;}
      else {prevHiddenState = prevHiddenStateZERO;}

      // 4. Calculate the "Delta" (The total error at this neuron)
      for (int j = 0; j < outputSize; j++) {
        // We always add the external error (dExternal) + the future error (dNextHidden).
        // If dExternal is 0 for this step, it just adds 0. That's fine.
        float errorSignal = dOutArr.get(c,j) + futureError[j];

        // Derivative of Tanh is (1 - y^2)
        float val = state.hidden[j]; // The value stored was post-tanh
        float derivative = 1 - (val * val);

        deltaVec[j] = errorSignal * derivative;
      }
      // 5. Calculate Gradients & Prepare Next Step
      // Get the row where we will store the gradient for the previous layer
      for (int j = 0; j < outputSize; j++) {
        float delta = deltaVec[j];
        // A. Accumulate Bias Gradient
        dBias[j] += delta;
        // B. Accumulate Input Weight Gradient (Win) & Pass Error DOWN
        for (int i = 0; i < inputSize; i++) {
          dWin.set(i,j,dWin.get(i,j) + delta * state.input[i]);
          // Pass error to the layer below (Dense/Input)
          dInputToReturn.set(c,i,dInputToReturn.get(c,i) + delta * weightIn.get(i,j));
        }
        // C. Accumulate Recurrent Weight Gradient (Whid) & Pass Error BACK
        for (int k = 0; k < outputSize; k++) {
          // Use the PEEKED previous state
          dWhid.set(k,j,dWhid.get(k,j) + delta * prevHiddenState[k]);
          // Pass error back in time to t-1
          dNextHidden[k] += delta * weightHid.get(k,j);
        }
      }
      c++;
    }
    return dInputToReturn;
  }

  @Override
  public void updateWeight(float lr, float beta1, float beta2, float eps) {
    if (mWin == null) throw new IllegalStateException("Adam not initialized. Call turnOnTrainMode(true).");
    adamT++;
    // Pre-calculate bias corrections to save CPU cycles inside the loops
    float biasCorr1 = 1.0f - (float) Math.pow(beta1, adamT);
    float biasCorr2 = 1.0f - (float) Math.pow(beta2, adamT);
    float invBeta1 = 1.0f - beta1;
    float invBeta2 = 1.0f - beta2;

    // Update Input Weights (Input -> Hidden)
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        float g = dWin.get(i,j); // The gradient accumulated from backward()
        // Adam Math
        mWin.set(i,j,beta1 * mWin.get(i,j) + invBeta1 * g);
        vWin.set(i,j,beta2 * vWin.get(i,j) + invBeta2 * g * g);
        float mHat = mWin.get(i,j) / biasCorr1;
        float vHat = vWin.get(i,j) / biasCorr2;
        // Apply Update
        weightIn.set(i,j, weightIn.get(i,j) - (float)(lr * mHat / (Math.sqrt(vHat) + eps)));
      }
    }
    dWin.zeroAll();

    // Update Hidden Weights (Hidden -> Hidden)
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        float g = dWhid.get(i,j);
        mWhid.set(i,j,beta1 * mWhid.get(i,j) + invBeta1 * g);
        vWhid.set(i,j,beta2 * vWhid.get(i,j) + invBeta2 * g * g);
        float mHat = mWhid.get(i,j) / biasCorr1;
        float vHat = vWhid.get(i,j) / biasCorr2;
        weightHid.set(i,j, weightHid.get(i,j) - (float)(lr * mHat / (Math.sqrt(vHat) + eps)));
      }
    }
    dWhid.zeroAll();

    // Update Biases
    for (int j = 0; j < outputSize; j++) {
      float g = dBias[j];
      mBias[j] = beta1 * mBias[j] + invBeta1 * g;
      vBias[j] = beta2 * vBias[j] + invBeta2 * g * g;
      float mHat = mBias[j] / biasCorr1;
      float vHat = vBias[j] / biasCorr2;
      biases[j] -= (float) (lr * mHat / (Math.sqrt(vHat) + eps));

      dBias[j] = 0; // Reset
    }

    zeroHidden();
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
        buffer.putFloat(weightIn.get(i,j));
      }
    }

    // write hidden->hidden weights
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        buffer.putFloat(weightHid.get(i,j));
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

    weightIn = new MathUtil.arr2D(inputSize, outputSize);
    weightHid = new MathUtil.arr2D(outputSize, outputSize);
    biases = new float[outputSize];
    hidden = new float[outputSize];
    hiddenBuffer = new float[outputSize];

    // Read input->hidden weights
    for (int i = 0; i < inputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightIn.set(i,j,buffer.getFloat());
      }
    }

    // Read hidden->hidden weights
    for (int i = 0; i < outputSize; i++) {
      for (int j = 0; j < outputSize; j++) {
        weightHid.set(i,j,buffer.getFloat());
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
        out.append(String.format("%.3f", weightIn.get(i,j))).append(" ,");
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
        out.append(String.format("%.3f", weightHid.get(i,j))).append(" ,");
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

  private static class RnnState {
    float[] input;    // x_t
    float[] hidden;   // h_t (Post-Tanh)

    // THIS HAS TO COPY STATE FYI
    public RnnState(float[] in, float[] hidden) {
      this.input = Arrays.copyOf(in, in.length);
      this.hidden = Arrays.copyOf(hidden, hidden.length);
    }
  }
}