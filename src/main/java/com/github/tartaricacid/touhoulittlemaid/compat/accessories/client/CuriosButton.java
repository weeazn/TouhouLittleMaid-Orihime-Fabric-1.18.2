package com.github.tartaricacid.touhoulittlemaid.compat.accessories.client;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.gui.ITooltipButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Accessories的loom注入导致的错误，不用管
public class CuriosButton extends Button implements ITooltipButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/bauble_button.png");
    private final int vStart;
    private final Component tooltip;

    public CuriosButton(int x, int y, boolean isOpen, OnPress onPress) {
        super(x + 85, y + 129, 54, 31, new TextComponent(""), onPress);
        this.vStart = isOpen ? this.getHeight() : 0;
        if (isOpen) {
            this.tooltip = new TranslatableComponent("gui.touhou_little_maid.curios_button.close.desc");
        } else {
            this.tooltip = new TranslatableComponent("gui.touhou_little_maid.curios_button.open.desc");
        }
    }

    @Override
    public void renderButton(PoseStack guiGraphics, int mouseX, int mouseY, float partialTicks) {
        MaidGui.texture(guiGraphics, TEXTURE, this.x, this.y,
                108, this.vStart, this.getWidth(), this.getHeight(),
                256, 256);
    }

    @Override
    public boolean isTooltipHovered() {
        return this.isHovered;
    }

    @Override
    public void renderTooltip(PoseStack graphics, Minecraft mc, int mouseX, int mouseY) {
        MaidGui.tooltip(graphics, mc.font, this.tooltip, mouseX, mouseY);
    }
}