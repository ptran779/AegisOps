package com.github.ptran779.aegisops.brain.agent;

import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.brain.api.BrainInfer;
import com.github.ptran779.aegisops.brain.api.Sensor;
import com.github.ptran779.aegisops.brain.api.ThrottleSensor;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.github.ptran779.aegisops.server.ForgeServerEvent.BRAIN_INFER;

public class TestBrain extends com.github.ptran779.aegisops.brain.api.Brain {
  protected AbstractAgentEntity agent;
  protected boolean computing = false;  // is server figuring out what to do
  protected int next_behavior = -1;  // if ready, set number, stop on -1  // fixme if dead thread, we gonna stuck here

  int healthS, foodS;
  int bossS;
  int meleeIS, gunIS, clearLosS;

  int eatB, followB, saluteB, wanderB, bestFoodItemS, meleeB, gunB;


  public TestBrain(AbstractAgentEntity agent) {
    super(agent);
    this.agent = agent;

    // assign sensor
    healthS = addCoreSensor(new Sensor<Float>(agent::getHealth));
    foodS = addCoreSensor(new Sensor<Integer>(agent::getFood));
    bossS = addCoreSensor(new ThrottleSensor<Player>(()->Utils.findNearestEntity(agent, Player.class, 8, entity -> agent.getBossUUID().equals(entity.getUUID())), 40));
    bestFoodItemS = addCoreSensor(new ThrottleSensor<ItemStack>(agent.inventory::getBestFood, 40));

    // weapon sensor
    meleeIS = addCoreSensor(new Sensor<ItemStack>(()->agent.inventory.getItem(agent.meleeSlot)));
//    ammoS = addCoreSensor(new ThrottleSensor<Integer>(agent.inventory::gunExistTotalAmmoCount, 60));
    gunIS = addCoreSensor(new ThrottleSensor<Boolean>(agent.inventory::gunExistWithAmmo, 20));
    clearLosS = addCoreSensor(new ThrottleSensor<Boolean>(()->Utils.hasFriendlyInLineOfFire(agent, agent.getTarget()), 41111020));
    // should add a sensor for weapon dps?

    // assign behavior
    eatB = addBehavior(new EatBehavior(agent, (Sensor<ItemStack>) coreSensors.get(bestFoodItemS)));
    followB = addBehavior(new FollowBehavior(agent, 32, 6, 4));
    saluteB = addBehavior(new SaluteBehavior(agent, 300, 0, (Sensor<Player>) coreSensors.get(bossS)));
    wanderB = addBehavior(new WanderBehavior(agent, 200));
    meleeB = addBehavior(new MeleeBehavior(agent, 4, 64, (Sensor<ItemStack>) coreSensors.get(meleeIS)));
    gunB = addBehavior(new GunBehavior(agent, 0.5, 24, 64, (Sensor<Boolean>) coreSensors.get(gunIS), (Sensor<Boolean>) coreSensors.get(clearLosS)));
  }

  @Override
  public void onTick() {
    // just to test some dummy func
//    if (agent.tickCount % 200 == 0){
//      if (BRAIN_INFER == null){return;}
//      if(!isRunning()) {
//        if (!computing) {
//          boolean i = BRAIN_INFER.taskQueue.add(new BrainInfer.taskPayload(agent.getUUID(), new float[]{1, 2, 3}));
//          if (i) {computing = true;}
//        } else {
//          if (next_behavior!=-1) {
//            startBehavior(next_behavior);
//            next_behavior = -1;
//            computing = false;
//          }
//        }
//      }
//    }

    if (!isRunning() && agent.tickCount % 20 == 0) {  // throttle

//      if (activeBehaviours != gunB && behaviors.get(gunB).canUse()){
        startBehavior(gunB);
//      }
//      else if (activeBehaviours != eatB && behaviors.get(eatB).canUse()) {
//        startBehavior(eatB);
//      }
    }
  }
}
