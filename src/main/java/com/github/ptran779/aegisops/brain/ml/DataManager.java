package com.github.ptran779.aegisops.brain.ml;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// use to manage data for training
public class DataManager {
  private static final int DATA_VERSION = 1;
  public List<List<itemUnit>> rawDat; // raw storage
  public ItemPack trainPack;
  public ItemPack valPack;
  public ItemPack testDat;
  private final Random rng = new Random();

  public void readCsvBody(java.io.BufferedReader br, int inSize) {
    try {
      String line;
      List<itemUnit> currentSequence = new ArrayList<>();
      int lastGameID = -1;
      boolean isFirstRow = true;

      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) continue;

        String[] parts = line.split(",");

        // Safety: 1 (ID) + Inputs + 1 (Action) + 1 (Score)
        if (parts.length < 1 + inSize + 2) continue;
        try {
          // 1. Parse Game ID
          int gameID = Integer.parseInt(parts[0].trim());
          // 2. Handle Sequence Switch
          if (isFirstRow) {
            lastGameID = gameID;
            isFirstRow = false;
          } else if (gameID != lastGameID) {
            // ID changed! Dump the previous sequence into rawDat
            if (!currentSequence.isEmpty()) {
              // We add a COPY because we clear the buffer next
              this.rawDat.add(new ArrayList<>(currentSequence));
              currentSequence.clear();
            }
            lastGameID = gameID;
          }

          // 3. Parse Data (Hot Loop)
          itemUnit unit = new itemUnit();
          unit.input = new float[inSize];
          // Parse Inputs
          int offset = 1; // Skip ID
          for (int i = 0; i < inSize; i++) {
            unit.input[i] = Float.parseFloat(parts[offset + i].trim());
          }

          // Parse Action & Score (Last 2 cols)
          int actionIdx = offset + inSize;
          unit.action = Integer.parseInt(parts[actionIdx].trim());
          unit.score = Float.parseFloat(parts[actionIdx + 1].trim());

          // 4. Add to Buffer
          currentSequence.add(unit);

        } catch (NumberFormatException e) {
          continue; // Skip junk lines
        }
      }

      // 5. Final Dump (Don't forget the last batch!)
      if (!currentSequence.isEmpty()) {
        this.rawDat.add(currentSequence);
      }

    } catch (java.io.IOException e) {
      e.printStackTrace();
    }
  }

  public static class itemUnit{
    public float[] input;
    public int action;
    public float score;
  }

  public byte[] diskSerialize() {
    if (rawDat == null || rawDat.isEmpty() || rawDat.get(0).isEmpty()) {return new byte[0];}
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      int unitInputSize = rawDat.get(0).get(0).input.length;

      // 1. Header: Version
      dos.writeInt(DATA_VERSION);  // write version
      dos.writeInt(unitInputSize);  // write the length of itemUnit including action & score
      // 2. Data Structure: List<List<itemUnit>>
      // Write number of event chain (Outer List Size)
      int chainSize = rawDat.size();
      dos.writeInt(chainSize);  // write number of chain
      for (int i = 0; i < chainSize; i++) {
        int seqSize = rawDat.get(i).size();
        dos.writeInt(seqSize);  // write number of sequence
        for (int j = 0; j < seqSize; j++) {
          itemUnit unit = rawDat.get(i).get(j);
          for (int k = 0; k < unitInputSize; k++) {
            dos.writeFloat(unit.input[k]);
          }
          dos.writeInt(unit.action);
          dos.writeFloat(unit.score);
        }
      }

      dos.flush();
      return baos.toByteArray();

    } catch (Exception e) {
      e.printStackTrace();
      return new byte[0];
    }
  }

  public static DataManager diskDeserialize(byte[] data) {
    // 1. Fast fail on empty data
    if (data == null || data.length == 0) return null;
    try {
      DataManager dm = new DataManager();
      DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
      // 2. Version Check
      int version = dis.readInt();
      if (version != DATA_VERSION) {
        System.out.println("[Aegisops Critical] Data version mismatch. Got " + version + ", expected " + DATA_VERSION);
        return null;
      }
      // 3. Read Structure Config
      int featureSize = dis.readInt();
      // 4. Read Chains
      int chainSize = dis.readInt();
      for (int i = 0; i < chainSize; i++) {
        int seqSize = dis.readInt();
        List<itemUnit> chain = new ArrayList<>(seqSize); // Pre-allocate for speed

        for (int j = 0; j < seqSize; j++) {
          itemUnit unit = new itemUnit();
          // A. Read Inputs (using the calculated global size)
          unit.input = new float[featureSize];
          for (int k = 0; k < featureSize; k++) {unit.input[k] = dis.readFloat();}
          // B. Read Action & Score
          unit.action = dis.readInt();
          unit.score = dis.readFloat();
          chain.add(unit);
        }
        dm.rawDat.add(chain);
      }

      return dm;

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[Aegisops Critical] Failed to deserialize Training Data");
      return null;
    }
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

  public static void shuffleDat(List<?> raw){Collections.shuffle(raw);}

  // universal handler to get stuff data ready. another func will manage shuffling and patch it ready for epoc
//  public void prepareData(float valF, float testF){
//    if (rawDat.isEmpty()) return;
//    //shuffle all data
//    shuffleDat(rawDat);
//
//    //split test group first for safety
//    int total = rawDat.size();
//    int testIdx = (int) (total * testF);
//
//    this.testDat = bakeData(rawDat.subList(0, testIdx));
//
//    List<List<itemUnit>> workingSet = rawDat.subList(testIdx, total);
//
//    int workSize = workingSet.size();
//    int split = (int) (workSize * (1.0f - valF)); // approximate split
//
//    // 3. BAKE THE DATA NOW!
//    // Convert Lists to Arrays ONCE.
//    this.trainPack = bakeData(workingSet.subList(0, split));
//    this.valPack = bakeData(workingSet.subList(split, workSize));
//  }

  public void prepareData(float valF, float testF){
    if (rawDat.isEmpty()) { System.out.println("PREPARE DATA ERROR: rawDat is empty!"); return; }
    shuffleDat(rawDat);

    int total = rawDat.size();
    int testIdx = (int) (total * testF);

    // [DEBUG PRINTS]
    System.out.println("--- Data Split Debug ---");
    System.out.println("Total Samples: " + total);
    System.out.println("Test Index (Size): " + testIdx);

    this.testDat = bakeData(rawDat.subList(0, testIdx));

    List<List<itemUnit>> workingSet = rawDat.subList(testIdx, total);
    int workSize = workingSet.size();
    int split = (int) (workSize * (1.0f - valF - testF));

    this.trainPack = bakeData(workingSet.subList(0, split));
    this.valPack = bakeData(workingSet.subList(split, workSize));

    System.out.println("Train Size: " + (split) + " | Val Size: " + (workSize - split));
    System.out.println("------------------------");
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

  public ItemPack fetchTest(){
    return testDat;
  }
}