package com.github.ptran779.aegisops.brain.behavior;

import com.github.ptran779.aegisops.brain.sensor.AbsSensor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public abstract class AbsAction {
  private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
  // decision weight
  private final HashMap<String, WeightBias> sensorWeights = new HashMap<>();
  // setup
  public void setFlags(EnumSet<Flag> pFlagSet) {
    this.flags.clear();
    this.flags.addAll(pFlagSet);
  }
  public EnumSet<Flag> getFlags() {return flags;}
  public void setWeightBias(String sensorName, WeightBias wb) {this.sensorWeights.put(sensorName, wb);}
  public WeightBias getWeightBias(String sensorName) {return sensorWeights.get(sensorName);}
  // You might want a convenience method to compute score:
  public float computeScore(Map<String, AbsSensor<?>> sensorValues) {
    float score = 0f;
    for (var entry : sensorWeights.entrySet()) {
      WeightBias wb = entry.getValue();
      Float sensorVal = (Float) sensorValues.get(entry.getKey()).getValue();
      if (sensorVal != null) {score += wb.weight * sensorVal + wb.bias;}
      // clean up dead unused weight // maybe move this to unload entity phase? FIXME
    }
    return score;
  }

  public boolean canUse(){return false;};
  public void start(){};
  public void end(){}
  public boolean keepUsing(){return false;}
  public boolean isInterruptible() {return true;}
  public void tick(){};

  public static enum Flag {
    MOVE, LOOK, JUMP, TARGET, USE;
  }

  public static class WeightBias {
    public float weight;
    public float bias;

    public WeightBias(float weight, float bias) {
      this.weight = weight;
      this.bias = bias;
    }
  }
}
