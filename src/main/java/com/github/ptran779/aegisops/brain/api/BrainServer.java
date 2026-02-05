package com.github.ptran779.aegisops.brain.api;

import com.github.ptran779.aegisops.brain.ml.DataManager;
import com.github.ptran779.aegisops.brain.ml.ML;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BrainServer {
  // FIXME put a limit on how much can go in queue for safety reason
  public final BlockingQueue<InfDatIn> TASK_QUEUE_INF = new LinkedBlockingQueue<>();
  public final BlockingQueue<InfDatOut> RESULT_QUEUE_INF = new LinkedBlockingQueue<>();

  public final BlockingQueue<TrainDatIn> TASK_QUEUE_TRAIN = new LinkedBlockingQueue<>();
  public final BlockingQueue<TrainDatOut> RESULT_QUEUE_TRAIN = new LinkedBlockingQueue<>();
  
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
          Thread.sleep(1000);         // 1 second delay
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }, "BrainTrainThread");
  }

  public void stop() {
    running = false;
    inferThread.interrupt();
    trainingThread.interrupt();
  }

  private void inferTick() throws InterruptedException {
    InfDatIn task;
    while ((task = TASK_QUEUE_INF.poll()) != null) {
      long startNano = System.nanoTime();                 // start timer
      float[] output = task.model.forward(task.vectorInput);
      long endNano = System.nanoTime();                   // end timer
      long durationMs = (endNano - startNano); // convert ns to ms

      System.out.println("[" + startNano + "] BrainInfer: processed task for agent "+ task.agentUUID + " in " + durationMs + "ns");

      RESULT_QUEUE_INF.add(new InfDatOut(task.agentUUID, ML.normalized(output)));
    }
  }

  private void trainingTick() throws InterruptedException {
    TrainDatIn task;
    while ((task = TASK_QUEUE_TRAIN.poll()) != null) {
      ML newModel = task.model.deepCopy();
      ML.TrainStat trainResult = newModel.startTraining(task.dataManager);
      RESULT_QUEUE_TRAIN.add(new TrainDatOut(task.targetUUID, task.receiver, task.modelUUD, newModel, trainResult));
    }
  }

  public record InfDatIn(UUID agentUUID, float[] vectorInput, ML model) {}
  public record InfDatOut(UUID agentUUID,float[] decision) {}
  public record TrainDatIn(UUID targetUUID, TARGET_RECEIVER receiver, UUID modelUUD, ML model, DataManager dataManager) {}
  public record TrainDatOut(UUID targetUUID, Object receiver, UUID modelUUID, ML model, ML.TrainStat stats) {}
//  public static class TrainDatIn {
//    public UUID targetUUID;
//    public TARGET_RECEIVER receiver;
//    public UUID modelUUD;
//    public ML model;
//    public DataManager dataManager;
//
//    public TrainDatIn(UUID targetUUID, TARGET_RECEIVER receiver, UUID modelUUD, ML model, DataManager dataManager) {
//      this.targetUUID = targetUUID;
//      this.receiver = receiver;
//      this.modelUUD = modelUUD;
//      this.dataManager = dataManager;
//      this.model = model;
//    }
//  }
//  public static class TrainDatOut {
//    public UUID targetUUID;
//    public TARGET_RECEIVER receiver;
//    public UUID modelUUID;
//    public ML model;
//    public ML.TrainStat stat;
//
//    public TrainDatOut(UUID targetUUID, TARGET_RECEIVER receiver, UUID modelUUID, ML model, ML.TrainStat stat) {
//      this.targetUUID = targetUUID;
//      this.receiver = receiver;
//      this.modelUUID = modelUUID;
//      this.model = model;
//      this.stat = stat;
//    }
//  }

  public void start() {
    inferThread.start();
    trainingThread.start();
  }

  public enum TARGET_RECEIVER {
    PLAYER, AGENT
  }
}
