package com.github.ptran779.aegisops.brain.api;

import com.github.ptran779.aegisops.brain.ml.DataManager;
import com.github.ptran779.aegisops.brain.ml.ML;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BrainServer {
  // FIXME put a limit on how much can go in queue for safety reason
  public final BlockingQueue<InfDatIn> taskQueueIn = new LinkedBlockingQueue<>();
  public final BlockingQueue<InfDatOut> resultQueueInf = new LinkedBlockingQueue<>();

  public final BlockingQueue<TrainDatIn> taskQueueTrain = new LinkedBlockingQueue<>();
  public final BlockingQueue<TrainDatOut> resultQueueTrain = new LinkedBlockingQueue<>();
  
  private final Thread inferThread;
  private final Thread trainingThread;
  private volatile boolean running = true;

  public BrainServer() {
    inferThread = new Thread(() -> {
      while (running) {
        try {
          inferTick();                    // check queues and process tasks
          Thread.sleep(1000);         // 1 second delay
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }, "BrainInferThread");

    trainingThread = new Thread(() -> {
      while (running) {
        try {
          trainingTick();                    // check queues and process tasks
          Thread.sleep(10000);         // 10 second delay
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }, "BrainInferThread");
  }

  public void stop() {
    running = false;
    inferThread.interrupt();
    trainingThread.interrupt();
  }

  private void inferTick() throws InterruptedException {
    InfDatIn task;
    while ((task = taskQueueIn.poll()) != null) {
      long startNano = System.nanoTime();                 // start timer
      float[] output = task.model.forward(task.vectorInput);
      long endNano = System.nanoTime();                   // end timer
      long durationMs = (endNano - startNano); // convert ns to ms

      System.out.println("[" + startNano + "] BrainInfer: processed task for agent "+ task.agentUUID + " in " + durationMs + "ns");

      resultQueueInf.add(new InfDatOut(task.agentUUID, ML.normalized(output)));
    }
  }

  private void trainingTick() throws InterruptedException {
    TrainDatIn task;
    while ((task = taskQueueTrain.poll()) != null) {
      long startNano = System.nanoTime();                 // start timer
      // copy deep brain state
      ML newModel = task.model.deepCopy();
      newModel.batchSize = task.batchsize;
      newModel.startTraining(task.dataManager, task.maxEpoch, task.stopEpoch);
      long endNano = System.nanoTime();                   // end timer
      long durationMs = (endNano - startNano); // convert ns to ms
      System.out.println("server done training for agent "+ task.agentUUID + " in " + durationMs + "ns" + " dumping new model for now");

      resultQueueTrain.add(new TrainDatOut(task.agentUUID, newModel));
    }
  }

  public static class InfDatIn {
    public UUID agentUUID;
    float[] vectorInput;
    ML model;

    public InfDatIn(UUID agentUUID, float[] vectorInput, ML model) {
      this.agentUUID = agentUUID;
      this.vectorInput = vectorInput;
      this.model = model;
    }
  }

  public static class InfDatOut {
    public UUID agentUUID;
    public float[] decision;

    public InfDatOut(UUID agentUUID, float[] decision) {
      this.agentUUID = agentUUID;
      this.decision = decision;
    }
  }

  public static class TrainDatIn {
    public UUID agentUUID;
    public ML model;
    public DataManager dataManager;
    public int maxEpoch;
    public int stopEpoch;
    public int batchsize;

    public TrainDatIn(UUID agentUUID, DataManager dataManager, ML model, int maxEpoch, int stopEpoch, int batchsize) {
      this.agentUUID = agentUUID;
      this.dataManager = dataManager;
      this.model = model;
      this.maxEpoch = maxEpoch;
      this.stopEpoch = stopEpoch;
      this.batchsize = batchsize;
    }
  }

  public static class TrainDatOut {
    public UUID agentUUID;
    public ML model;

    public TrainDatOut(UUID agentUUID, ML model) {
      this.agentUUID = agentUUID;
      this.model = model;
    }
  }



  public void start() {
    inferThread.start();
    trainingThread.start();
  }
}
