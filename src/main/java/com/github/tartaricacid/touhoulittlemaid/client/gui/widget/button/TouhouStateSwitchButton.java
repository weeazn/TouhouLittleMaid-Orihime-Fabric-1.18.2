package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class TouhouStateSwitchButton extends AbstractWidget {
    protected ResourceLocation resourceLocation;
    protected boolean isStateTriggered;
    protected int xTexStart;
    protected int yTexStart;
    protected int xDiffTex;
    protected int yDiffTex;

    public TouhouStateSwitchButton(int pX, int pY, int pWidth, int pHeight, boolean pInitialState) {
        super(pX, pY, pWidth, pHeight, net.minecraft.network.chat.TextComponent.EMPTY);
        this.isStateTriggered = pInitialState;
    }

    public void initTextureValues(int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation) {
        this.xTexStart = pXTexStart;
        this.yTexStart = pYTexStart;
        this.xDiffTex = pXDiffTex;
        this.yDiffTex = pYDiffTex;
        this.resourceLocation = pResourceLocation;
    }

    public void setStateTriggered(boolean pTriggered) {
        this.isStateTriggered = pTriggered;
    }

    public boolean isStateTriggered() {
        return this.isStateTriggered;
    }

    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput);
    }

    public void renderButton(PoseStack pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.disableDepthTest();
        int i = this.xTexStart;
        int j = this.yTexStart;
        if (this.isStateTriggered) {
            i += this.xDiffTex;
        }

        if (this.isHoveredOrFocused()) {
            j += this.yDiffTex;
        }

        MaidGui.texture(pGuiGraphics, this.resourceLocation, this.x, this.y, i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
    }
}
