package com.github.ptran779.aegisops.brain.ml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/// fixme optimized new usage in all layer during back prog
public class ML {
  protected List<AbstractLayer> layers;
  protected Supplier<Float> scoreFunc;
  protected Function<float[], Integer> actionFunc;

  protected MathUtil.arr2D dErrs;   // for chain event derivative back pass  fixme
  public float posLearnRate = 0.1f;
  public float negLearnRate = 1f;
  public float lr = 0.001f, beta1 = 0.9f, beta2 = 0.99f, eps = 0.001f;
  public int batchSize = 1;

  public ML() {
    layers = new ArrayList<>();
  }

  public ML deepCopy(){
    ML copy = new ML();
    for (AbstractLayer layer : layers){
      AbstractLayer copyLayer = layer.clone();
      copy.layers.add(copyLayer);
    }
    return copy;
  }

  public List<AbstractLayer> getLayers(){return layers;}

  // return false if layer violate size coupling
  public boolean addLayer(AbstractLayer layer) {
    if (layers.isEmpty()) {
      layers.add(layer);
      return true;
    } else {
      if (layers.get(layers.size() - 1).getOutputSize() != layer.getInputSize()) {
        System.out.println("Layer fail to add with output size " + layers.get(layers.size() - 1).getOutputSize() + "trying to " +
            "couple with input size " + layer.getInputSize());
        return false;
      } else {
        layers.add(layer);
        return true;
      }
    }
  }

  public float[] forward(float[] input) {
    for (AbstractLayer layer : layers) {input = layer.forward(input);}
    return input;
  }

  private void pauseTraining(boolean pause) {for (AbstractLayer layer : layers) {layer.pauseTraining(pause);}}

  private void turnOnTrainMode(boolean mode) {
    for (AbstractLayer layer : layers) {layer.turnOnTrainMode(mode);}
    if (mode){
      dErrs = new MathUtil.arr2D(100, layers.get(layers.size() - 1).getOutputSize());  //FIXME VERY TMP BUFFER STATE
    } else {
      dErrs = null;
    }
  }

  // do 1 full step, with weight update and return the score. Also, please turn on the damn train mode first
  // give a [batchSize][numChain][dataUnit]
  private void batchStep(float[][][] batchX, int[][] batchA, float[][] batchY) {
    for (int i=0; i< batchX.length; i++) {
      // set error to all 0
      MathUtil.arr2D dErrBack = dErrs;
      dErrBack.zeroAll();
      float[][] chainInput = batchX[i];
      //clear Hidden data.. mostly for rnn reset
      for(AbstractLayer layer : layers) {layer.zeroHidden();}
      // forward
      int nchain = chainInput.length;
      for (int j=0; j<nchain; j++) {
        float[] chainOut = chainInput[j];
        forward(chainOut);
        // eval score & convert to error
        float score = batchY[i][j];
        int action = batchA[i][j];
        dErrBack.set(nchain-j-1, action, score>0 ? -score*posLearnRate : score*negLearnRate);
      }

      // backward
      for (int k =layers.size()-1; k >= 0; k--) {
        dErrBack = layers.get(k).backward(dErrBack);
      }
    }

    // weight update
    for (AbstractLayer layer : layers) {layer.updateWeight(lr, beta1, beta2, eps);}
  }

  // do 1 training epoch
  private float epoch(float[][][] trainX, int[][] trainA, float[][] trainY,
                      float[][][] evalX, int[][] evalA, float[][] evalY) {

    // 1. Validation & Setup
    if (batchSize < 1) { System.out.println("Batch size < 1"); return 0; }
    if (trainX.length == 0 || evalX.length == 0) return 0;

    // 2. Shuffle Training Indices
    // We can't shuffle the big arrays directly easily, so we shuffle an index list
    Integer[] indices = new Integer[trainX.length];  //fixme optimiza
    for (int i = 0; i < indices.length; i++) indices[i] = i;
    // Fisher-Yates shuffle
    java.util.Collections.shuffle(Arrays.asList(indices));

    // 3. Training Loop
    pauseTraining(false); // Enable Dropout/Noise if any

    for (int i = 0; i < trainX.length; i += batchSize) {
      // A. Slice the Batch
      int end = Math.min(i + batchSize, trainX.length);
      int actualBatchSize = end - i;
      //fixme need optimization
      float[][][] batchX = new float[actualBatchSize][][];
      int[][] batchA = new int[actualBatchSize][];
      float[][] batchY = new float[actualBatchSize][];

      // Fill the batch using the shuffled indices
      for (int b = 0; b < actualBatchSize; b++) {
        int idx = indices[i + b];
        batchX[b] = trainX[idx];
        batchA[b] = trainA[idx];
        batchY[b] = trainY[idx];
      }

      // B. Train Step
      batchStep(batchX, batchA, batchY);
    }

    // 4. Evaluation Loop
    pauseTraining(true);

    float totalAlignment = 0;
    float totalPossibleScore = 0; // To normalize the result later

    for (int i = 0; i < evalX.length; i++) {
      float[][] chainInputs = evalX[i];
      int[] chainActions = evalA[i];
      float[] chainScores = evalY[i];

      for (AbstractLayer l : layers) l.zeroHidden();

      for (int t = 0; t < chainInputs.length; t++) {
        // Predict
        float[] output = forward(chainInputs[t]);

        int actionTaken = chainActions[t];
        float realScore = chainScores[t];     // e.g., +50 or -100
        float predictedVal = output[actionTaken]; // e.g., 0.8 or -0.5

        // YOUR METRIC: Scale * Score
        // If both positive: + * + = + (Good)
        // If both negative: - * - = + (Good)
        // If mismatch:      + * - = - (Bad)
        float alignment = predictedVal * realScore;
        totalAlignment += alignment;

        // For normalization: What if the AI was PERFECT?
        // Perfect means predicting 1.0 for positive scores and -1.0 for negative scores.
        // So "Perfect Alignment" is just abs(realScore).
        totalPossibleScore += Math.abs(realScore);
      }
    }

    // Result: A percentage of how well the AI captured the available rewards
    // 1.0 = Perfect alignment (God mode)
    // 0.0 = Random guessing / Neutral
    // -1.0 = Perfect failure (AI loves death and hates life)
    float weightedAccuracy = (totalPossibleScore == 0) ? 0 : totalAlignment / totalPossibleScore;
    System.out.println("Epoch Eval -> Weighted Alignment: " + String.format("%.2f", weightedAccuracy * 100) + "%");

    return weightedAccuracy;
  }


