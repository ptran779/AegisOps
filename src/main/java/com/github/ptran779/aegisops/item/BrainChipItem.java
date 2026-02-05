package com.github.ptran779.aegisops.item;

import com.github.ptran779.aegisops.brain.ml.ML;
import com.github.ptran779.aegisops.config.MlModelManager;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.network.ml_packet.BrainChipScreen;
import com.github.ptran779.aegisops.network.PacketHandler;
import com.github.ptran779.aegisops.network.ml_packet.GetTrainDataList;
import com.github.ptran779.aegisops.network.ml_packet.UpdateTrainDataSize;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

import static com.github.ptran779.aegisops.config.MlModelManager.getAvailableCsvs;

public class BrainChipItem extends Item {
  public BrainChipItem(Properties pProperties) {
    super(pProperties);
  }

  // generate chipUUID tag if no tag exist -- this is used to identify the model
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

  public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
    if (player.isShiftKeyDown()) {
      if (!player.level().isClientSide) {
        if (livingEntity instanceof AbstractAgentEntity agent) {
          UUID modelUUID = getOrCreateUUID(stack);  //get UUID item tag
          MlModelManager.MLUnit mUnit = MlModelManager.getMUnit(modelUUID, player.level().getGameTime());

          // if mUnit already has IOsize, ignore. also, IO size of either 0 sound wrong bth
          if (mUnit.inSize == 0 || mUnit.outSize == 0) {
            mUnit.inSize = agent.getSensorSize();
            mUnit.outSize = agent.getBehaviorSize();
            player.displayClientMessage(Component.literal("chip: " + modelUUID + " bound IO to " + agent.agentType
                + "class"), false);
          } else {
            player.displayClientMessage(Component.literal("chip: " + modelUUID + " already has IO bound at In:"
                    + mUnit.inSize + " Out:" + mUnit.outSize),false);
          }
        }
      }
    }
    return InteractionResult.PASS;
  }

  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!level.isClientSide) {
      UUID modelUUID = getOrCreateUUID(stack);  //get UUID item tag
      MlModelManager.MLUnit mUnit = MlModelManager.getMUnit(modelUUID, level.getGameTime());
      //request to send data and byte chain of the rest of the model
      byte[] rawModel = mUnit.model==null ? new byte[0]: mUnit.model.modelSimpleSerialize();
      byte[] rawConfig = ML.trainConfigSerialize(mUnit.model);
      boolean trainMode = false;
      int trainDatLen = 0;
      if(mUnit.dataManager != null) {
        trainMode = true;
        trainDatLen = mUnit.dataManager.rawDat.size();
      }
      // fixme might crash due to network racing
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
          new BrainChipScreen(modelUUID, mUnit.inSize, mUnit.outSize, trainMode, rawConfig, rawModel));
      List<String> messy = getAvailableCsvs();
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
          new GetTrainDataList(messy));
      PacketHandler.CHANNELS.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
          new UpdateTrainDataSize(trainDatLen));
    }
    return InteractionResultHolder.success(stack);
  }
}
