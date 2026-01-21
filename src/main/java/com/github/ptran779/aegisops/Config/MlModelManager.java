package com.github.ptran779.aegisops.Config;

import com.github.ptran779.aegisops.brain.ml.DataManager;
import com.github.ptran779.aegisops.brain.ml.DenseLayer;
import com.github.ptran779.aegisops.brain.ml.ML;
import com.github.ptran779.aegisops.brain.ml.RNNLayer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MlModelManager {
  private static final Path MODEL_DIR = Path.of("config/aegisops/brainmodel/");
  private static int lastGameTick = 0;
  private static final Map<UUID, Item> mLib = new HashMap<>();

  public static class Item{
    public int lastAccess;
    public ML model;
    DataManager dataManager;

    public Item(ML model, int lastAccess) {
      this.lastAccess = lastAccess;
      this.model = model;
    }
  }

  public static Item getModel(UUID uuid, int gameTick) {
    Item mItem = mLib.get(uuid);
    if (mItem == null) {
      mItem = loadModel(uuid, gameTick);
      mLib.put(uuid, mItem);
    }
    else {mItem.lastAccess = gameTick;}

    // do some cleanup if needed
    if (gameTick - lastGameTick > 24000) {cleanCache(gameTick);}
    lastGameTick = gameTick;
    return mItem;
  }

  public static void saveModel(UUID uuid) {
    Item mItem = mLib.get(uuid);
    if (mItem == null) return; // nothing to save
    System.out.println("Saving " + uuid + " to " + MODEL_DIR);
    byte[] data = mItem.model.serialize();

    try {
      Path file = MODEL_DIR.resolve(uuid.toString() + ".bin"); // add extension
      Files.createDirectories(file.getParent()); // ensure directory exists
      Files.write(file, data); // write bytes to file
      System.out.println("Saved ML model for " + uuid);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Item loadModel(UUID uuid, int gameTick) {
    Path modelf = MODEL_DIR.resolve(uuid.toString() + ".bin");
    if (Files.exists(modelf)) {
      System.out.println("Loading ML model from file " + uuid);
      try {
        byte[] data = Files.readAllBytes(modelf);    // <-- read the file
        ML model = ML.deserialize(data);            // <-- give the bytes to your static method
        if (model == null) return null;

        Item mItem = new Item(model, gameTick);
        mLib.put(uuid, mItem);                       // add to the library
        return mItem;
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Failed to load ML model for " + uuid);
        return null;  //crash me
      }
    }
    return createDummyModel(gameTick);
  }

  private static void cleanCache(int gameTick) {
    mLib.entrySet().removeIf(e -> {
      if (gameTick - e.getValue().lastAccess > 24000) {
        saveModel(e.getKey()); // persist to disk before removing
        return true;           // remove from the cache
      }
      return false;
    });
  }

  public static void cleanAll() {
    System.out.println("Cleaning up ML models ");
    for (UUID uuid : mLib.keySet()) {
      System.out.println("Saving ML model for " + uuid);
      saveModel(uuid);  // persist each model
    }
    mLib.clear();         // remove all from memory
  }

  public static Item createDummyModel(int gameTick) {  //fixme like foreal. this is lazy test
      System.out.println("A new ChipBrain object created");
      ML model = new ML();
      // let's add in a bunch of layer dummy model
//      DenseLayer l1 = new DenseLayer(20, 10);
      RNNLayer l1 = new RNNLayer(20, 10);

      // initialized random for now. fixme add load serialized
      l1.randomInit(-1, 1);

      model.addLayer(l1);

      return new Item(model, gameTick);
    }
}
