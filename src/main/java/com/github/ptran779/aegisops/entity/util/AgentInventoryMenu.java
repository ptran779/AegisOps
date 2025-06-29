package com.github.ptran779.aegisops.entity.util;

import com.github.ptran779.aegisops.server.MenuInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AgentInventoryMenu extends AbstractContainerMenu {
    private final AgentInventory agentInv;
    private final Container playerInv;
    public final AbstractAgentEntity agent;

    //client packet
    public AgentInventoryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (AbstractAgentEntity) playerInventory.player.level().getEntity(buf.readInt()));
    }

    //server construct
    public AgentInventoryMenu(int containerId, Inventory playerInventory, AbstractAgentEntity agent) {
        super(MenuInit.AEGISOPS_MENU.get(), containerId);
        this.agent = agent;
        this.agentInv = agent.inventory;
        this.playerInv = playerInventory;

        // sync armor in event of modification
        agentInv.syncArmor();

        //laod main
        loadInventoryMenu();
    }

    private void loadInventoryMenu() {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(this.agentInv, i, 61 + i * 18, 57));
        }
        // agent load out
        int agentInvSize = agentInv.getContainerSize();
        this.addSlot(new GunSlot(this.agentInv, agent.gunSlot, 14, 120));    // gun
        this.addSlot(new MeleeSlot(this.agentInv, agent.meleeSlot, 34, 120));    // sword
        this.addSlot(new Slot(this.agentInv, agent.specialSlot, 24, 139));    // special
        this.addSlot(new ArmorSlot(this.agentInv, agentInvSize-4, 14, 82, EquipmentSlot.HEAD));
        this.addSlot(new ArmorSlot(this.agentInv, agentInvSize-3, 34, 82, EquipmentSlot.CHEST));
        this.addSlot(new ArmorSlot(this.agentInv, agentInvSize-2, 14, 101, EquipmentSlot.LEGS));
        this.addSlot(new ArmorSlot(this.agentInv, agentInvSize-1, 34, 101, EquipmentSlot.FEET));

        //player hotbar
        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(this.playerInv, c, 61 + c * 18, 141));
        }

        // player slot
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(this.playerInv, c + r * 9+9, 61 + c * 18, 85 + r * 17));
            }
        }
    }

    public ItemStack quickMoveStack(Player player, int stackIndex) {
        Slot slot = this.slots.get(stackIndex);
        ItemStack stack = slot.getItem();
        // empty slot
        if (!slot.hasItem() || stack.getCount() <=0) {return ItemStack.EMPTY;}
        ItemStack copyStack = stack.copy();

        ItemStack itemStack = slot.getItem();
        if (stackIndex < this.agentInv.getContainerSize() && !this.moveItemStackTo(itemStack, this.agentInv.getContainerSize(), this.slots.size(), false)) {
            return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(itemStack, 0, this.agentInv.getContainerSize(), true)) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, itemStack);
        return copyStack;
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
