package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.Utils;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.inventory.AgentInventoryMenu;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;



@OnlyIn(Dist.CLIENT)
public class AgentInventoryScreen extends AbstractContainerScreen<AgentInventoryMenu>{
    private static final Font font = Minecraft.getInstance().font;
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(AegisOps.MOD_ID,"textures/inventory.png");
//    private static final ResourceLocation BUTTON_STATE = new ResourceLocation(AegisOps.MOD_ID,"textures/button.png");
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
                int next = (agent.getTargetMode().ordinal() + 1) % Utils.TargetMode.values().length;
                PacketHandler.CHANNELS.sendToServer(new AgentHostilePacket(agent.getId(),next));
            }, font
        );
        targetingBtn.setTooltip(Tooltip.create(Component.literal("Toggle Targeting Mode")));
        addRenderableWidget(targetingBtn);

        PlainTextButton specialBtn = new PlainTextButton(this.leftPos + 227, this.topPos + 65,
            65, 25,Component.empty(),
            btn -> PacketHandler.CHANNELS.sendToServer(new AgentSpecialPacket(agent.getId(), !agent.getAllowSpecial()))
            , font
        );
        specialBtn.setTooltip(Tooltip.create(Component.literal("Toggle Special Move")));
        addRenderableWidget(specialBtn);

        PlainTextButton dismissBtn = new PlainTextButton(this.leftPos + 227, this.topPos + 151,
            65, 15,Component.empty(),
            btn -> {
                PacketHandler.CHANNELS.sendToServer(new AgentDismissPacket(agent.getId(),true));
                Minecraft.getInstance().setScreen(null);
            }, font
        );
        dismissBtn.setTooltip(Tooltip.create(Component.literal("Dismiss Agent")));
        addRenderableWidget(dismissBtn);
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
        pGuiGraphics.drawString(font, agent.getName().getString() +" the " + agent.agentType, 65, 15, 0x00CFFF, false);
        pGuiGraphics.drawString(font, "Commander: " + agent.getOwner(), 65, 40, 0x00CFFF, false);
        String followDisp = switch (agent.getMovement()){
            case 0 -> "Wander";
            case 1 -> "Guard";
            case 2 -> "Follow";
            default -> "not sure";
        };
        pGuiGraphics.drawString(font, followDisp, 242, 16, 0x00CFFF, false);
        String hostileDisp = switch (agent.getTargetMode()){
            case OFF -> "None";
            case HOSTILE_ONLY -> "Hostile";
            case ENEMY_AGENTS -> "Humanoid";
            case ALL -> "All";
        };
        pGuiGraphics.drawString(font, hostileDisp, 242, 44, 0x00CFFF, false);

        pGuiGraphics.drawString(font, agent.getAllowSpecial() ? "Spec On":"Spec Off", 242, 72, 0x00CFFF, false);

        pGuiGraphics.drawString(font, agent.getVirtualAmmo() + "/" +agent.getMaxVirtualAmmo(), 242, 100, 0x00CFFF, false);
        pGuiGraphics.drawString(font, agent.getFood() + "/" +agent.maxfood, 242, 128, 0x00CFFF, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int x, int y) {
        super.renderTooltip(pGuiGraphics, x, y);
        // Example condition — replace with hover bounds check
        if (isHovering(x, y, this.leftPos + 227, this.topPos + 94, 65,25)) {
            pGuiGraphics.renderTooltip(font, Component.literal("Virtual Ammo"), x, y);
        } else if (isHovering(x, y, this.leftPos + 227, this.topPos + 123, 65,25)) {
            pGuiGraphics.renderTooltip(font, Component.literal("Food"), x, y);
        }
    }

    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width &&
            mouseY >= y && mouseY <= y + height;
    }
}