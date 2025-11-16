package com.github.ptran779.aegisops.entity.inventory;

import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.server.MenuInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class AgentAdvanceConfigMenu extends AbstractContainerMenu {
    private final AgentInventory agentInv;
  public final AbstractAgentEntity agent;

    //client packet
    public AgentAdvanceConfigMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (AbstractAgentEntity) playerInventory.player.level().getEntity(buf.readInt()));
    }

    //server construct
    public AgentAdvanceConfigMenu(int containerId, Inventory playerInventory, AbstractAgentEntity agent) {
        super(MenuInit.AEGISOPS_MENU2.get(), containerId);
        this.agent = agent;
        this.agentInv = agent.inventory;

      // sync armor in event of modification
        agentInv.syncArmor();
    }

//    private void loadInventoryMenu() {
//        for (int i = 7; i < this.agentInv.getContainerSize(); i++) {
//            this.addSlot(new Slot(this.agentInv, i, 61 + (i-7) * 18, 57));
//        }
//        // agent load out
//        this.addSlot(new GunSlot(this.agentInv, agent.gunSlot, 14, 120));    // gun
//        this.addSlot(new MeleeSlot(this.agentInv, agent.meleeSlot, 34, 120));    // sword
//        this.addSlot(new Slot(this.agentInv, agent.specialSlot, 24, 139));    // special
//        this.addSlot(new ArmorSlot(this.agentInv, agent.gearSlots[0], 14, 82, EquipmentSlot.HEAD));
//        this.addSlot(new ArmorSlot(this.agentInv, agent.gearSlots[1], 34, 82, EquipmentSlot.CHEST));
//        this.addSlot(new ArmorSlot(this.agentInv, agent.gearSlots[2], 14, 101, EquipmentSlot.LEGS));
//        this.addSlot(new ArmorSlot(this.agentInv, agent.gearSlots[3], 34, 101, EquipmentSlot.FEET));
//
//        //player hotbar
//        for (int c = 0; c < 9; c++) {
//            this.addSlot(new Slot(this.playerInv, c, 61 + c * 18, 141));
//        }
//
//        // player slot
//        for (int r = 0; r < 3; r++) {
//            for (int c = 0; c < 9; c++) {
//                this.addSlot(new Slot(this.playerInv, c + r * 9+9, 61 + c * 18, 85 + r * 17));
//            }
//        }
//    }
//
    public ItemStack quickMoveStack(Player player, int stackIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.agentInv.stillValid(player);
    }

    public void removed(Player player) {
        super.removed(player);
        agentInv.loadArmor();
    }
}
