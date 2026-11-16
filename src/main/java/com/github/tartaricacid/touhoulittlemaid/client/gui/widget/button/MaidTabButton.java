package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.client.gui.ITooltipButton;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

// Accessories的loom注入导致的错误，不用管
public class MaidTabButton extends Button implements ITooltipButton {
    private static final ResourceLocation SIDE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_side.png");
    private final int left;
    private final List<Component> tooltips;

    public MaidTabButton(int x, int y, int left, String key, OnPress onPressIn) {
        //super(Button.builder(new TextComponent(""), onPressIn).pos(x, y).size(24, 26));
        super(x, y, 24, 26, new TextComponent(""), onPressIn);
        this.left = left;
        this.tooltips = Lists.newArrayList(
                new TranslatableComponent("gui.touhou_little_maid.button." + key),
                new TranslatableComponent("gui.touhou_little_maid.button." + key + ".desc")
        );
    }

    @Override
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableDepthTest();
        if (!this.active) {
            MaidGui.texture(graphics, SIDE, this.x, this.y, left, 21, this.width, this.height, 256, 256);
        }
        MaidGui.texture(graphics, SIDE, this.x + 4, this.y + 6, left, 47, 16, 16, 256, 256);
    }

    @Override
    public boolean isTooltipHovered() {
        return this.active && this.isHovered;
    }

    @Override
    public void renderTooltip(PoseStack graphics, Minecraft mc, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        MaidGui.componentTooltip(graphics, font, tooltips, mouseX, mouseY);
    }
}