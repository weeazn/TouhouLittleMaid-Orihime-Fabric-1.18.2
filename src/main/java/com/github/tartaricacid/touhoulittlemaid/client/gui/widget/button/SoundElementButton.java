package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import com.github.tartaricacid.touhoulittlemaid.client.sound.data.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import java.util.List;

public class SoundElementButton extends FlatColorButton {
    private final ResourceLocation soundEvent;
    private final int soundCount;
    private final boolean otherColor;

    public SoundElementButton(int pX, int pY, int pWidth, int pHeight, ResourceLocation soundEvent, List<SoundData> sounds, boolean otherColor, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, new TranslatableComponent(net.minecraft.Util.makeDescriptionId("button", soundEvent)), pOnPress);
        this.soundEvent = soundEvent;
        this.soundCount = sounds.size();
        this.otherColor = otherColor;
    }

    @Override
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (otherColor) {
            MaidGui.gradient(graphics, this.x, this.y, this.x + this.width, this.y + this.height, 0x5f_9e9e9e, 0x5f_9e9e9e);
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

    @Override
    public void renderString(PoseStack graphics, Font font, int pColor) {
        MaidGui.text(graphics, font, "▷", this.x + 5, this.y + (this.height - 8) / 2, 0xe0e0e0);
        MaidGui.text(graphics, font, this.getMessage(), this.x + 15, this.y + (this.height - 8) / 2, 0xfafafa);
        String countText = soundCount + "♫";
        int countTextWidth = font.width(countText);
        MaidGui.text(graphics, font, countText, this.x + this.getWidth() - countTextWidth - 5, this.y + (this.height - 8) / 2, 0xCCCCCC);
    }

    public ResourceLocation getSoundEvent() {
        return soundEvent;
    }
}
