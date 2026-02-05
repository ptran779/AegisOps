package com.github.ptran779.aegisops.network;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.network.ml_packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel CHANNELS = NetworkRegistry.newSimpleChannel(new ResourceLocation(AegisOps.MOD_ID, "main"), ()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public static void register() {
    int id = 0;
    CHANNELS.registerMessage(id++, AgentDismissPacket.class, AgentDismissPacket::encode, AgentDismissPacket::decode, AgentDismissPacket::handle);
    CHANNELS.registerMessage(id++, AgentSpecialPacket.class, AgentSpecialPacket::encode, AgentSpecialPacket::decode, AgentSpecialPacket::handle);
    CHANNELS.registerMessage(id++, AgentFollowPacket.class, AgentFollowPacket::encode, AgentFollowPacket::decode, AgentFollowPacket::handle);
    CHANNELS.registerMessage(id++, AgentHostilePacket.class, AgentHostilePacket::encode, AgentHostilePacket::decode, AgentHostilePacket::handle);
    CHANNELS.registerMessage(id++, CameraModePacket.class, CameraModePacket::encode, CameraModePacket::decode, CameraModePacket::handle);
    CHANNELS.registerMessage(id++, EntityRenderPacket.class, EntityRenderPacket::encode, EntityRenderPacket::decode, EntityRenderPacket::handle);
    CHANNELS.registerMessage(id++, StructureRenderPacket.class, StructureRenderPacket::encode, StructureRenderPacket::decode, StructureRenderPacket::handle);
    // for ML purpose
    CHANNELS.registerMessage(id++, BrainChipScreen.class, BrainChipScreen::encode, BrainChipScreen::decode, BrainChipScreen::handle);
    CHANNELS.registerMessage(id++, CreateNewBrain.class, CreateNewBrain::encode, CreateNewBrain::decode, CreateNewBrain::handle);
    CHANNELS.registerMessage(id++, UpdateBrainConfig.class, UpdateBrainConfig::encode, UpdateBrainConfig::decode, UpdateBrainConfig::handle);
    CHANNELS.registerMessage(id++, GetTrainDataList.class, GetTrainDataList::encode, GetTrainDataList::decode, GetTrainDataList::handle);
    CHANNELS.registerMessage(id++, TurnOnTrainMode.class, TurnOnTrainMode::encode, TurnOnTrainMode::decode, TurnOnTrainMode::handle);
    CHANNELS.registerMessage(id++, ImportTrainData.class, ImportTrainData::encode, ImportTrainData::decode, ImportTrainData::handle);
    CHANNELS.registerMessage(id++, UpdateTrainDataSize.class, UpdateTrainDataSize::encode, UpdateTrainDataSize::decode, UpdateTrainDataSize::handle);
    CHANNELS.registerMessage(id++, ClearTrainData.class, ClearTrainData::encode, ClearTrainData::decode, ClearTrainData::handle);
    CHANNELS.registerMessage(id++, ExportTrainData.class, ExportTrainData::encode, ExportTrainData::decode, ExportTrainData::handle);
    CHANNELS.registerMessage(id++, PushDatLog.class, PushDatLog::encode, PushDatLog::decode, PushDatLog::handle);
    CHANNELS.registerMessage(id++, PrepDatForTrain.class, PrepDatForTrain::encode, PrepDatForTrain::decode, PrepDatForTrain::handle);
    CHANNELS.registerMessage(id++, TrainBrainChip.class, TrainBrainChip::encode, TrainBrainChip::decode, TrainBrainChip::handle);
    CHANNELS.registerMessage(id++, TrainDone.class, TrainDone::encode, TrainDone::decode, TrainDone::handle);
    CHANNELS.registerMessage(id++, CommitTrainModel.class, CommitTrainModel::encode, CommitTrainModel::decode, CommitTrainModel::handle);
  }
}