  public void startTraining(DataManager dataMan, int maxEpochs, int stopIfNotImproved) {
    turnOnTrainMode(true);
    // prepare data should be done ahead of time and only call fetch since you might want to extend training
    DataManager.ItemPack[] epocSet = dataMan.fetchTrainEpoc();
    int epocCount = 0;
    float lastScore = -1000;
    float bestScore = -1000;
    int failC = 0;

    while (epocCount < maxEpochs && failC < stopIfNotImproved) {
      //timer
      long startTime = System.nanoTime();
      float score = epoch(epocSet[0].input, epocSet[0].action, epocSet[0].output, epocSet[1].input, epocSet[1].action, epocSet[1].output);
      //stop time
      long endTime = System.nanoTime();
      epocCount++;
      failC = lastScore > score ? failC + 1 : 0;
      lastScore = score;
      float durationMs = (endTime - startTime) / 1_000_000f;
      bestScore = Math.max(score, bestScore);
      System.out.printf("Epoch [%3d] | Time: %6.1fms | Score: %.5f | Best: %.5f | Fail: %d/%d%n",
          epocCount, durationMs, score, bestScore, failC, stopIfNotImproved);
    }

    System.out.println("train done I think"); // place holder don change this
    turnOnTrainMode(false);
  }

//  private void

  public void setScoreFunc(Supplier<Float> scoreFunc) {this.scoreFunc = scoreFunc;}
  public void setActionFunc(Function<float[],Integer> actionFunc) {this.actionFunc = actionFunc;}

  //for gameplay inference only
  public float computeScore() {return scoreFunc.get();}
  public int computeAction(float[] options) {return actionFunc.apply(options);}

  // mostly for visual
  public static float[] normalized(float[] input) {
    if (input == null || input.length == 0) return new float[0];

    // Find max for numerical stability
    float max = input[0];
    for (float v : input) if (v > max) max = v;

    // Compute exponentials and sum
    float sum = 0f;
    float[] exp = new float[input.length];
    for (int i = 0; i < input.length; i++) {
      exp[i] = (float) Math.exp(input[i] - max);
      sum += exp[i];
    }

    // Normalize to probabilities
    for (int i = 0; i < input.length; i++) {
      exp[i] /= sum;
    }

    return exp;
  }

  public byte[] serialize() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);

      // 1. number of layers
      dos.writeInt(layers.size());

      // 2. for each layer
      for (AbstractLayer layer : layers) {
        // write a simple type ID
        dos.writeInt(layer.getLayerID());
        // write layer bytes
        byte[] layerBytes = layer.serialize();
        dos.writeInt(layerBytes.length); // so we know how many bytes to read when deserializing
        dos.write(layerBytes);
      }

      dos.flush();
      return baos.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return new byte[0];
    }
  }

  public static ML deserialize(byte[] data) {
    try {
      ML model = new ML();
      DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

      int numLayers = dis.readInt();
      for (int i = 0; i < numLayers; i++) {
        int typeId = dis.readInt();
        int len = dis.readInt();
        byte[] layerBytes = new byte[len];
        dis.readFully(layerBytes);

        AbstractLayer layer;
        if (typeId == DenseLayer.LAYER_ID) layer = new DenseLayer(0, 0);
        else if(typeId == ReluLayer.LAYER_ID) layer = new ReluLayer(0);
        else if (typeId == RNNLayer.LAYER_ID) layer = new RNNLayer(0, 0);
        else throw new RuntimeException("Unknown layer type in deserialization");

        layer.deserialize(ByteBuffer.wrap(layerBytes));
        model.addLayer(layer);
      }

      return model;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Number of Layer: ").append(layers.size()).append("\n");
    for (AbstractLayer layer : layers) {
      sb.append(layer.toString());
    }
    return sb.toString();
  }
}
