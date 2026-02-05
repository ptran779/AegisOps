package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.brain.ml.*;
import com.github.ptran779.aegisops.client.widgets.*;
import com.github.ptran779.aegisops.network.*;
import com.github.ptran779.aegisops.network.ml_packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class BrainChipScreen extends Screen {
  private static final Font font = Minecraft.getInstance().font;
  private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(AegisOps.MOD_ID, "textures/ml_bg.png");
  private static final ResourceLocation BRAIN_BUTTON = new ResourceLocation(AegisOps.MOD_ID, "textures/brain_chip_bnt.png");
  private final UUID modelUUID;
  public final int imageWidth = 480;
  public final int imageHeight = 270;

  public static final int textureWidth = 124;
  public static final int textureHeight = 137;

  // model related data
  private ArchitectureCanvasWidget archCanvas;
  private ToggleButton but1,but2,but3,but4,but5;
  private EditBox lrBox, beta1Box, beta2Box, epsBox, posRlBox, negRlBox, batchBox, maxEpocBox, patBox, minDeltaBox;
  private EditBox valFracBox, testFracBox, expDestBox;
  private ToggleButton trainModeBut, addDatBut, exportDatBut, clearDatBut, prepDatBut, trainModelBut, restoreModelBut, pushModelBut;

  private StringWidget trainDatString;
  private DropDownWidget datImportOption;
  private ConsoleWidget debugConsole;

//  private boolean changeMode = false;
  private byte[] curModel;
  private byte[] byteConfig;

  private int modelInputSize;
  private final int modelOutputSize;
//  private int trainDatS;
  private boolean trainM; // only used for initialization for now

  public BrainChipScreen(UUID modelUUID, int modelInputSize, int modelOutputSize, boolean trainM) {
    super(Component.literal("Brain Chip Config"));
    this.modelUUID = modelUUID;
    this.modelInputSize = modelInputSize;
    this.modelOutputSize = modelOutputSize;
    this.trainM = trainM;
  }

  protected void init() {
    int screenX = (this.width - imageWidth)/2;
    int screenY = (this.height - imageHeight)/2;
    initModel(screenX, screenY);
    initConfig(screenX, screenY);
    initTraining(screenX, screenY);
  }

  private void initModel(int screenX, int screenY) {
    StringWidget inLabel = new StringWidget(screenX+19, screenY+60, 20, 7,
        Component.literal(String.valueOf(modelInputSize)).withStyle(Style.EMPTY.withColor(0x00F5F7)),font
    );
    StringWidget outLabel = new StringWidget(screenX+390, screenY+60, 20, 7,
        Component.literal(String.valueOf(modelOutputSize)).withStyle(Style.EMPTY.withColor(0x00F5F7)),font
    );
    this.addRenderableWidget(inLabel);
    this.addRenderableWidget(outLabel);

    but1 = new ToggleButton(screenX+434, screenY+25,31,23,0,0,23,
        BRAIN_BUTTON,textureWidth,textureHeight,
        bnt -> {
          but1.stateOn = !but1.stateOn;
          flushChangeButton();

          // hold the serialized of old model just in case we dont really change anything
          if(but1.stateOn) {
            curModel = serializeModel();
          }
          else {
            archCanvas.clearLayers();
            deserializeModel(curModel);
          }
          archCanvas.unlock(but1.stateOn);
        });
    but2= new ToggleButton(screenX+434, screenY+51,31,23,31,0,23,
        BRAIN_BUTTON,textureWidth,textureHeight,
        bnt -> {
          byte[] newModel = serializeModel();
          if (java.util.Arrays.equals(newModel, curModel)) {
            addLog("Same brain model no push");
          } else {
            PacketHandler.CHANNELS.sendToServer(new CreateNewBrain(modelUUID, newModel));
            curModel = newModel;
          }
          but1.stateOn = !but1.stateOn;
          flushChangeButton();
          archCanvas.unlock(false);  // lock edit
        });

    but3= new ToggleButton(screenX+434, screenY+77,31,23,62,0,23,
        BRAIN_BUTTON,textureWidth,textureHeight,
        bnt -> {System.out.println(bnt.active);});

    but4= new ToggleButton(screenX+434, screenY+103,14,23,93,0,23,
        BRAIN_BUTTON,textureWidth,textureHeight,
        bnt -> {popLayer();});

    but5= new ToggleButton(screenX+451, screenY+103,14,23,111,0,23,
        BRAIN_BUTTON,textureWidth,textureHeight,
        bnt -> addLayer(1, 16));  // just default

    flushChangeButton();

    but1.setTooltip(Tooltip.create(Component.literal("Button 1")));
    but2.setTooltip(Tooltip.create(Component.literal("Button 2")));
    but3.setTooltip(Tooltip.create(Component.literal("Button 3")));
    but4.setTooltip(Tooltip.create(Component.literal("Button 4")));
    but5.setTooltip(Tooltip.create(Component.literal("Button 5")));

    this.addRenderableWidget(but1);
    this.addRenderableWidget(but2);
    this.addRenderableWidget(but3);
    this.addRenderableWidget(but4);
    this.addRenderableWidget(but5);

    this.archCanvas = new ArchitectureCanvasWidget(screenX+74,screenY+28,297,95, 65, 75, 10);
    this.addRenderableWidget(archCanvas);
  }
  private void initConfig(int screenX, int screenY) {
    // training config
    lrBox = new EditBox(font, screenX+75, screenY+141, 40, 11, Component.literal("0.001"));
    beta1Box = new EditBox(font, screenX+75, screenY+158, 40, 11, Component.literal("0.9"));
    beta2Box = new EditBox(font, screenX+75, screenY+175, 40, 11, Component.literal("0.999"));
    epsBox = new EditBox(font, screenX+75, screenY+192, 40, 11, Component.literal("1E-8"));
    posRlBox = new EditBox(font, screenX+75, screenY+209, 40, 11, Component.literal("0.1"));
    negRlBox = new EditBox(font, screenX+75, screenY+226, 40, 11, Component.literal("0.1"));
    batchBox = new EditBox(font, screenX+75, screenY+243, 40, 11, Component.literal("32"));
    maxEpocBox = new EditBox(font, screenX+180, screenY+141, 40, 11, Component.literal("100"));
    patBox = new EditBox(font, screenX+180, screenY+158, 40, 11, Component.literal("10"));
    minDeltaBox = new EditBox(font, screenX+180, screenY+175, 40, 11, Component.literal("0.0001"));
    valFracBox = new EditBox(font, screenX+180, screenY+192, 40, 11, Component.literal("0.2"));
    testFracBox = new EditBox(font, screenX+180, screenY+209, 40, 11, Component.literal("0.2"));

    lrBox.setTooltip(Tooltip.create(Component.literal("Learning Rate: Adaptation speed (Standard: 0.001)")));
    beta1Box.setTooltip(Tooltip.create(Component.literal("Beta 1: 1st Moment Decay (Standard: 0.9)")));
    beta2Box.setTooltip(Tooltip.create(Component.literal("Beta 2: 2nd Moment Decay (Standard: 0.999)")));
    epsBox.setTooltip(Tooltip.create(Component.literal("Epsilon: Stability Constant (Standard: 1E-8)")));
    posRlBox.setTooltip(Tooltip.create(Component.literal("Pos Rate: Reward Multiplier")));
    negRlBox.setTooltip(Tooltip.create(Component.literal("Neg Rate: Punishment Multiplier")));
    batchBox.setTooltip(Tooltip.create(Component.literal("Batch Size: Samples per Update (Standard: 32)")));
    maxEpocBox.setTooltip(Tooltip.create(Component.literal("Max Epochs: Training Cycles")));
    patBox.setTooltip(Tooltip.create(Component.literal("Patience: Stop if no improvement")));
    minDeltaBox.setTooltip(Tooltip.create(Component.literal("Min Delta: Threshold for improvement")));
    valFracBox.setTooltip(Tooltip.create(Component.literal("Val Frac: Fraction of data for validation")));
    testFracBox.setTooltip(Tooltip.create(Component.literal("Test Frac: Fraction of data for testing")));

    this.addRenderableWidget(lrBox);
    this.addRenderableWidget(beta1Box);
    this.addRenderableWidget(beta2Box);
    this.addRenderableWidget(epsBox);
    this.addRenderableWidget(posRlBox);
    this.addRenderableWidget(negRlBox);
    this.addRenderableWidget(batchBox);
    this.addRenderableWidget(maxEpocBox);
    this.addRenderableWidget(patBox);
    this.addRenderableWidget(minDeltaBox);
    this.addRenderableWidget(valFracBox);
    this.addRenderableWidget(testFracBox);

    ImageButton config1 = new ImageButton(screenX+125, screenY+234, 31, 21, 0,
        46, 0, BRAIN_BUTTON, textureWidth,textureHeight,bnt->{
      deserializeConfig(ML.trainConfigSerialize(null));  // pull the server one and push the default setting
    });
    ImageButton config2 = new ImageButton(screenX+202, screenY+234, 31, 21, 31,
        46, 0, BRAIN_BUTTON, textureWidth, textureHeight, bnt->{
      byteConfig = serializeConfig();
      PacketHandler.CHANNELS.sendToServer(new UpdateBrainConfig(modelUUID, byteConfig));
      deserializeConfig(byteConfig);
    });
    this.addRenderableWidget(config1);
    this.addRenderableWidget(config2);
  }
  private void initTraining(int screenX, int screenY) {
    trainModeBut = new ToggleButton(screenX+441, screenY+140, 24, 16, 96,
        67, 16, BRAIN_BUTTON, textureWidth,textureHeight,bnt->{
      // flip
      toggleTrainMode();
      PacketHandler.CHANNELS.sendToServer(new TurnOnTrainMode(modelUUID, trainM));
    });

    //data managing
    trainDatString = new StringWidget(screenX+400, screenY+143, 12, 9,
        Component.literal("0").withStyle(Style.EMPTY.withColor(0x00F5F7)),font);

    addDatBut = new ToggleButton(screenX+441, screenY+159, 24, 16, 0,
        67, 16, BRAIN_BUTTON, textureWidth,textureHeight,bnt->{
      PacketHandler.CHANNELS.sendToServer(new ImportTrainData(modelUUID, datImportOption.getSelected()));
    });
    exportDatBut = new ToggleButton(screenX+441, screenY+178, 24, 16, 24,
        67, 16, BRAIN_BUTTON, textureWidth,textureHeight,bnt->{
      PacketHandler.CHANNELS.sendToServer(new ExportTrainData(modelUUID, expDestBox.getValue()));
    });
    clearDatBut = new ToggleButton(screenX+441, screenY+197, 24, 16, 48,
        67, 16, BRAIN_BUTTON, textureWidth,textureHeight,bnt->{
      PacketHandler.CHANNELS.sendToServer(new ClearTrainData(modelUUID));
    });
    prepDatBut = new ToggleButton(screenX+441, screenY+216, 24, 16, 72,
        67, 16, BRAIN_BUTTON, textureWidth,textureHeight,bnt->{
      PacketHandler.CHANNELS.sendToServer(new PrepDatForTrain(modelUUID));
      // assume done
      trainModelBut.stateOn = true;
      trainModelBut.active = true;
    });

    trainModelBut = new ToggleButton(screenX+366, screenY+236, 27, 19, 0,
        99, 19, BRAIN_BUTTON, textureWidth,textureHeight, bnt->{
      if (!trainDatString.getMessage().getString().equals("0")) PacketHandler.CHANNELS.sendToServer(new TrainBrainChip(modelUUID));  // quick dirty check
      else addLog("Can't train while data empty");
    });
    restoreModelBut = new ToggleButton(screenX+402, screenY+236, 27, 19, 27,
        99, 19, BRAIN_BUTTON, textureWidth,textureHeight, bnt->{
      PacketHandler.CHANNELS.sendToServer(new CommitTrainModel(false, modelUUID));
      trainDone(false);
    });
    pushModelBut = new ToggleButton(screenX+438, screenY+236, 27, 19, 54,
        99, 19, BRAIN_BUTTON, textureWidth,textureHeight, bnt->{
      PacketHandler.CHANNELS.sendToServer(new CommitTrainModel(true, modelUUID));
      trainDone(false);
    });

    datImportOption = new DropDownWidget(screenX+366, screenY+159, 72, 16, 6,
        "",null, (s)->{});
    expDestBox = new EditBox(font, screenX+367, screenY+179, 70, 14, Component.empty());

    debugConsole = new ConsoleWidget(screenX+248, screenY+141, 113, 113,font, 0x00f5f7);
    debugConsole.log("hello world");

    trainModeBut.stateOn = false;
    addDatBut.active=false;
    exportDatBut.active=false;
    clearDatBut.active=false;
    prepDatBut.active=false;
    trainModelBut.active=false;
    restoreModelBut.active=false;
    pushModelBut.active=false;

    this.addRenderableWidget(trainDatString);
    this.addRenderableWidget(trainModeBut);
    this.addRenderableWidget(addDatBut);
    this.addRenderableWidget(exportDatBut);
    this.addRenderableWidget(clearDatBut);
    this.addRenderableWidget(prepDatBut);
    this.addRenderableWidget(trainModelBut);
    this.addRenderableWidget(restoreModelBut);
    this.addRenderableWidget(pushModelBut);

    this.addRenderableWidget(expDestBox);
    this.addRenderableWidget(debugConsole);
    this.addRenderableWidget(datImportOption);

    if (curModel.length != 0) deserializeModel(curModel);
    deserializeConfig(byteConfig);

    // activate other thing correctly
    if (trainM){
      trainM = false;
      toggleTrainMode();
    }
  }

  private void flushChangeButton(){
    but2.active = but1.stateOn;
    but3.active = but1.stateOn;
    but4.active = but1.stateOn;
    but5.active = but1.stateOn;

    but2.stateOn = but1.stateOn;
    but3.stateOn = but1.stateOn;
    but4.stateOn = but1.stateOn;
    but5.stateOn = but1.stateOn;
  };

  public void toggleTrainMode(){
    trainM = !trainM;
    trainModeBut.stateOn = trainM;
    // deactivate only for prep
    if (!trainM){
      trainModelBut.stateOn = false;
      restoreModelBut.stateOn = false;
      pushModelBut.stateOn = false;

      restoreModelBut.active = false;
      pushModelBut.active = false;
    }
    addDatBut.stateOn = trainM;
    exportDatBut.stateOn = trainM;
    clearDatBut.stateOn = trainM;
    prepDatBut.stateOn = trainM;

    addDatBut.active = trainM;
    exportDatBut.active = trainM;
    clearDatBut.active = trainM;
    prepDatBut.active = trainM;
  }

  public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(pGuiGraphics);

    int screenX = (this.width - imageWidth)/2;
    int screenY = (this.height - imageHeight)/2;

    pGuiGraphics.blit(CONTAINER_BACKGROUND, screenX, screenY, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    super.render(pGuiGraphics, mouseX, mouseY, partialTicks);
  }

  public void addLayer(int type, int node){
    if (archCanvas == null) {
      System.out.println("[AegisOps Critical] Screen is not initialized");
      return;
    }
    String lType = switch (type) {
      case DenseLayer.LAYER_ID -> "Dense";
      case LeakyReluLayer.LAYER_ID -> "Leaky Relu";
      case ReluLayer.LAYER_ID -> "Relu";
      case RNNLayer.LAYER_ID -> "RNN";
      default -> "Unknown";
    };

    LayerConfigWidget lWid = new LayerConfigWidget(0,0,65,75, lType, node);
    archCanvas.addLayer(lWid);
  }

  public void popLayer(){
    if (archCanvas == null) {
      System.out.println("[AegisOps Critical] Screen is not initialized");
    } else if (archCanvas.getLayers().isEmpty()) {
      System.out.println("[AegisOps info] Empty architecture");
    } else {
      archCanvas.popLayer();
    }
  }

  // for storage and transfer load
  protected byte[] serializeModel() {
    if (archCanvas == null || archCanvas.getLayers().isEmpty()) return new byte[0];
    List<LayerConfigWidget> widgets = archCanvas.getLayers();

    //preload data for injection layer
    int layerCount = widgets.size();
    int[] rawDat = new int[widgets.size()*2];

    int lastsize = modelInputSize;
    for (int i = 0; i < layerCount; i++) {
      LayerConfigWidget widget = widgets.get(i);
      int id = switch(widget.typeSelector.getSelected()) {
        case "Dense" -> DenseLayer.LAYER_ID;
        case "Leaky Relu" -> LeakyReluLayer.LAYER_ID;
        case "Relu" -> ReluLayer.LAYER_ID;
        case "RNN" -> RNNLayer.LAYER_ID;
        default -> throw new IllegalStateException("Unknown layer type");
      };

      int size = lastsize;
      // Only parse size if it's a configurable layer (Dense/RNN)
      if (id != ReluLayer.LAYER_ID && id != LeakyReluLayer.LAYER_ID) {
        try {
          size = Integer.parseInt(widget.outputSizeBox.getValue());
        } catch (NumberFormatException e) {
          return new byte[0]; // Fail immediately on bad input
        }
      }
      rawDat[i*2] = id; rawDat[i*2+1] = size;
      lastsize = size;
    }

    //backward stepping and auto correct last layer
    int lastConfigurableIndex = -1;

    for (int i = layerCount - 1; i >= 0; i--) {
      int id = rawDat[i*2];
      if (id != ReluLayer.LAYER_ID && id != LeakyReluLayer.LAYER_ID) {
        lastConfigurableIndex = i;
        break;
      }
    }

    // If no configurable layer exists (only Activations), model is invalid.
    if (lastConfigurableIndex == -1) {return new byte[0];}

    // DIRECT MODIFICATION: Force the size to match the entity requirement -- chain to end
    for (int i = lastConfigurableIndex; i < layerCount; i++) {
      rawDat[i*2+1] = modelOutputSize;
    }

    // Now we write the corrected data to the byte array
    int totalBytes = 8 + (layerCount * 8);
    ByteBuffer buffer = ByteBuffer.allocate(totalBytes);

    buffer.putInt(this.modelInputSize);
    buffer.putInt(layerCount);
    for (int i = 0; i < layerCount; i++) {
      buffer.putInt(rawDat[i*2]);
      buffer.putInt(rawDat[i*2+1]);
    }

    return buffer.array();
  }

  private void deserializeModel(byte[] data) {
    if (data.length==0) return;
    ByteBuffer buffer = ByteBuffer.wrap(data);
    modelInputSize = buffer.getInt();
    int numLayers = buffer.getInt();
    for (int i = 0; i < numLayers; i++) {
      addLayer(buffer.getInt(), buffer.getInt());
    }
    archCanvas.unlock(false);
  }

  protected byte[] serializeConfig() {
    // 1. Allocate the buffer (40 bytes fixed size)
    ByteBuffer buffer = ByteBuffer.allocate(48);

    // 2. Write Floats (Scrape from EditBoxes)
    // We use a safe parse; if they typed "crap", it defaults to 0 to prevent crashes.
    buffer.putFloat(parseSafeFloat(lrBox));
    buffer.putFloat(parseSafeFloat(beta1Box));
    buffer.putFloat(parseSafeFloat(beta2Box));
    buffer.putFloat(parseSafeFloat(epsBox));
    buffer.putFloat(parseSafeFloat(posRlBox));
    buffer.putFloat(parseSafeFloat(negRlBox));

    // 3. Write Ints
    buffer.putInt(parseSafeInt(batchBox));
    buffer.putInt(parseSafeInt(maxEpocBox));
    buffer.putInt(parseSafeInt(patBox));

    // 4. Write Final Float
    buffer.putFloat(parseSafeFloat(minDeltaBox));
    buffer.putFloat(parseSafeFloat(valFracBox));
    buffer.putFloat(parseSafeFloat(testFracBox));

    return buffer.array();
  }

// --- Helpers to handle "crap" input without crashing ---
  private float parseSafeFloat(EditBox box) {
    try {
      return Float.parseFloat(box.getValue());
    } catch (NumberFormatException e) {
      return 0.0f; // Background handling: default to 0 on garbage
    }
  }

  private int parseSafeInt(EditBox box) {
    try {
      return Integer.parseInt(box.getValue());
    } catch (NumberFormatException e) {
      return 0; // Background handling
    }
  }

  private void deserializeConfig(byte[] data) {
    if (data.length == 0) return;
    ByteBuffer buffer = ByteBuffer.wrap(data);
    lrBox.setValue(String.valueOf(buffer.getFloat()));
    beta1Box.setValue(String.valueOf(buffer.getFloat()));
    beta2Box.setValue(String.valueOf(buffer.getFloat()));
    epsBox.setValue(String.valueOf(buffer.getFloat()));
    posRlBox.setValue(String.valueOf(buffer.getFloat()));
    negRlBox.setValue(String.valueOf(buffer.getFloat()));
    batchBox.setValue(String.valueOf(buffer.getInt()));
    maxEpocBox.setValue(String.valueOf(buffer.getInt()));
    patBox.setValue(String.valueOf(buffer.getInt()));
    minDeltaBox.setValue(String.valueOf(buffer.getFloat()));
    valFracBox.setValue(String.valueOf(buffer.getFloat()));
    testFracBox.setValue(String.valueOf(buffer.getFloat()));
  }

  public void setCurModel(byte[] curModel) {this.curModel = curModel;}

  public void setTrainConfig(byte[] trainConfig) {this.byteConfig = trainConfig;}

  public boolean isPauseScreen() { return false; }

  public void updateImportList(List<String> files) {datImportOption.setOptions(files);}

  public void updateDatSize(int s){
    trainDatString.setMessage(Component.literal(String.valueOf(s)).withStyle(Style.EMPTY.withColor(0x00F5F7)));
  }

  public void addLog(String log) {
    debugConsole.log(log);
  }

  public void trainDone(boolean done){
    if (done) {
      restoreModelBut.stateOn = true;
      pushModelBut.stateOn = true;
      restoreModelBut.active = true;
      pushModelBut.active = true;
      trainModelBut.stateOn = false;
      trainModelBut.active = false;
    } else {
      restoreModelBut.stateOn = false;
      pushModelBut.stateOn = false;
      restoreModelBut.active = false;
      pushModelBut.active = false;
      trainModelBut.stateOn = true;
      trainModelBut.active = true;
    }
  }
}