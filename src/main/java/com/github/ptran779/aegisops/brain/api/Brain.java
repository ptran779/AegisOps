package com.github.ptran779.aegisops.brain.api;

import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public abstract class Brain {
  // for main sensor, put in order and hard code the order somewhere. It is way faster.
  protected LivingEntity brainEntity;
  protected final List<Sensor<?>> coreSensors;

  protected final List<Behavior> behaviors;
  protected int activeBehaviours=-1;
  protected int lastRunBehaviours=-1;

  public boolean isRunning(){
    return activeBehaviours>-1;
  }

  public void startBehavior(int i){
    activeBehaviours = i;
  }

  public Brain(LivingEntity brainEntity) {
    coreSensors = new ArrayList<>();
    behaviors = new ArrayList<>();
    this.brainEntity = brainEntity;
  }

  // Sensor stuff
  public int addCoreSensor(Sensor<?> sensor) {
    coreSensors.add(sensor);
    return coreSensors.size() - 1;
  }

  public void resetSensors() {
    for (Sensor<?> s : coreSensors) s.markDirty(brainEntity.tickCount);
  }

  public List<Sensor<?>> getAllCoreSensors(){return coreSensors;}

  // behavior stuff
  public int addBehavior(Behavior behavior) {
    behaviors.add(behavior);
    return behaviors.size() - 1;
  }

  // Ticking logic
  public void preTick(){resetSensors();}

  public abstract void onTick();  // decision overwrite here

  public void postTick(){  // only 1 behavior can be active
    if (lastRunBehaviours!= activeBehaviours) {
      if (lastRunBehaviours != -1) {behaviors.get(lastRunBehaviours).stop();}
      if (isRunning()) {behaviors.get(activeBehaviours).start();}
    } else {
      if (isRunning()) {
        boolean state = behaviors.get(activeBehaviours).run();
        if (state) {
          behaviors.get(activeBehaviours).stop();
          activeBehaviours = -1;
        }
      }
    }
    lastRunBehaviours = activeBehaviours;
  };

  public void tick(){
    // prep and reset
    preTick();
    // decision
    onTick();
    // behavior execution
    postTick();
  }
}