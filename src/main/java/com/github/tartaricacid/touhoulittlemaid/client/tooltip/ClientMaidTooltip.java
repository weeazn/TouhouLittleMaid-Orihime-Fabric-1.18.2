package com.github.tartaricacid.touhoulittlemaid.client.tooltip;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.ItemMaidTooltip;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.YsmMaidInfo;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import com.github.tartaricacid.touhoulittlemaid.item.AbstractStoreMaidItem;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;
import org.joml.Quaternionf;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import static com.github.tartaricacid.touhoulittlemaid.client.event.SpecialMaidRenderEvent.EASTER_EGG_MODEL;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class ClientMaidTooltip implements ClientTooltipComponent {
    private final @Nullable MaidModelInfo info;
    private final YsmMaidInfo ysmMaidInfo;
    private final MutableComponent name;
    private final String customName;

    public ClientMaidTooltip(ItemMaidTooltip tooltip) {
        var stored = CustomPackLoader.MAID_MODELS.getInfo(tooltip.modelId());
        this.info = stored.or(() -> CustomPackLoader.MAID_MODELS.getInfo(EntityMaid.DEFAULT_MODEL_ID)).orElse(null);
        this.ysmMaidInfo = tooltip.ysmMaidInfo();
        this.name = stored.isEmpty() && !ysmMaidInfo.isYsmModel() ? new TextComponent(tooltip.modelId()) : getName(this.info, this.ysmMaidInfo);
        this.customName = tooltip.customName();
    }

    public MutableComponent getName(MaidModelInfo info, YsmMaidInfo ysmMaidInfo) {
        // 优先使用 YSM 模型名称
        if (YsmCompat.isInstalled() && ysmMaidInfo.isYsmModel()) {
            MutableComponent name = ysmMaidInfo.name();
            if (name.equals(new TextComponent(""))) {
                return new TextComponent(ysmMaidInfo.modelId());
            }
            return name;
        }

        // 然后才是默认模型名
        if (info == null) {
            return new TextComponent("");
        }
        return new TranslatableComponent(ParseI18n.getI18nKey(info.getName()));
    }

    @Override
    public int getHeight() {
        return info == null ? 0 : 70;
    }

    @Override
    public int getWidth(Font font) {
        return info == null ? 0 : Math.max(font.width(this.name), 50);
    }

    @Override
    public void renderImage(Font font, int pX, int pY, PoseStack guiGraphics, net.minecraft.client.renderer.entity.ItemRenderer itemRenderer, int blitOffset) {
        if (info == null) {
            return;
        }
        guiGraphics.pushPose();
        try {
            guiGraphics.translate(0, 0, blitOffset + 1);
            drawPreview(font, pX, pY, guiGraphics);
        } finally {
            guiGraphics.popPose();
        }
    }

    private void drawPreview(Font font, int pX, int pY, PoseStack guiGraphics) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        MutableComponent customNameComponent = null;
        if (StringUtils.isNotBlank(customName)) {
            customNameComponent = AbstractStoreMaidItem.readStoredName(customName);
            if (customNameComponent != null) {
                MaidGui.text(guiGraphics, font, customNameComponent.withStyle(ChatFormatting.GRAY), pX, pY + 2, 0xFFFFFF);
            }
        } else {
            MaidGui.text(guiGraphics, font, name.withStyle(ChatFormatting.GRAY), pX, pY + 2, 0xFFFFFF);
        }

        int width = this.getWidth(font);
        int posX = pX + width / 2;
        int posY = pY + 64;
        double rot = ((System.currentTimeMillis() / 25.0) % 360);
        Quaternionf pose = (new Quaternionf()).rotateZ((float) Math.PI);
        Quaternionf rotation = (new Quaternionf()).rotateY((float) Math.toRadians(rot));
        pose.mul(rotation);
        EntityMaid maid;
        try {
            maid = (EntityMaid) EntityCacheUtil.ENTITY_CACHE.get(EntityMaid.TYPE, () -> {
                Entity e = EntityMaid.TYPE.create(world);
                return Objects.requireNonNullElseGet(e, () -> new EntityMaid(world));
            });
        } catch (ExecutionException | ClassCastException e) {
            TouhouLittleMaid.LOGGER.error("Failed to render maid tooltip preview", e);
            return;
        }
        clearMaidDataResidue(maid, false);
        if (StringUtils.isNotBlank(customName)) {
            maid.setCustomName(customNameComponent);
        }
        if (info.getEasterEgg() != null) {
            maid.setModelId(EASTER_EGG_MODEL);
        } else {
            maid.setModelId(info.getModelId().toString());
        }

        // YSM 渲染运用
        if (YsmCompat.isInstalled() && ysmMaidInfo.isYsmModel()) {
            maid.setIsYsmModel(true);
            maid.setYsmModel(ysmMaidInfo.modelId(), ysmMaidInfo.textureId(), ysmMaidInfo.name());
        } else {
            maid.setIsYsmModel(false);
        }

        MaidGui.enableScissor(pX, posY - 50, pX + width, posY);
        guiGraphics.pushPose();
        try {
            guiGraphics.translate(posX, posY, 50);
            float scale = 25 * info.getRenderItemScale();
            guiGraphics.scale(scale, scale, -scale);
            guiGraphics.mulPose(cn.sh1rocu.touhoulittlemaid.client.ModelMatrices.quaternion(pose));
            var client = Minecraft.getInstance();
            var dispatcher = client.getEntityRenderDispatcher();
            var buffers = client.renderBuffers().bufferSource();
            dispatcher.setRenderShadow(false);
            try {
                dispatcher.render(maid, 0, 0, 0, 0, 1, guiGraphics, buffers, 15728880);
                buffers.endBatch();
            } finally {
                dispatcher.setRenderShadow(true);
            }
        } finally {
            guiGraphics.popPose();
            MaidGui.disableScissor();
        }
    }
}
