package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.ai;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsHubScreen.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class SideButton extends Button {
    protected final Type type;

    protected boolean isSelect = false;

    public SideButton(Type type, int pX, int pY, Component pMessage, OnPress pOnPress) {
        super(pX, pY, 100, 20, pMessage, pOnPress);
        this.type = type;
    }

    @Override
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float pPartialTick) {
        MaidGui.gradient(graphics, this.x, this.y, this.x + this.width,
                this.y + this.height, 0xbf_090909, 0xbf_090909);
        if (isSelect) {
            MaidGui.gradient(graphics, this.x, this.y, this.x + 2,
                    this.y + this.height, 0xff_55ff55, 0xff_55ff55);
        }
        if (this.isHoveredOrFocused() || isSelect) {
            MaidGui.gradient(graphics, this.x, this.y, this.x + this.width,
                    this.y + this.height, 0x2f_F3EFE0, 0x2f_F3EFE0);
        }
        this.renderString(graphics, Minecraft.getInstance().font, 0xF3EFE0);
    }

    public void renderString(PoseStack graphics, Font font, int pColor) {
        MaidGui.drawCenteredString(graphics, font, this.getMessage(), this.x + this.width / 2,
                this.y + (this.height - 8) / 2, 0xF3EFE0);
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void updateSelect(Type currentType) {
        this.setSelect(this.type == currentType);
        this.active = (this.type != currentType);
    }

    public Type getType() {
        return type;
    }
}
