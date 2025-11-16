package com.github.ptran779.aegisops.brain.sensor;

public abstract class AbsSensor<T> {
  protected T value;
  public AbsSensor(T value) {this.value = value;}
  public T getValue() {return value;}   /// FIXME enforce int/float type for computation
  public void setValue(T value) {this.value = value;}
  public abstract void update();
}
