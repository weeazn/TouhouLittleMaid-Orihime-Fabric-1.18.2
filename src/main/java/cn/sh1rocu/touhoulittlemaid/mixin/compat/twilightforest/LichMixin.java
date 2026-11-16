package cn.sh1rocu.touhoulittlemaid.mixin.compat.twilightforest;

import com.github.tartaricacid.touhoulittlemaid.init.MaidDamageTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "twilightforest.entity.boss.Lich")
abstract class LichMixin {
    @WrapOperation(method = {"hurt", "method_5643"}, remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/damagesource/DamageSource;isMagic()Z", remap = true))
    private boolean tlm$breakShield(DamageSource source, Operation<Boolean> original) {
        return MaidDamageTags.matches(MaidDamageTags.LICH, source);
    }
}
