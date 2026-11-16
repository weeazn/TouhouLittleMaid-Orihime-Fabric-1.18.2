package com.github.tartaricacid.touhoulittlemaid.client.gui.item;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.PicnicBasketContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PicnicBasketContainerScreen extends AbstractContainerScreen<PicnicBasketContainer> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");

    public PicnicBasketContainerScreen(PicnicBasketContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.imageHeight = 132;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(PoseStack guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack guiGraphics, float partialTick, int mouseX, int mouseY) {
        int middleX = (this.width - this.imageWidth) / 2;
        int middleY = (this.height - this.imageHeight) / 2;
        MaidGui.texture(guiGraphics, CONTAINER_BACKGROUND, middleX, middleY, 0, 0, this.imageWidth, 35);
        MaidGui.texture(guiGraphics, CONTAINER_BACKGROUND, middleX, middleY + 35, 0, 126, this.imageWidth, 96);
    }
}
