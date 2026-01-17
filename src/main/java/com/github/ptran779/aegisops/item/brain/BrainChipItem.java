package com.github.ptran779.aegisops.item.brain;

import com.github.ptran779.aegisops.Config.MlModelManager;
import com.github.ptran779.aegisops.brain.api.BrainInfer;
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
      if (player.isShiftKeyDown()) {
        UUID chipId = getOrCreateUUID(stack);
        ML model = MlModelManager.getModel(chipId, (int) level.getGameTime()).model;
        System.out.println(model.toString());
      } else {
        UUID chipId = getOrCreateUUID(stack);
        ML model = MlModelManager.getModel(chipId, (int) level.getGameTime()).model;
        // let just print for now

        //generate some random vector
        float[] payload = new float[model.getLayers().get(0).getInputSize()];
        for (int i = 0; i < payload.length; i++) {
          payload[i] = (float) Math.random() * 2 - 1; // random between -1 and 1
        }
        // send to offthread
        System.out.println("TASK AWAY");
        ForgeServerEvent.BRAIN_INFER.taskQueue.add(new BrainInfer.taskPayload(UUID.randomUUID(), payload, model));
      }
    }
    return InteractionResultHolder.success(stack);
  }
}
