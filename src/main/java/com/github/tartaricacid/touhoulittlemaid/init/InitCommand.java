package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.command.arguments.HandleTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.resources.ResourceLocation;

public class InitCommand {
    public static void init() {
        ArgumentTypes.register(TouhouLittleMaid.MOD_ID + ":handle_types", HandleTypeArgument.class, new EmptyArgumentSerializer<>(HandleTypeArgument::type));
    }
}
