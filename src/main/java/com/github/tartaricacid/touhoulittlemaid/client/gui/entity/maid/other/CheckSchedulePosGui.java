package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.other;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import java.util.List;

public class CheckSchedulePosGui extends Screen {
    private final AbstractMaidContainerGui<?> parent;
    private final Component tips;
    private int middleX;
    private int middleY;

    public CheckSchedulePosGui(AbstractMaidContainerGui<?> parent, Component tips) {
        super(new TextComponent("Check Schedule Pos Screen"));
        this.parent = parent;
        this.tips = tips;
    }

    @Override
    public void init() {
        this.middleX = this.width / 2;
        this.middleY = this.height / 2;
        Button returnButton = new Button(middleX - 100, middleY + 10, 200, 20, new TranslatableComponent("button.touhou_little_maid.maid.return"), (b) -> Screens.getClient(this).setScreen(this.parent));
        this.addRenderableWidget(returnButton);
    }

    @Override
    public void render(PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        List<FormattedCharSequence> split = font.split(tips, 300);
        int startY = middleY - 10 - split.size() * (font.lineHeight + 3);
        for (FormattedCharSequence text : split) {
            MaidGui.drawCenteredString(graphics, font, text, middleX, startY, 0xFFFFFF);
            startY += font.lineHeight + 3;
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
