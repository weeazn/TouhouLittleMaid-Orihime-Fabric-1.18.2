package com.github.tartaricacid.touhoulittlemaid.event.maid;

import net.minecraft.network.chat.TranslatableComponent;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SaddleMaidEvent {
    public static void onInteract(InteractMaidEvent event) {
        Player player = event.getPlayer();
        EntityMaid maid = event.getMaid();
        ItemStack stack = event.getStack();
        if (!stack.is(Items.SADDLE)) return;
        if (player.getFirstPassenger() instanceof EntityMaid held) {
            if (!player.level.isClientSide) held.stopRiding();
            event.setCanceled(true);
        } else if (player.getPassengers().isEmpty() && maid.getPassengers().isEmpty()) {
            if (player.level.isClientSide) {
                showTips();
            } else if (maid.startRiding(player)) {
                if (maid.isHomeModeEnable()) maid.setHomeModeEnable(false);
                if (player instanceof ServerPlayer serverPlayer) {
                    InitTrigger.MAID_EVENT.trigger(serverPlayer, TriggerType.PICKUP_MAID);
                }
            }
            event.setCanceled(true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void showTips() {
        Minecraft minecraft = Minecraft.getInstance();
        Component component = new TranslatableComponent("message.touhou_little_maid.saddle.how_to_eject");
        minecraft.gui.setOverlayMessage(component, false);
        net.minecraft.client.gui.chat.NarratorChatListener.INSTANCE.sayNow(component);
    }
}
