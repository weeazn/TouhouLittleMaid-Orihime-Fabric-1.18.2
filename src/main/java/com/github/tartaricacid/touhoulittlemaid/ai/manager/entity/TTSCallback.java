package com.github.tartaricacid.touhoulittlemaid.ai.manager.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.ai.service.ErrorCode;
import com.github.tartaricacid.touhoulittlemaid.ai.service.ResponseCallback;
import com.github.tartaricacid.touhoulittlemaid.ai.service.ServiceType;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.TTSAudioToClientMessage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.github.tartaricacid.touhoulittlemaid.ai.service.ErrorCode;

import java.net.http.HttpRequest;

public class TTSCallback implements ResponseCallback<byte[]> {
    private final EntityMaid maid;
    // Vanilla 1.18.2 limits clientbound custom payloads to 1 MiB, including the two VarInts.
    private static final int MAX_AUDIO_BYTES = (1 << 20) - 10;

    public TTSCallback(EntityMaid maid, String chatText, long waitingChatBubbleId) {
        this.maid = maid;
        if (maid.level instanceof ServerLevel level) {
            level.getServer().execute(() -> {
                if (maid.isAlive()) maid.getChatBubbleManager().addLLMChatText(chatText, waitingChatBubbleId);
            });
        }
    }

    @Override
    public void onFailure(HttpRequest request, Throwable throwable, int errorCode) {
        if (maid.level instanceof ServerLevel serverLevel) {
            MinecraftServer server = serverLevel.getServer();
            server.submit(() -> {
                if (maid.getOwner() instanceof ServerPlayer player) {
                    String cause = throwable.getLocalizedMessage();
                    MutableComponent errorMessage = ErrorCode.getErrorMessage(ServiceType.TTS, errorCode, cause);
                    player.sendMessage(errorMessage.withStyle(ChatFormatting.RED), net.minecraft.Util.NIL_UUID);
                }
            });
        }
        TouhouLittleMaid.LOGGER.warn("TTS request failed: {}, error is {}", request, throwable.getMessage());
    }

    @Override
    public void onSuccess(byte[] data) {
        if (!(maid.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (data == null || data.length == 0 || data.length > MAX_AUDIO_BYTES) {
            onFailure(null, new IllegalArgumentException("Invalid TTS audio length"), ErrorCode.REQUEST_RECEIVED_ERROR);
            return;
        }
        MinecraftServer server = serverLevel.getServer();
        server.submit(() -> {
            if (maid.isAlive() && maid.getOwner() instanceof ServerPlayer player) {
                ServerPlayNetworking.send(player, TTSAudioToClientMessage.ID, TTSAudioToClientMessage.encode(maid.getId(), data));
            }
        });
    }

    public EntityMaid getMaid() {
        return maid;
    }
}
