package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.api.client.gui.ITooltipButton;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
// Accessories的loom注入导致的错误，不用管
public class TaskButton extends Button implements ITooltipButton {
    private final IMaidTask task;
    private final boolean enable;
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;
    private final List<Component> tooltips;

    public TaskButton(IMaidTask task, boolean enable, int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, OnPress onPressIn) {
        this(task, enable, xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, 256, 256, onPressIn);
    }

    public TaskButton(IMaidTask task, boolean enable, int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, ResourceLocation resourceLocationIn, int textureWidth, int textureHeight, OnPress onPressIn) {
        this(task, enable, xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, resourceLocationIn, textureWidth, textureHeight, onPressIn, new TextComponent(""));
    }

    public TaskButton(IMaidTask task, boolean enable, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component title) {
        this(task, enable, x, y, width, height, xTexStart, yTexStart, yDiffText, resourceLocation, textureWidth, textureHeight, onPress, Collections.emptyList(), title);
    }

    public TaskButton(IMaidTask task, boolean enable, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, List<Component> tooltips, Component title) {
        super(x, y, width, height, title, onPress);
        this.task = task;
        this.enable = enable;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffText;
        this.resourceLocation = resourceLocation;
        this.tooltips = tooltips;
    }

    public IMaidTask getTask() {
        return task;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        // 禁用声音
        if (!enable) {
            return false;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    @SuppressWarnings("all")
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        int i = this.yTexStart;
        if (this.isHoveredOrFocused()) {
            i += this.yDiffTex;
        }
        RenderSystem.enableDepthTest();
        MaidGui.texture(graphics, this.resourceLocation, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, this.textureWidth, this.textureHeight);
        if (!enable) {
            // 不知为啥设定渲染高度是19，但渲染出来的是20...
            MaidGui.fill(graphics, this.x, this.y, this.x + this.width, this.y + this.height, 0x80000000);
            MaidGui.texture(graphics, this.resourceLocation, this.x + 72, this.y, (float) 93, (float) 68, 7, 19, this.textureWidth, this.textureHeight);
        }
        MaidGui.item(graphics, task.getIcon(), this.x + 2, this.y + 2);
        MaidGui.text(graphics, minecraft.font, task.getName(), this.x + 23, this.y + 6, 0x333333, false);
    }

    @Override
    public boolean isTooltipHovered() {
        return this.isHovered;
    }

    @Override
    public void renderTooltip(PoseStack graphics, Minecraft mc, int mouseX, int mouseY) {
        if (!this.tooltips.isEmpty()) {
            MaidGui.componentTooltip(graphics, mc.font, this.tooltips, mouseX, mouseY);
        }
    }
}
