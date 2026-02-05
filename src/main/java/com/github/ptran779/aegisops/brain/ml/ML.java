package com.github.ptran779.aegisops.brain.ml;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ML {
  public static final int VERSION = 1;   // used in serialized/deserialized for safety check later
  protected List<AbstractLayer> layers;
  protected Supplier<Float> scoreFunc;
  protected Function<float[], Integer> actionFunc;  // fixme what is it for?

  public float posLearnRate = 0.1f;
  public float negLearnRate = 1f;
  public float lr = 0.001f, beta1 = 0.9f, beta2 = 0.99f, eps = 1e-8f;
  public int batchSize = 8;
  public int maxEpochs = 1, patience = 3;
  public float minDelta = 5;
  // used to prepare dataset. For convenience of UI management, I just leave it here
  public float valFrac = 0.2f;
  public float testFrac = 0.2f;
  public boolean dataCollector = false;

  protected MathUtil.arr2D dErrs;   // for chain event derivative back pass

  public ML() {
    layers = new ArrayList<>();
  }

  public ML deepCopy(){
    ML copy = new ML();
    for (AbstractLayer layer : layers){
      AbstractLayer copyLayer = layer.clone();
      copy.layers.add(copyLayer);
    }
    copy.scoreFunc = scoreFunc;
    copy.actionFunc = actionFunc;
    copy.posLearnRate = posLearnRate;
    copy.negLearnRate = negLearnRate;
    copy.lr = lr;
    copy.beta1 = beta1;
    copy.beta2 = beta2;
    copy.eps = eps;
    copy.batchSize = batchSize;
    copy.maxEpochs = maxEpochs;
    copy.patience = patience;
    copy.minDelta = minDelta;
    copy.valFrac = valFrac;
    copy.testFrac = testFrac;

    return copy;
  }

  public int getInsize(){
    if (layers.isEmpty()) {return 0;}
    return layers.get(0).getInputSize();
  }

  public int getOutsize(){
    if (layers.isEmpty()) {return 0;}
    return layers.get(layers.size()-1).getOutputSize();
  }

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
        dErrBack.set(nchain-j-1, action, score>0 ? score*posLearnRate : -score*negLearnRate);
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
    if (evalX == null || evalX.length == 0) {return 0;}
//    if (trainX.length == 0 || evalX.length == 0) return 0;

    if (trainX != null && trainX.length > 0) {
      // 2. Shuffle Training Indices
      // We can't shuffle the big arrays directly easily, so we shuffle an index list
      Integer[] indices = new Integer[trainX.length];  //fixme optimize me with preallocation
      for (int i = 0; i < indices.length; i++) indices[i] = i;
      // Fisher-Yates shuffle
      java.util.Collections.shuffle(Arrays.asList(indices));

      // 3. Training Loop
      pauseTraining(false); // Enable Dropout/Noise if any

      for (int i = 0; i < trainX.length; i += batchSize) {
        // A. Slice the Batch
        int end = Math.min(i + batchSize, trainX.length);
        int actualBatchSize = end - i;
        //fixme need optimization with batch slicing and pre allocated block
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
    }
    // 4. Evaluation Loop
    pauseTraining(true);

    float totalAlignment = 0;
    float totalPossibleScore = 0; // To normalize the result later fixme

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
//    System.out.println("Epoch Eval -> Weighted Alignment: " + String.format("%.2f", weightedAccuracy * 100) + "%");

    return weightedAccuracy;
  }

  public record TrainStat(
      long trainTimeNs,
      float startScore,
      float endScore,
      float epochRun
  ) {}

  //MASTER START TRAING... config this
  public TrainStat startTraining(DataManager dataMan) {
    long topStartTime = System.nanoTime();
    turnOnTrainMode(true);
    // prepare data should be done ahead of time and only call fetch since you might want to extend training
    DataManager.ItemPack[] epocSet = dataMan.fetchTrainEpoc();
    DataManager.ItemPack testSet = dataMan.fetchTest();
    int epocCount = 0;
    float bestScore = -1000;
    float lastBestScore = -1000;
    int failC = 0;

    // get some data pre train
    float preScore = epoch(new float[0][][], new int[0][], new float[0][],
        testSet.input, testSet.action, testSet.output);
    System.out.printf("PreTrain Evaluation Score: %.5f (%s%%)%n", preScore, String.format("%.2f", preScore * 100));

    // epoc loop
    while (epocCount < maxEpochs && failC < patience) {
      //timer
      long startTime = System.nanoTime();
      float score = epoch(epocSet[0].input, epocSet[0].action, epocSet[0].output, epocSet[1].input, epocSet[1].action, epocSet[1].output);
      //stop time
      long endTime = System.nanoTime();
      epocCount++;
      if (score > lastBestScore + minDelta) {
        failC = 0; // We improved significantly! Reset counter.
        lastBestScore = score;
      } else {
        failC++;   // We didn't improve enough. Count a failure.
      }
      float durationMs = (endTime - startTime) / 1_000_000f;
      bestScore = Math.max(score, bestScore);
      System.out.printf("Epoch [%3d] | Time: %6.1fms | Score: %.5f | Best: %.5f | Fail: %d/%d%n",
          epocCount, durationMs, score, bestScore, failC, patience);
    }

    // run final test
    float postScore = epoch(new float[0][][], new int[0][], new float[0][],
        testSet.input, testSet.action, testSet.output);
    System.out.printf("Final Evaluation Score: %.5f (%s%%)%n", postScore, String.format("%.2f", postScore * 100));

    turnOnTrainMode(false);
    return new TrainStat(System.nanoTime()-topStartTime, preScore, postScore, epocCount);
  }

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

  // for disk IO
  public byte[] diskSerialize() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);

      // version for safety check
      dos.writeInt(VERSION);

      // train config param
      dos.writeFloat(lr);dos.writeFloat(beta1);dos.writeFloat(beta2);dos.writeFloat(eps);
      dos.writeFloat(posLearnRate);dos.writeFloat(negLearnRate);
      dos.writeInt(batchSize);
      dos.writeInt(maxEpochs);dos.writeInt(patience);dos.writeFloat(minDelta);
      dos.writeFloat(valFrac);dos.writeFloat(testFrac);

      dos.writeBoolean(dataCollector);

      // number of layers
      dos.writeInt(layers.size());

      // for each layer dynamic
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
  public static ML diskDeserialize(byte[] data) {
    try {
      ML model = new ML();
      DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
      // read first for version check
      int modelVersion = dis.readInt();
      if (modelVersion != VERSION) {
        System.out.println("[Aegisops Critical] model has incorrect version. Got " + modelVersion + ", expected " + VERSION);
        return null;
      }
      // config load
      modelConfigByteLoad(model, dis);
      // layer load
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
        else if (typeId == LeakyReluLayer.LAYER_ID) { layer = new LeakyReluLayer(0);
        } else throw new RuntimeException("[Aegisops crash] Unknown layer type in deserialization");  // maybe return null

        layer.deserialize(ByteBuffer.wrap(layerBytes));
        model.addLayer(layer);
      }

      return model;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[Aegisops Critical] Failed to deserialize ML]");
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

  // for network composition
  //input size, numberOfLayer, layer1O, layer2O... last layer output has the output of the model
  public byte[] modelSimpleSerialize() {
    // 1. Calculate exact size needed: 11 int/float + 2 per layer
    int totalBytes = 8 + (layers.size() * 8);
    ByteBuffer buffer = ByteBuffer.allocate(totalBytes);

    // 2. Write Header (Matches screen.deserializeModel reading order)
    buffer.putInt(getInsize());      // Reads into 'modelInputSize'
    buffer.putInt(layers.size());    // Reads into 'numLayers'

    // 3. Write Body
    for (AbstractLayer layer : layers) {
      buffer.putInt(layer.getLayerID());   // Reads into addLayer(type...)
      buffer.putInt(layer.getOutputSize());// Reads into addLayer(...node)
    }

    return buffer.array();
  }
  public static ML createModelFromSerialization(byte[] data, boolean ranInit) {
    try {
      if (data == null || data.length == 0) return null;  // for clearing model
      ML model = new ML();
      DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
      int inputSize = dis.readInt();
      int numLayers = dis.readInt();

      for (int i = 0; i < numLayers; i++) {
        int typeId = dis.readInt();
        int outputSize = dis.readInt();

        AbstractLayer layer;
        if (typeId == DenseLayer.LAYER_ID) layer = new DenseLayer(inputSize, outputSize);
        else if(typeId == ReluLayer.LAYER_ID) layer = new ReluLayer(outputSize);
        else if (typeId == RNNLayer.LAYER_ID) layer = new RNNLayer(inputSize, outputSize);
        else if (typeId == LeakyReluLayer.LAYER_ID) { layer = new LeakyReluLayer(outputSize);}
        else throw new RuntimeException("Unknown layer type in deserialization");

        model.addLayer(layer);
        inputSize = outputSize;
      }

      if (ranInit) model.initRandom(-1,1);
      return model;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  // for config network
  public static byte[] trainConfigSerialize(@Nullable ML model) {
    ByteBuffer buffer = ByteBuffer.allocate(48);
    if (model != null) {  // if model exist
      buffer.putFloat(model.lr);
      buffer.putFloat(model.beta1);
      buffer.putFloat(model.beta2);
      buffer.putFloat(model.eps);
      buffer.putFloat(model.posLearnRate);
      buffer.putFloat(model.negLearnRate);
      buffer.putInt(model.batchSize);
      buffer.putInt(model.maxEpochs);
      buffer.putInt(model.patience);
      buffer.putFloat(model.minDelta);
      buffer.putFloat(model.valFrac);
      buffer.putFloat(model.testFrac);
    } else {  // just based config
      buffer.putFloat(0.001f);
      buffer.putFloat(0.9f);
      buffer.putFloat(0.99f);
      buffer.putFloat(0.00000001f);
      buffer.putFloat(0.1f);
      buffer.putFloat(1f);
      buffer.putInt(32);
      buffer.putInt(1);
      buffer.putInt(3);
      buffer.putFloat(5);
      buffer.putFloat(0.2f);
      buffer.putFloat(0.2f);
    }
    return buffer.array();
  }

  public void trainConfigDeserialize(byte[] data) {
      try {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        modelConfigByteLoad(this, dis);
      } catch (IOException ignored) {}
  }

  private static void modelConfigByteLoad(ML model, DataInputStream dis) throws IOException {
    model.lr = dis.readFloat();
    model.beta1 = dis.readFloat();
    model.beta2 = dis.readFloat();
    model.eps = dis.readFloat();
    model.posLearnRate = dis.readFloat();
    model.negLearnRate = dis.readFloat();
    model.batchSize = dis.readInt();
    model.maxEpochs = dis.readInt();
    model.patience = dis.readInt();
    model.minDelta = dis.readFloat();
    model.valFrac = dis.readFloat();
    model.testFrac = dis.readFloat();
    model.dataCollector = dis.readBoolean();
  }

  private void initRandom(float min, float max) {for (AbstractLayer layer : layers) {layer.randomInit(min, max);}}
}
