package com.github.ptran779.aegisops.brain.api;

import java.util.function.Supplier;

// sensor only worth for complex eval method. simple thing like get health value is a waste
public class Sensor<T> {
  private T cachedValue;
  private boolean dirty = true;
  private final Supplier<T> updater;

  public Sensor(Supplier<T> updater) {
    this.updater = updater;
  }

  /**
   * Get the value of the sensor.
   * Computes it only if marked dirty.
   */
  public T get() {
    if (dirty) {
      cachedValue = updater.get();
      dirty = false;
    } return cachedValue;
  }

  /** Mark sensor as dirty, so it will recompute next get() */
  public void markDirty(int tickCount) {dirty = true;}

  /** Optional: force immediate update */
  public T update() {
    cachedValue = updater.get();
    dirty = false;
    return cachedValue;
  }
}