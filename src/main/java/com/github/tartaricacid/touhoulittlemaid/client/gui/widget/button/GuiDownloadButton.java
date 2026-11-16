package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.client.download.pojo.DownloadInfo;
import com.github.tartaricacid.touhoulittlemaid.client.download.pojo.DownloadStatus;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GuiDownloadButton extends Button {
    private final DownloadInfo info;

    public GuiDownloadButton(int pX, int pY, int pWidth, int pHeight, DownloadInfo info, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, new TextComponent(""), pOnPress);
        this.info = info;
        this.active = DownloadStatus.canDownload(info.getStatus());
    }

    @Override
    public void renderButton(com.mojang.blaze3d.vertex.PoseStack graphics, int mouseX, int mouseY, float partialTick) {
        this.active = DownloadStatus.canDownload(info.getStatus());
        super.renderButton(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public Component getMessage() {
        MutableComponent text;
        if (info == null) {
            return new TranslatableComponent("selectWorld.futureworld.error.title");
        }
        switch (info.getStatus()) {
            case DOWNLOADED -> text = new TranslatableComponent("gui.touhou_little_maid.resources_download.downloaded");
            case DOWNLOADING -> text = new TranslatableComponent("gui.touhou_little_maid.resources_download.downloading");
            case NEED_UPDATE -> text = new TranslatableComponent("gui.touhou_little_maid.resources_download.need_update");
            default -> text = new TranslatableComponent("gui.touhou_little_maid.resources_download.not_download");
        }
        return text;
    }
}
