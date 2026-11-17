package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobTargetMixin {
    @Shadow private LivingEntity target;

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
    private LivingEntity tlm$skipSleepingMaid(LivingEntity target) {
        return target instanceof EntityMaid maid && maid.isSleeping() ? null : target;
    }

    @Inject(method = "getTarget", at = @At("HEAD"))
    private void tlm$forgetSleepingMaid(CallbackInfoReturnable<LivingEntity> callback) {
        if (target instanceof EntityMaid maid && maid.isSleeping()) target = null;
    }
}
