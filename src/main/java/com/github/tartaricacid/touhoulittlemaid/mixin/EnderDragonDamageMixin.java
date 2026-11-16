package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.github.tartaricacid.touhoulittlemaid.init.MaidDamageTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragon.class)
abstract class EnderDragonDamageMixin {
    @WrapOperation(method = "hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;isExplosion()Z"))
    private boolean tlm$allowEnderDanmaku(DamageSource source, Operation<Boolean> original) {
        return MaidDamageTags.matches(MaidDamageTags.DRAGON, source);
    }
}
