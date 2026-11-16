package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.gui.ITooltipButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

/**
 * 女仆界面侧边栏按钮
 */
// Accessories的loom注入导致的错误，不用管
public class MaidSideTabButton extends Button implements ITooltipButton {
    private static final ResourceLocation SIDE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_side.png");
    private static final int V_OFFSET = 107;
    private final List<Component> tooltips;
    private final int top;

    public MaidSideTabButton(int x, int y, int top, OnPress onPressIn, List<Component> tooltips) {
        //super(Button.builder(new TextComponent(""), onPressIn).pos(x, y).size(26, 24));
        super(x, y, 26, 24, new TextComponent(""), onPressIn);
        this.top = V_OFFSET + top;
        this.tooltips = tooltips;
    }

    @Override
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableDepthTest();
        if (!this.active) {
            MaidGui.texture(graphics, SIDE, this.x + 2, this.y, 209, top, this.width, this.height, 256, 256);
        }
        // 193, 111
        MaidGui.texture(graphics, SIDE, this.x + 6, this.y + 4, 193, top + 4, 16, 16, 256, 256);
    }

    @Override
    public boolean isTooltipHovered() {
        return this.isHovered;
    }

    @Override
    public void renderTooltip(PoseStack graphics, Minecraft mc, int mouseX, int mouseY) {
        MaidGui.componentTooltip(graphics, mc.font, this.tooltips, mouseX, mouseY);
    }
}