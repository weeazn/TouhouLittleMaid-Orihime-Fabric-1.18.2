package com.github.tartaricacid.touhoulittlemaid.client.event;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class AddInformationEvent {
    public static void onRenderTooltips(ItemStack stack, TooltipFlag type, List<Component> lines) {
        if (stack.isEmpty()) {
            return;
        }
        if (BaubleManager.getBauble(stack) != null) {
            lines.add(new TextComponent(" "));
            lines.add(new TranslatableComponent("tooltips.touhou_little_maid.bauble.desc"));
            lines.add(new TranslatableComponent("tooltips.touhou_little_maid.bauble.usage").withStyle(ChatFormatting.GRAY));
        }
        if (BackpackManager.findBackpack(stack).isPresent()) {
            lines.add(new TextComponent(" "));
            lines.add(new TranslatableComponent("tooltips.touhou_little_maid.backpack.desc"));
            lines.add(new TranslatableComponent("tooltips.touhou_little_maid.backpack.usage").withStyle(ChatFormatting.GRAY));
        }
    }
}
