package com.github.ptran779.aegisops.item.brain;

import com.github.ptran779.aegisops.brain.ml.DenseLayer;
import com.github.ptran779.aegisops.brain.ml.ML;
import com.github.ptran779.aegisops.brain.ml.RNNLayer;
import net.minecraft.nbt.CompoundTag;

import java.lang.ref.Cleaner;
import java.util.Random;

public class ChipBrain implements IChipBrain {
  ML model;
  public ChipBrain() {
    System.out.println("A new ChipBrain object created");
    model = new ML();
    // let's add in a bunch of layer dummy model
    DenseLayer l1 = new DenseLayer(20, 15);
    RNNLayer l2 = new RNNLayer(15, 10);

    // initialized random for now. fixme add load serialized
    l1.randomInit(-1, 1);
    l2.randomInit(-1, 1);
    l2.zeroHidden();

    model.addLayer(l1);
    model.addLayer(l2);
  }

  // just checking if obj being toss away correctly
  private static final Cleaner cleaner = Cleaner.create();

  // This inner class defines cleanup logic
  private static class State implements Runnable {
    @Override
    public void run() {
      System.out.println("ChipBrain was tossed away!");
    }
  }

  @Override
  public void printAllWeight() {
    for (int i=0; i<model.getLayers().size(); i++) {
      System.out.println("Layer " + i);
      System.out.println(model.getLayers().get(i));
    }
  }

  @Override
  public void fakeComputeTest() {
    Random random = new Random();
    float[] someIn = new float[20];
    for (int i = 0; i < someIn.length; i++) {
      someIn[i] = random.nextFloat() * 2f - 1f; // -1 .. 1
    }

    long start = System.nanoTime();
    float[] out = model.forward(someIn);
    long end = System.nanoTime();

    float[] normalized = ML.normalized(out);

    System.out.printf("Forward time: %.3f ms\n", (end - start) / 1e6);
    System.out.println("Output probabilities:");
    for (int i = 0; i < normalized.length; i++) {
      System.out.printf("%d: %.2f%%\n", i, normalized[i] * 100f);
    }
  }
}
