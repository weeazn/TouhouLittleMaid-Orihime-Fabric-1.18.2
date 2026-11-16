package com.github.tartaricacid.touhoulittlemaid.ai.service;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.MutableComponent;

public final class ErrorCode {
    public static final int REQUEST_SENDING_ERROR = 0;
    public static final int REQUEST_RECEIVED_ERROR = 1;
    public static final int JSON_DECODE_ERROR = 2;
    public static final int CHAT_CHOICE_IS_EMPTY = 3;
    public static final int CHAT_TEXT_IS_EMPTY = 4;
    public static final int MICROPHONE_NOT_FOUND = 5;
    public static final int CHAT_TOKEN_LIMIT_EXCEEDED = 6;

    public static MutableComponent getErrorMessage(ServiceType serviceType, int errorCode, String message) {
        if (serviceType == ServiceType.LLM) {
            switch (errorCode) {
                case ErrorCode.REQUEST_SENDING_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.llm.request_sending_error", message);
                }
                case ErrorCode.REQUEST_RECEIVED_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.llm.request_received_error", message);
                }
                case ErrorCode.JSON_DECODE_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.llm.json_decode_error", message);
                }
                case ErrorCode.CHAT_CHOICE_IS_EMPTY -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.llm.chat_choice_is_empty", message);
                }
                case ErrorCode.CHAT_TEXT_IS_EMPTY -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.llm.chat_text_is_empty", message);
                }
                case ErrorCode.CHAT_TOKEN_LIMIT_EXCEEDED -> {
                    return new TranslatableComponent("message.touhou_little_maid.ai_chat.max_tokens_limit");
                }
            }
        }

        if (serviceType == ServiceType.TTS) {
            switch (errorCode) {
                case ErrorCode.REQUEST_SENDING_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.tts.request_sending_error", message);
                }
                case ErrorCode.REQUEST_RECEIVED_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.tts.request_received_error", message);
                }
            }
        }

        if (serviceType == ServiceType.STT) {
            switch (errorCode) {
                case ErrorCode.REQUEST_SENDING_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.stt.request_sending_error", message);
                }
                case ErrorCode.REQUEST_RECEIVED_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.stt.request_received_error", message);
                }
                case ErrorCode.JSON_DECODE_ERROR -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.stt.json_decode_error", message);
                }
                case ErrorCode.MICROPHONE_NOT_FOUND -> {
                    return new TranslatableComponent("ai.touhou_little_maid.chat.stt.no_microphone");
                }
            }
        }

        return new TextComponent("");
    }
}
