package com.github.tartaricacid.touhoulittlemaid.client.sound;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorWindows;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.TranslatableComponent;
import java.io.File;

@Environment(EnvType.CLIENT)
public final class MaidSystemVoice {
    private static Narrator maidVoice;
    private static boolean reportedUnavailable;

    private MaidSystemVoice() { }

    public static void speak(String text) {
        Narrator voice = NarratorChatListener.INSTANCE.narrator;
        // Narrus Yeetus replaces the game's narrator with a silent instance. Use the bundled
        // Windows voice for maid replies only, leaving the game's accessibility settings alone.
        if (!voice.active() && Util.getPlatform() == Util.OS.WINDOWS
                && FabricLoader.getInstance().isModLoaded("narrus_yeetus")) {
            if (maidVoice == null && !reportedUnavailable) {
                try {
                    Narrator.setJNAPath(File.pathSeparator);
                    maidVoice = new NarratorWindows();
                } catch (LinkageError | RuntimeException unavailable) {
                    TouhouLittleMaid.LOGGER.warn("Could not start the Windows maid voice", unavailable);
                }
            }
            voice = maidVoice;
        }
        if (voice != null && voice.active()) {
            voice.say(text, true);
        } else if (!reportedUnavailable && Minecraft.getInstance().player != null) {
            reportedUnavailable = true;
            Minecraft.getInstance().player.displayClientMessage(
                    new TranslatableComponent("ai.touhou_little_maid.chat.system_voice_unavailable"), false);
        }
    }

    public static void close() {
        if (maidVoice != null && maidVoice.active()) maidVoice.destroy();
        maidVoice = null;
    }
}
