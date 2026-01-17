package com.github.ptran779.aegisops.brain.api;

import java.util.function.Supplier;

// throttle sensor. Use this class for heavy computation // also fixme maybe use a random update time for varibility?
public class ThrottleSensor<T> extends Sensor<T> {
  private final int updatePeriod;
  private int lastUpdate = 0;
  public ThrottleSensor(Supplier<T> updater, int updatePeriod) {
    super(updater);
    this.updatePeriod = updatePeriod;
  }
  public void markDirty(int tickCount) {
    if (tickCount - lastUpdate >= updatePeriod) {
      lastUpdate = tickCount;
      super.markDirty(tickCount);
    }
  }
}
