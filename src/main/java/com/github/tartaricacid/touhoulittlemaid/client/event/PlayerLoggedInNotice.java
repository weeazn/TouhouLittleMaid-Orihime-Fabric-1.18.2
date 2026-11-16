package com.github.tartaricacid.touhoulittlemaid.client.event;

import net.minecraft.network.chat.TranslatableComponent;
import cn.sh1rocu.touhoulittlemaid.api.event.PlayerLoggedInEvent;
import com.github.tartaricacid.touhoulittlemaid.init.registry.CompatRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.*;

@Environment(EnvType.CLIENT)
public class PlayerLoggedInNotice {
    private static boolean notFirst = false;

    public static void onEnterGame(PlayerLoggedInEvent event) {
        boolean missingPatchouli = !FabricLoader.getInstance().isModLoaded(CompatRegistry.PATCHOULI);
        if (notFirst) {
            return;
        }
        if (missingPatchouli) {
            MutableComponent title = new TranslatableComponent("message.touhou_little_maid.missing_patchouli.title")
                    .withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, I18n.get("message.touhou_little_maid.missing_patchouli.url"));
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("message.touhou_little_maid.missing_patchouli.url"));
            MutableComponent base = new TranslatableComponent("message.touhou_little_maid.missing_patchouli.click_here")
                    .withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(false).withUnderlined(true).withClickEvent(clickEvent).withHoverEvent(hoverEvent));
            event.getEntity().sendMessage(title.append(new net.minecraft.network.chat.TextComponent(" ")).append(base), net.minecraft.Util.NIL_UUID);
        }
        notFirst = true;
    }
}
