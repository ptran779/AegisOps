package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.entity.util.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.util.AgentInventoryMenu;
import com.github.ptran779.aegisops.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Tooltip;
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

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.github.ptran779.aegisops.entity.util.AbstractAgentEntity.FEMALE;
import static com.github.ptran779.aegisops.entity.util.AbstractAgentEntity.SKIN;


@OnlyIn(Dist.CLIENT)
public class AgentInventoryScreen extends AbstractContainerScreen<AgentInventoryMenu>{
    private static final Font font = Minecraft.getInstance().font;
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(AegisOps.MOD_ID,"textures/inventory.png");
    private static final ResourceLocation BUTTON_STATE = new ResourceLocation(AegisOps.MOD_ID,"textures/button.png");
    private final Player player;
    private final AbstractAgentEntity agent;
    DecimalFormat df = new DecimalFormat("#.#");

    protected void init(){
        super.init();
        PlainTextButton movementBtn = new PlainTextButton(this.leftPos + 227, this.topPos + 7,
            65, 25,Component.empty(),
            btn -> {
                PacketHandler.CHANNELS.sendToServer(new AgentFollowPacket(agent.getId(),
                    agent.getMovement() == 2 ? 0 : agent.getMovement() + 1,
                    this.player.getUUID()));
            }, font
        );
        movementBtn.setTooltip(Tooltip.create(Component.literal("Toggle Movement Mode")));
        addRenderableWidget(movementBtn);

        PlainTextButton targetingBtn = new PlainTextButton(this.leftPos + 227, this.topPos + 36,
            65, 25,Component.empty(),
            btn -> {
                PacketHandler.CHANNELS.sendToServer(new AgentHostilePacket(
                    agent.getId(),
                    agent.getAutoHostile() == 3 ? 0 : agent.getAutoHostile() + 1));
            }, font
        );
        targetingBtn.setTooltip(Tooltip.create(Component.literal("Toggle Targeting Mode")));
        addRenderableWidget(targetingBtn);

        PlainTextButton dismissBtn = new PlainTextButton(this.leftPos + 227, this.topPos + 151,
            65, 15,Component.empty(),
            btn -> {
                PacketHandler.CHANNELS.sendToServer(new AgentBoolPacket(
                    agent.getId(),
                    AgentCommandType.REMOVE,
                    true));
                Minecraft.getInstance().setScreen(null);
            }, font
        );
        dismissBtn.setTooltip(Tooltip.create(Component.literal("Dismiss Agent")));
        addRenderableWidget(dismissBtn);
//        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+35,8,8,0,0,8,BUTTON_STATE, 8,16, () -> true, btn -> {
//            PacketHandler.CHANNELS.sendToServer(new AgentFollowPacket(agent.getId(), agent.getMovement() == 2 ? 0 :agent.getMovement()+1, this.player.getUUID()));
//        }));
//        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+50,8,8,0,0,8,BUTTON_STATE, 8,16, () -> true,btn -> {
//            PacketHandler.CHANNELS.sendToServer(new AgentHostilePacket(agent.getId(), agent.getAutoHostile() == 3 ? 0 :agent.getAutoHostile()+1));
//        }));
//        addRenderableWidget(new ToggleButton(this.leftPos + 227,this.topPos+65,8,8,0,0,8,BUTTON_STATE, 8,16, agent::getAttackPlayer,btn -> {
//            PacketHandler.CHANNELS.sendToServer(new AgentBoolPacket(agent.getId(), AgentCommandType.ATTACK_PLAYER, !agent.getAttackPlayer()));
//        }));
//        addRenderableWidget(new ToggleButton(this.leftPos + 240,this.topPos+150,40,12,0,0,0,BUTTON_STATE, 40,12, ()->true,btn -> {
//            PacketHandler.CHANNELS.sendToServer(new AgentBoolPacket(agent.getId(), AgentCommandType.REMOVE, true));
//            Minecraft.getInstance().setScreen(null); // Close the screen
//        }));
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
        InventoryScreen.renderEntityInInventory(pGuiGraphics, leftPos+33, topPos+72, 30, pose, null, agent);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(CONTAINER_BACKGROUND,(this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2,0,0,this.imageWidth,this.imageHeight, 300, 166);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // control flag
        pGuiGraphics.drawString(this.font, agent.getName().getString() +" the " + agent.agentType, 65, 15, 0x00CFFF, false);
        pGuiGraphics.drawString(this.font, "Commander: " + agent.getOwner(), 65, 40, 0x00CFFF, false);
        String followDisp = switch (agent.getMovement()){
            case 0 -> "Wander";
            case 1 -> "Guard";
            case 2 -> "Follow";
            default -> "not sure";
        };
        pGuiGraphics.drawString(this.font, followDisp, 245, 15, 0x00CFFF, false);
        String hostileDisp = switch (agent.getAutoHostile()){
            case 0 -> "None";
            case 1 -> "Hostile";
            case 2 -> "Humanoid";
            case 3 -> "All";
            default -> "not sure";
        };
        pGuiGraphics.drawString(this.font, hostileDisp, 245, 45, 0x00CFFF, false);

        // debug val
//        pGuiGraphics.drawString(this.font, "Food", 240, 80, 0x00CFFF, false);
        pGuiGraphics.drawString(this.font, agent.getFood() + " / " +agent.maxfood, 245, 126, 0x00CFFF, false);
//        pGuiGraphics.drawString(this.font, agent.getEntityData().get(SKIN) + " - " +agent.getEntityData().get(FEMALE), 237, 105, 4210752, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int x, int y) {
        super.renderTooltip(pGuiGraphics, x, y);
    }
}