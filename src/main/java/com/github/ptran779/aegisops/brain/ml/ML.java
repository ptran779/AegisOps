package com.github.ptran779.aegisops.brain.ml;

import org.apache.commons.math3.util.FastMath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ML {
  protected List<AbstractLayer> layers;
  public ML() {
    layers = new ArrayList<>();
  }

  public List<AbstractLayer> getLayers(){return layers;}

  // return false if layer violate size coupling
  public boolean addLayer(AbstractLayer layer) {
    if (layers.isEmpty()) {
      layers.add(layer);
      return true;
    } else {
      if (layers.get(layers.size() - 1).getOutputSize() != layer.getInputSize()) {
        System.out.println("Layer fail to add with output size " + layers.get(layers.size() - 1).getOutputSize() + "trying to " +
            "couple with input size " + layer.getInputSize());
        return false;
      } else {
        layers.add(layer);
        return true;
      }
    }
  }

  public float[] forward(float[] input) {
    // should clean this later
    if (layers.isEmpty()) {return null;}
    if (input.length != layers.get(0).getInputSize()) {return null;}

    float[] output = input;
    for (AbstractLayer layer : layers) {
      output = layer.forward(output);
    }
    return output;
  }

  // mostly for visual
  public static float[] normalized(float[] input) {
    if (input == null || input.length == 0) return new float[0];

    // Find max for numerical stability
    float max = input[0];
    for (float v : input) if (v > max) max = v;

    // Compute exponentials and sum
    float sum = 0f;
    float[] exp = new float[input.length];
    for (int i = 0; i < input.length; i++) {
      exp[i] = (float) FastMath.exp(input[i] - max);
      sum += exp[i];
    }

    // Normalize to probabilities
    for (int i = 0; i < input.length; i++) {
      exp[i] /= sum;
    }

    return exp;
  }

  public byte[] serialize() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);

      // 1. number of layers
      dos.writeInt(layers.size());

      // 2. for each layer
      for (AbstractLayer layer : layers) {
        // write a simple type ID
        if (layer instanceof DenseLayer) dos.writeInt(1);
        else if (layer instanceof RNNLayer) dos.writeInt(2);
        else throw new RuntimeException("Unknown layer type");

        // write layer bytes
        byte[] layerBytes = layer.serialize();
        dos.writeInt(layerBytes.length); // so we know how many bytes to read when deserializing
        dos.write(layerBytes);
      }

      dos.flush();
      return baos.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
      return new byte[0];
    }
  }

  public static ML deserialize(byte[] data) {
    try {
      ML model = new ML();
      DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

      int numLayers = dis.readInt();
      for (int i = 0; i < numLayers; i++) {
        int typeId = dis.readInt();
        int len = dis.readInt();
        byte[] layerBytes = new byte[len];
        dis.readFully(layerBytes);

        AbstractLayer layer;
        if (typeId == 1) layer = new DenseLayer(0, 0); // you'll deserialize inside
        else if (typeId == 2) layer = new RNNLayer(0, 0);
        else throw new RuntimeException("Unknown layer type in deserialization");

        layer.deserialize(ByteBuffer.wrap(layerBytes));
        model.addLayer(layer);
      }

      return model;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Number of Layer: ").append(layers.size()).append("\n");
    for (AbstractLayer layer : layers) {
      sb.append(layer.toString());
    }
    return sb.toString();
  }
}
