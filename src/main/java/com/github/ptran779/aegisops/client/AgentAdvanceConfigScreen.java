package com.github.ptran779.aegisops.client;

import com.github.ptran779.aegisops.AegisOps;
import com.github.ptran779.aegisops.config.SkinManager;
import com.github.ptran779.aegisops.entity.agent.AbstractAgentEntity;
import com.github.ptran779.aegisops.entity.inventory.AgentAdvanceConfigMenu;
import com.github.ptran779.aegisops.network.Agent.ChangeSkinPacket;
import com.github.ptran779.aegisops.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
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
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AgentAdvanceConfigScreen extends AbstractContainerScreen<AgentAdvanceConfigMenu>{
    private static final Font font = Minecraft.getInstance().font;
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(AegisOps.MOD_ID,"textures/inventory2.png");
//    private static final ResourceLocation BUTTON_STATE = new ResourceLocation(AegisOps.MOD_ID,"textures/button.png");
    private final Player player;
    private final AbstractAgentEntity agent;
    DecimalFormat df = new DecimalFormat("#.#");

    protected void init(){
        super.init();
        // SKIN LIST
        Set<String> skinNames = SkinManager.getAllSkin(agent.getFemale());

        DropDownWidget skinDropdown = new DropDownWidget(this.leftPos + 63, this.topPos + 28,75,12,
            skinNames, agent.getSkin(),
            selected -> {
                PacketHandler.CHANNELS.sendToServer(new ChangeSkinPacket(agent.getId(), selected)); // server sync
                agent.setSkin(selected);  // if server too slow, use this to flush on client quickly
                agent.flushSkinCache();  // if server slow, we might have problem :)
            }
        );
        this.addRenderableWidget(skinDropdown);
    }

    // forward mouse scrolling to child widget
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Forward scroll to all children
        for (GuiEventListener widget : this.children()) {
            if (widget.mouseScrolled(mouseX, mouseY, delta)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public AgentAdvanceConfigScreen(AgentAdvanceConfigMenu container, Inventory pPlayerInventory, Component pTitle) {
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

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // Skin Selection
        pGuiGraphics.drawString(font, "Skin Selection", 66, 11, 0x00CFFF, false);
    }

    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width &&
            mouseY >= y && mouseY <= y + height;
    }
}