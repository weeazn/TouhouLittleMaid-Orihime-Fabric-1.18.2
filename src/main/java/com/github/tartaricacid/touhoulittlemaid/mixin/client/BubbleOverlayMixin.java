package com.github.tartaricacid.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble.ShaderBubbleText;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class BubbleOverlayMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V", shift = At.Shift.AFTER))
    private void renderBubbleText(float tickDelta, long startTime, boolean renderLevel, CallbackInfo ci) {
        ShaderBubbleText.draw();
    }
}
