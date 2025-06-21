package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.util.AgentInventoryMenu;
import com.github.ptran779.aegisops.network.AgentActionPacket;
import com.github.ptran779.aegisops.network.AgentCommandType;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.math.RoundingMode;
import java.text.DecimalFormat;


@OnlyIn(Dist.CLIENT)
public class AgentInventoryScreen extends AbstractContainerScreen<AgentInventoryMenu>{
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(AegisOps.MOD_ID,"textures/inventory.png");
    private static final ResourceLocation BUTTON_STATE = new ResourceLocation(AegisOps.MOD_ID,"textures/button.png");
    private final Player player;
    private final AbstractAgentEntity agent;
    DecimalFormat df = new DecimalFormat("#.#");

    protected void init(){
        super.init();
        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+20,8,8,0,0,8,BUTTON_STATE, 8,16, agent::getAutoArmor, btn -> {
            PacketHandler.CHANNELS.sendToServer(new AgentActionPacket(agent.getId(), AgentCommandType.AUTO_ARMOR, !agent.getAutoArmor()));
        }));
        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+35,8,8,0,0,8,BUTTON_STATE, 8,16, agent::getFollow, btn -> {
            PacketHandler.CHANNELS.sendToServer(new AgentActionPacket(agent.getId(), AgentCommandType.FOLLOW, !agent.getFollow(), this.player.getName().getString()));
        }));
        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+50,8,8,0,0,8,BUTTON_STATE, 8,16, agent::getAutoHostile,btn -> {
            PacketHandler.CHANNELS.sendToServer(new AgentActionPacket(agent.getId(), AgentCommandType.AUTO_HOSTILE, !agent.getAutoHostile()));
        }));
        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+65,8,8,0,0,8,BUTTON_STATE, 8,16, ()->true,btn -> {
            System.out.println("button clicked not sure");
            PacketHandler.CHANNELS.sendToServer(new AgentActionPacket(agent.getId(), AgentCommandType.DEBUG, !agent.getAutoHostile()));
        }));
    }

    public AgentInventoryScreen(AgentInventoryMenu container, Inventory pPlayerInventory, Component pTitle) {
        super(container, pPlayerInventory, pTitle);
        this.imageHeight = 166;
        this.imageWidth = 300;
        this.agent = container.agent;
        this.inventoryLabelY = this.imageHeight - 94;
        this.player = pPlayerInventory.player;
        df.setRoundingMode(RoundingMode.CEILING);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        this.renderCom(pGuiGraphics);
    }

    protected void renderCom(GuiGraphics pGuiGraphics) {
        //somewhat crude and not really work
        Vec3 look = agent.getLookAngle();
        double angleRad = Math.atan2(-look.z, look.x);

        float pitch = (float) Math.PI;
        float yaw = (float) Math.PI/2 + (float) angleRad;
        // Build rotation quaternion (pitch then yaw)
        Quaternionf pose = new Quaternionf().rotationYXZ(yaw, -pitch, 0); // negative pitch flips correctly
        InventoryScreen.renderEntityInInventory(pGuiGraphics, leftPos+33, topPos+90, 30, pose, null, agent);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(CONTAINER_BACKGROUND,(this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2,0,0,this.imageWidth,this.imageHeight, 300, 166);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // control flag
        pGuiGraphics.drawString(this.font, agent.getName().getString() +" the " + agent.agentType, 5, 5, 4210752, false);
        pGuiGraphics.drawString(this.font, "Auto Armor", 237, 20, 4210752, false);
        pGuiGraphics.drawString(this.font, "Follow", 237, 35, 4210752, false);
        pGuiGraphics.drawString(this.font, "Aggressive", 237, 50, 4210752, false);
        pGuiGraphics.drawString(this.font, "Debug", 237, 65, 4210752, false);

        // debug val
        pGuiGraphics.drawString(this.font, "Food", 237, 90, 4210752, false);
        pGuiGraphics.drawString(this.font, agent.getFood() + " / " +agent.maxfood, 237, 100, 4210752, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int x, int y) {
        super.renderTooltip(pGuiGraphics, x, y);
    }
}