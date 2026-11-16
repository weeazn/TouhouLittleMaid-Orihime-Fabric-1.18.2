package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import java.util.Collections;
import java.util.List;


public class FlatColorButton extends Button {
    private List<Component> tooltips;
    protected boolean isSelect = false;

    public FlatColorButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
    }

    public FlatColorButton setTooltips(String key) {
        tooltips = Collections.singletonList(new TranslatableComponent(key));
        return this;
    }

    public FlatColorButton setTooltips(List<Component> tooltips) {
        this.tooltips = tooltips;
        return this;
    }

    public void renderToolTip(PoseStack graphics, Screen screen, int pMouseX, int pMouseY) {
        if (this.isHovered && tooltips != null) {
            MaidGui.componentTooltip(graphics, Screens.getClient(screen).font, tooltips, pMouseX, pMouseY);
        }
    }

    @Override
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (isSelect) {
            MaidGui.gradient(graphics, this.x, this.y, this.x + this.width, this.y + this.height, 0xff_1E90FF, 0xff_1E90FF);
        } else {
            MaidGui.gradient(graphics, this.x, this.y, this.x + this.width, this.y + this.height, 0xff_434242, 0xff_434242);
        }
        if (this.isHoveredOrFocused()) {
            MaidGui.gradient(graphics, this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            MaidGui.gradient(graphics, this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            MaidGui.gradient(graphics, this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            MaidGui.gradient(graphics, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }
        //int i = getFGColor();
        int i = this.active ? 16777215 : 10526880;
        this.renderString(graphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public void renderString(PoseStack graphics, Font font, int pColor) {
        MaidGui.drawCenteredString(graphics, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xF3EFE0);
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
