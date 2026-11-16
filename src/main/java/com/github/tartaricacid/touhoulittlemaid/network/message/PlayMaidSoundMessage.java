package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.client.sound.ClientMaidSoundHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import static cn.sh1rocu.touhoulittlemaid.TouhouLittleMaidFabric.getResourceLocation;

public class PlayMaidSoundMessage {
    public static final ResourceLocation ID = getResourceLocation("play_maid_sound");

    public static final ResourceLocation AT_POS = getResourceLocation("play_maid_sound_at_pos");

    public static FriendlyByteBuf encodeAtPos(SoundEvent event, String pack, double x, double y, double z, float volume, float pitch) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeResourceLocation(event.getLocation());
        buf.writeUtf(pack);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        return buf;
    }

    public static FriendlyByteBuf encode(ResourceLocation soundEvent, String id, int entityId) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeResourceLocation(soundEvent);
        buf.writeUtf(id);
        buf.writeVarInt(entityId);
        return buf;
    }

    @Environment(EnvType.CLIENT)
    public static void handleAtPos(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientMaidSoundHandler.handleAtPos(client, listener, buf, sender);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientMaidSoundHandler.handle(client, listener, buf, sender);
    }
}
