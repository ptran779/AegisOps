package com.github.ptran779.aegisops.brain.api;

public abstract class Behavior {
  public boolean active=false;
  public Behavior() {}

  public boolean isInterruptible(){return true;}  // might need later
  public void start(){}
  public abstract boolean canUse();
  public abstract boolean run();  // set this to true when behavior is complete
  public void stop(){}
  public void interrupt(){
    active = false;
  }
}
