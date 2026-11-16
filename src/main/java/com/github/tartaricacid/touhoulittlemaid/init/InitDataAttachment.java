package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.ModConstants;
import com.github.tartaricacid.touhoulittlemaid.data.ChatTokensAttachment;
import com.github.tartaricacid.touhoulittlemaid.data.MaidNumAttachment;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;

public final class InitDataAttachment implements EntityComponentInitializer {
    public static final ComponentKey<MaidNumAttachment> MAID_NUM = ComponentRegistry.getOrCreate(new ResourceLocation(ModConstants.MOD_ID, "maid_num"), MaidNumAttachment.class);
    public static final ComponentKey<PowerAttachment> POWER_NUM = ComponentRegistry.getOrCreate(new ResourceLocation(ModConstants.MOD_ID, "power"), PowerAttachment.class);
    public static final ComponentKey<ChatTokensAttachment> CHAT_TOKENS = ComponentRegistry.getOrCreate(new ResourceLocation(ModConstants.MOD_ID, "chat_tokens"), ChatTokensAttachment.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MAID_NUM, player -> new MaidNumAttachment(0), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(POWER_NUM, player -> new PowerAttachment(0), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(CHAT_TOKENS, player -> new ChatTokensAttachment(0), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
