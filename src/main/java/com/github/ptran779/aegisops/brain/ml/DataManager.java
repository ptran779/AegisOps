package com.github.ptran779.aegisops.brain.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// use to manage data for training
public class DataManager {
  public List<List<itemUnit>> rawDat; // raw storage
  public ItemPack trainPack;
  public ItemPack valPack;
  public ItemPack testDat;
  private final Random rng = new Random();

  public static class itemUnit{
    public float[] input;
    public int action;
    public float score;
  }

  public static class ItemPack {
    // [Batch][TimeStep][Feature]
    public float[][][] input;
    public int[][] action;
    public float[][] output;
    public int size;

    public ItemPack(float[][][] input, int[][] action, float[][] output) {
      this.input = input;
      this.action = action;
      this.output = output;
      this.size = input.length;
    }

    // ULTRA-FAST Zero-Allocation Shuffle
    // We just swap the pointers of the rows. We don't move actual data.
    public void shuffle(Random rng) {
      for (int i = size - 1; i > 0; i--) {
        int index = rng.nextInt(i + 1);

        // Swap Input Pointers
        float[][] tmpIn = input[i];
        input[i] = input[index];
        input[index] = tmpIn;

        // Swap Action Pointers
        int[] tmpAct = action[i];
        action[i] = action[index];
        action[index] = tmpAct;

        // Swap Output Pointers
        float[] tmpOut = output[i];
        output[i] = output[index];
        output[index] = tmpOut;
      }
    }
  }

  /// expected dat sequence : time series chain event,
  /// item 1: a..a..a..a
  /// item 2: a..a..a
  /// item 3: a..a..a..a ...
  /// store this raw, then make them identical length for training
  /// also, data is store in input,output concat 1D array for easier & faster storage
  public DataManager() {
    this.rawDat = new ArrayList<>();
  }

  public void add(List<itemUnit> data) {this.rawDat.add(data);}
  public void flush_data(){
    this.rawDat.clear();
    this.trainPack = null;
    this.valPack = null;
    this.testDat = null;
  }

  // call once every new train session
  private static ItemPack bakeData(List<List<itemUnit>> data) {
    if (data == null || data.isEmpty()) return null;
    int batchSize = data.size();

    float[][][] input = new float[batchSize][][];
    int[][] action = new int[batchSize][];
    float[][] output = new float[batchSize][];

    for (int i = 0; i < batchSize; i++) {
      List<itemUnit> chain = data.get(i);
      int timeSteps = chain.size();
      // Assuming homogeneous feature size for the first element
      // (Safety check omitted for speed)

      input[i] = new float[timeSteps][];
      action[i] = new int[timeSteps];
      output[i] = new float[timeSteps];

      for (int j = 0; j < timeSteps; j++) {
        itemUnit unit = chain.get(j);
        // POINTER COPY ONLY - No new float[] creation for data
        input[i][j] = unit.input;
        action[i][j] = unit.action;
        output[i][j] = unit.score;
      }
    }
    return new ItemPack(input, action, output);
  }

  // REPLACES packData AND prepareData
  // Handles variable lengths naturally (Jagged Arrays)
  public static ItemPack packData(List<List<itemUnit>> data) {
    if (data.isEmpty() || data.get(0).isEmpty()) return null;
    int batchSize = data.size();

    // 1. Allocate ONLY the Batch dimension first (The "Spine")
    float[][][] input = new float[batchSize][][];
    int[][] action = new int[batchSize][];
    float[][] output = new float[batchSize][];

    for (int i = 0; i < batchSize; i++) {
      List<itemUnit> chain = data.get(i);
      int timeSteps = chain.size();       // Use THIS chain's specific length
      int featSize = chain.get(0).input.length;

      // 2. Allocate the specific length for this game/chain
      input[i] = new float[timeSteps][featSize];
      action[i] = new int[timeSteps];
      output[i] = new float[timeSteps];

      // 3. Fill data
      for (int j = 0; j < timeSteps; j++) {
        itemUnit unit = chain.get(j);
        input[i][j] = unit.input;
        action[i][j] = unit.action; // No cast needed
        output[i][j] = unit.score;
      }
    }
    return new ItemPack(input, action, output);
  }

  public static void shuffleDat(List<?> raw){Collections.shuffle(raw);}

  // universal handler to get stuff data ready. another func will manage shuffling and patch it ready for epoc
  public void prepareData(float testF, float valF){
    if (rawDat.isEmpty()) return;
    //shuffle all data
    shuffleDat(rawDat);

    //split test group first for safety
    int total = rawDat.size();
    int testIdx = (int) (total * testF);

    List<List<itemUnit>> workingSet = rawDat.subList(testIdx, total);

    int workSize = workingSet.size();
    int split = (int) (workSize * (1.0f - valF)); // approximate split

    // 3. BAKE THE DATA NOW!
    // Convert Lists to Arrays ONCE.
    this.trainPack = bakeData(workingSet.subList(0, split));
    this.valPack = bakeData(workingSet.subList(split, workSize));
  }

  // fetch the data, use every epoc
  public ItemPack[] fetchTrainEpoc(){
    ItemPack[] readyPack = new ItemPack[2];
    //just shuffle the thing and ready to ship out
    if (trainPack != null) trainPack.shuffle(rng);

    readyPack[0] = trainPack;
    readyPack[1] = valPack;
    return readyPack;
  }
}