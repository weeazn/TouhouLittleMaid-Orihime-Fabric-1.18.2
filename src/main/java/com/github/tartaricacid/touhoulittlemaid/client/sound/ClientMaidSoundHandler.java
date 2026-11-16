package com.github.tartaricacid.touhoulittlemaid.client.sound;

import net.minecraft.core.Registry;
import com.github.tartaricacid.touhoulittlemaid.client.sound.data.MaidSoundInstance;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public final class ClientMaidSoundHandler {
    public static void handleAtPos(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ResourceLocation sound = buf.readResourceLocation();
        String pack = buf.readUtf();
        double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
        float volume = buf.readFloat(), pitch = buf.readFloat();
        client.execute(() -> {
            if (client.level != null) Registry.SOUND_EVENT.getOptional(sound).ifPresent(event ->
                    client.getSoundManager().play(new MaidSoundInstance(event, pack, x, y, z, volume, pitch)));
        });
    }

    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ResourceLocation soundEvent = buf.readResourceLocation();
        String id = buf.readUtf();
        int entityId = buf.readVarInt();
        client.execute(() -> playSound(soundEvent, id, entityId));
    }

    private static void playSound(ResourceLocation soundEvent, String id, int entityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(entityId);
            if (entity instanceof EntityMaid maid) {
                SoundEvent event = Registry.SOUND_EVENT.get(soundEvent);
                if (event != null) {
                    mc.getSoundManager().play(new MaidSoundInstance(event, id, maid));
                }
            }
        }
    }
}
