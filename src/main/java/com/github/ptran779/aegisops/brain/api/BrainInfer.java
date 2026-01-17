package com.github.ptran779.aegisops.brain.api;

import com.github.ptran779.aegisops.brain.ml.ML;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BrainInfer {
  // FIXME put a limit on how much can go in queue for safety reason
  public final BlockingQueue<taskPayload> taskQueue = new LinkedBlockingQueue<>();
  public final BlockingQueue<resultPayload> resultQueue = new LinkedBlockingQueue<>();
  private final Thread workerThread;
  private volatile boolean running = true;

  public BrainInfer() {
    workerThread = new Thread(() -> {
      while (running) {
        try {
          tick();                    // check queues and process tasks
          Thread.sleep(1000);         // 1 second delay
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }, "BrainInferThread");
  }

  public void stop() {
    running = false;
    workerThread.interrupt();
  }

  private void tick() throws InterruptedException {
    taskPayload task;
    while ((task = taskQueue.poll()) != null) {
      long startNano = System.nanoTime();                 // start timer
      float[] output = task.model.forward(task.vectorInput);
      long endNano = System.nanoTime();                   // end timer
      long durationMs = (endNano - startNano); // convert ns to ms

      System.out.println("[" + startNano + "] BrainInfer: processed task for agent "+ task.agentUUID + " in " + durationMs + "ns");

      resultQueue.add(new resultPayload(task.agentUUID, ML.normalized(output)));
    }
  }

  public static class taskPayload{
    public UUID agentUUID;
    float[] vectorInput;
    ML model;

    public taskPayload(UUID agentUUID, float[] vectorInput, ML model) {
      this.agentUUID = agentUUID;
      this.vectorInput = vectorInput;
      this.model = model;
    }
  }

  public static class resultPayload{
    public UUID agentUUID;
    public float[] decision;

    public resultPayload(UUID agentUUID, float[] decision) {
      this.agentUUID = agentUUID;
      this.decision = decision;
    }
  }

  public void start() {
    workerThread.start();
  }
}
