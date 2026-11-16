package com.github.tartaricacid.touhoulittlemaid.compat.immersivemelodies.client;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.CtrlBinding;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import immersive_melodies.client.MelodyProgress;
import immersive_melodies.client.MelodyProgressManager;
import immersive_melodies.client.animation.BipedEntityModelAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class ImmersiveMelodiesCompatInner {
    static void addInnerBinding(CtrlBinding binding) {
        binding.livingEntityVar("im_pitch", ctx -> {
            if (ctx.geoInstance() instanceof GeckoMaidEntity<?> maid) {
                return maid.getImmersiveMelodiesData().pitch;
            }
            return 0f;
        });

        binding.livingEntityVar("im_volume", ctx -> {
            if (ctx.geoInstance() instanceof GeckoMaidEntity<?> maid) {
                return maid.getImmersiveMelodiesData().volume;
            }
            return 0f;
        });

        binding.livingEntityVar("im_current", ctx -> {
            if (ctx.geoInstance() instanceof GeckoMaidEntity<?> maid) {
                return maid.getImmersiveMelodiesData().current;
            }
            return 0f;
        });

        binding.livingEntityVar("im_delta", ctx -> {
            if (ctx.geoInstance() instanceof GeckoMaidEntity<?> maid) {
                return maid.getImmersiveMelodiesData().delta;
            }
            return 0L;
        });

        binding.livingEntityVar("im_time", ctx -> {
            if (ctx.geoInstance() instanceof GeckoMaidEntity<?> maid) {
                return maid.getImmersiveMelodiesData().time;
            }
            return 0L;
        });
    }

    static void updateMelodyProgress(LivingEntity entity, ImmersiveMelodiesCompat.ImmersiveMelodiesData imData) {
        Item item = BipedEntityModelAnimator.getInstrument(entity);
        if (item != null) {
            float time = (Minecraft.getInstance().isPaused() ? 0.0F : Minecraft.getInstance().getFrameTime()) + (float) entity.tickCount;
            MelodyProgress progress = MelodyProgressManager.INSTANCE.getProgress(entity);
            progress.visualTick(time);
            imData.pitch = progress.getCurrentPitch();
            imData.volume = progress.getCurrentVolume();
            imData.current = progress.getCurrent();
            int note = progress.getLastIndex();
            if (imData.lastNote != note || progress.getTime() < imData.time) {
                imData.lastNote = note;
                imData.noteStart = progress.getTime();
            }
            imData.delta = Math.max(0, progress.getTime() - imData.noteStart);
            imData.time = progress.getTime();
        }
    }
}