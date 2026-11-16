package cn.sh1rocu.touhoulittlemaid.mixin.client;

import cn.sh1rocu.touhoulittlemaid.api.extension.IBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
    @Shadow
    protected ClientLevel level;

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void tlm$addDestroyEffects(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (!blockState.isAir() && blockState.getBlock() instanceof IBlock block
                && block.tlm$addDestroyEffects(blockState, this.level, blockPos, (ParticleEngine) (Object) this)) {
            ci.cancel();
        }
    }
}
