package com.github.tartaricacid.touhoulittlemaid.client.overlay;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.item.ItemHakureiGohei;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShowPowerOverlay {
    private static ItemStack POWER_POINT;

    public static final ShowPowerOverlay INSTANCE = new ShowPowerOverlay();

    public void render(@NotNull PoseStack guiGraphics, float tickDelta) {
        Minecraft minecraft = Minecraft.getInstance();
        Options options = minecraft.options;
        Player player = minecraft.player;
        if (player == null || options.hideGui) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!MiscConfig.ALWAYS_SHOW_POWER.get() && !ItemHakureiGohei.isGohei(stack)) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        if (POWER_POINT == null) {
            POWER_POINT = InitItems.POWER_POINT.getDefaultInstance();
        }
        MaidGui.item(guiGraphics, POWER_POINT, 5, 5);
        PowerAttachment cap = InitDataAttachment.POWER_NUM.get(player);
        MaidGui.text(guiGraphics, font, String.format("%s×%.2f", ChatFormatting.BOLD, cap.get()), 20, 10, 0xffffff);
    }
}
