package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(Brain.class)
abstract class BrainTargetMixin {
    @Shadow public abstract <U> void eraseMemory(MemoryModuleType<U> key);
    @Shadow public abstract boolean hasMemoryValue(MemoryModuleType<?> key);
    @Shadow public abstract <U> Optional<U> getMemory(MemoryModuleType<U> key);

    @Inject(method = "tick", at = @At("HEAD"))
    private void tlm$forgetSleepingMaid(ServerLevel level, LivingEntity mob, CallbackInfo ci) {
        if (hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) instanceof EntityMaid maid && maid.isSleeping()) {
            eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    @Inject(method = "setMemoryInternal", at = @At("HEAD"), cancellable = true)
    private void tlm$skipSleepingMaid(MemoryModuleType<?> key, Optional<? extends ExpirableValue<?>> value, CallbackInfo ci) {
        if (key == MemoryModuleType.ATTACK_TARGET && value.isPresent()
                && value.get().getValue() instanceof EntityMaid maid && maid.isSleeping()) {
            eraseMemory(MemoryModuleType.ATTACK_TARGET);
            ci.cancel();
        }
    }
}
