package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.Config.MlModelManager;
import com.github.ptran779.aegisops.brain.api.BrainServer;
import com.github.ptran779.aegisops.brain.ml.DataIO;
import com.github.ptran779.aegisops.brain.ml.DataManager;
import com.github.ptran779.aegisops.brain.ml.ML;
import com.github.ptran779.aegisops.server.ForgeServerEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;


public class BrainChipItem extends Item {
  public BrainChipItem(Properties pProperties) {
    super(pProperties);
  }

  public static UUID getOrCreateUUID(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag(); // creates NBT if missing
    if (!tag.contains("chipUUID")) {
      UUID uuid = UUID.randomUUID();
      tag.putUUID("chipUUID", uuid);  // vanilla helper for UUIDs
      return uuid;
    } else {
      return tag.getUUID("chipUUID");
    }
  }

  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!level.isClientSide) {
      long tStart = System.nanoTime(); // START TOTAL TIMER

      if (player.isShiftKeyDown()) {
        UUID chipId = getOrCreateUUID(stack);
        ML model = MlModelManager.getModel(chipId, (int) level.getGameTime()).model;
        System.out.println(model.toString());
      } else {
        System.out.println("--- START MANUAL TRAINING ---");

        // 1. Get Model
        long t1 = System.nanoTime();
        UUID chipId = getOrCreateUUID(stack);
        ML model = MlModelManager.getModel(chipId, (int) level.getGameTime()).model;
        model.batchSize = 8;
        long t2 = System.nanoTime();

        // 2. Load CSV (The Danger Zone)
        DataManager superDAT = new DataManager();
        superDAT.rawDat = DataIO.loadFromCSV("config/aegisops/dummydata/1.csv");
        long t3 = System.nanoTime();

        // 3. Prepare Data
        superDAT.prepareData(0.1f, 0.1f);
        long t4 = System.nanoTime();

        // 4. Queue Task
        System.out.println("TASK TRAIN AWAY");
        ForgeServerEvent.BRAIN_INFER.taskQueueTrain.add(new BrainServer.TrainDatIn(UUID.randomUUID(), superDAT, model, 20, 3, 8));
        long t5 = System.nanoTime();

        // --- REPORT CARD ---
        float getModelTime = (t2 - t1) / 1_000_000f;
        float loadCsvTime  = (t3 - t2) / 1_000_000f; // Expect this to be high
        float prepTime     = (t4 - t3) / 1_000_000f;
        float queueTime    = (t5 - t4) / 1_000_000f;
        float totalTime    = (t5 - tStart) / 1_000_000f;

        System.out.println(String.format("PERF REPORT (ms):"));
        System.out.println(String.format(" > Get Model : %6.3f ms", getModelTime));
        System.out.println(String.format(" > Load CSV  : %6.3f ms (CRITICAL)", loadCsvTime));
        System.out.println(String.format(" > Prep Data : %6.3f ms", prepTime));
        System.out.println(String.format(" > Queue Add : %6.3f ms", queueTime));
        System.out.println(String.format(" = TOTAL     : %6.3f ms", totalTime));

        if (totalTime > 50.0f) {
          System.out.println("!!! LAG WARNING: Operation took > 1 Tick (50ms) !!!");
        }
      }
    }
    return InteractionResultHolder.success(stack);
  }
}
