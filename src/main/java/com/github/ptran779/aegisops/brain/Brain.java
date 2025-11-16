package com.github.ptran779.aegisops.brain;

import com.github.ptran779.aegisops.brain.behavior.AbsAction;
import com.github.ptran779.aegisops.brain.sensor.AbsSensor;

import java.util.*;

public class Brain {
  // Map sensor name/key to Sensor instance (generic Sensor<?>)
  ///  FIXME swap string with something else for quciker compute
  protected Map<String, AbsSensor<?>> sensorMap = new HashMap<>();
  protected Map<String, AbsAction> actionMap = new HashMap<>();
  protected HashSet<String> runningActions = new HashSet<>();
  // Keep track of flags currently locked/used by running actions
  private static final EnumSet<AbsAction.Flag> ALL_FLAGS = EnumSet.allOf(AbsAction.Flag.class);
  private EnumSet<AbsAction.Flag> freeFlag = EnumSet.noneOf(AbsAction.Flag.class);

  protected int tcount;
  protected boolean triggerCompute = false;
  protected final int throttle_time;

  public Brain(int throttle_time) {
    this.throttle_time = throttle_time;
    tcount = throttle_time;
  }

  public void forceEval(){triggerCompute = true;}

  // Tick method: update all sensors (called each AI tick)
  public void tick() {
    ///  compute action update
    if(++tcount >= throttle_time || triggerCompute) {  /// FIXME test with turn of the throttle and only trigger compute by hitting agent with a stick :)
      tcount = 0;
      triggerCompute = false;
      for (AbsSensor<?> sensor : sensorMap.values()) {sensor.update();}
      /// Figure out which behavior can run
      // Reset freeFlags before deciding new actions
      freeFlag.addAll(ALL_FLAGS);
      HashSet<String> newActions = new HashSet<>();
      // Mark flags used by current running actions
      for (String oldAction : runningActions) {
        AbsAction action = actionMap.get(oldAction);
        if (action != null && !action.isInterruptible()) {freeFlag.removeAll(action.getFlags());} // only interruptible
        else {newActions.add(oldAction);}
      }
      /// compute linear score and sort of only usable action
      // Comparator for descending score (higher score first)
      Comparator<ScoreActionPair> comparator = Comparator.comparingDouble((ScoreActionPair p) -> p.score).reversed();
      List<ScoreActionPair> candidates = new ArrayList<>();
      // check if all flag that action want to use available, then compute score then push in the queue
      for (var entry : actionMap.entrySet()) {
        if (freeFlag.containsAll(entry.getValue().getFlags()) && entry.getValue().canUse()) {
          float score = entry.getValue().computeScore(sensorMap);
          candidates.add(new ScoreActionPair(score, entry.getKey()));
        }
      }
      candidates.sort(comparator);
      /// progressively try to run, until run out of flag
      for (ScoreActionPair pair : candidates) {
        if (freeFlag.isEmpty()) break;
        AbsAction action = actionMap.get(pair.action);
        if (freeFlag.containsAll(action.getFlags())) {
          freeFlag.removeAll(action.getFlags());
          newActions.add(pair.action);
        }
      }
      // Must stop old actions *before* starting new ones to avoid state interference
      for (String oldAction : runningActions) {
        if (!newActions.contains(oldAction)) {actionMap.get(oldAction).end();}
      }
      for (String newAction : newActions) {
        if (!runningActions.contains(newAction)) {actionMap.get(newAction).start();}
      }
      runningActions = newActions;  // update running list
    };
    ///  compute tick
    Iterator<String> it = runningActions.iterator();
    while (it.hasNext()) {
      String action = it.next();
      AbsAction actionObj = actionMap.get(action);
      if (!actionObj.keepUsing()) {
        actionObj.end();
        it.remove();
      } else {
        actionObj.tick();
      }
    }
  }

  // Register or replace a sensor dynamically
  public void registerSensor(String key, AbsSensor<?> sensor) {sensorMap.put(key, sensor);}
  public void registerBehavior(String key, AbsAction action) {actionMap.put(key, action);}
  public void removeSensor(String key) {sensorMap.remove(key);}
  public void removeBehavior(String key) {actionMap.remove(key);}

  // Custom pair class to hold score + action
  public record ScoreActionPair(float score, String action){}
}