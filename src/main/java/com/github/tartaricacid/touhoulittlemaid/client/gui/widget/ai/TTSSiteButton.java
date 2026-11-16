package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.ai;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.ai.service.tts.TTSSite;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.ai.settings.AIChatSettingsTTSSiteScreen;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.SaveTTSSiteMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class TTSSiteButton extends Button {
    private static final ResourceLocation MISC = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/ai_chat/misc.png");

    private final TTSSite site;
    private final AIChatSettingsTTSSiteScreen parent;

    public TTSSiteButton(TTSSite site, AIChatSettingsTTSSiteScreen parent, int x, int y, int width) {
        super(x, y, width, 24, new TextComponent(""), b -> {
        });
        this.site = site;
        this.parent = parent;

        String nameKey = this.site.getNameKey();
        if (I18n.exists(nameKey)) {
            this.setMessage(new TextComponent(I18n.get(nameKey)));
        } else {
            this.setMessage(new TextComponent(this.site.id()));
        }
    }

    @Override
    public void renderButton(PoseStack graphics, int mouseX, int mouseY, float partialTick) {
        MaidGui.gradient(graphics, this.x, this.y, this.x + this.width, this.y + this.height, 0xbf_090909, 0xbf_090909);
        if (this.isHoveredOrFocused()) {
            MaidGui.gradient(graphics, this.x, this.y, this.x + this.width, this.y + this.height, 0x2f_F3EFE0, 0x2f_F3EFE0);
        }

        this.renderString(graphics, Minecraft.getInstance().font, 0xF3EFE0);

        RenderSystem.enableBlend();
        // 站点图标（左侧）
        MaidGui.texture(graphics, this.site.icon(), this.x + 6, this.y + 4, 0, 0, 16, 16, 16, 16);
        // 启用按钮（右侧）
        MaidGui.texture(graphics, MISC, this.x + this.width - 46, this.y + 4, this.site.enabled() ? 16 : 0, 0, 16, 16);
        // 编辑按钮（最右侧）
        MaidGui.texture(graphics, MISC, this.x + this.width - 24, this.y + 4, 16, 16, 16, 16);
        RenderSystem.disableBlend();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int right = this.x + this.width;

        // 启用按钮
        if (right - 50 <= mouseX && mouseX <= right - 26) {
            var msg = SaveTTSSiteMessage.toggle(this.site.id(), !this.site.enabled());
            ClientPlayNetworking.send(SaveTTSSiteMessage.ID, SaveTTSSiteMessage.encode(msg));
            return;
        }

        // 编辑按钮
        if (right - 28 <= mouseX && mouseX <= right - 4) {
            this.parent.openTTSSiteEditor(this.site.id());
        }
    }

    public void renderString(PoseStack graphics, Font font, int color) {
        MaidGui.text(graphics, font, this.getMessage(), this.x + 28, this.y + (this.height - 8) / 2,
                this.site.enabled() ? 0xFF999999 : 0xFF444444, false);
    }
}
