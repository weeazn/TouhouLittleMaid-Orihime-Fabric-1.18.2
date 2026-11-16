package cn.sh1rocu.touhoulittlemaid.mixin.common;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.PlayMaidSoundMessage;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Parrot.class)
abstract class ParrotMixin {
    @WrapOperation(method = "imitateNearbyMobs", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void tlm$imitateMaid(Level level, Player player, double x, double y, double z, SoundEvent event,
            SoundSource category, float volume, float pitch, Operation<Void> original,
            @Local Mob selected, @Local(argsOnly = true) Entity imitator) {
        if (selected instanceof EntityMaid maid) {
            if (!level.isClientSide) {
                SoundEvent voice = SoundUtil.environmentSound(maid, event, 0.5F);
                NetworkHandler.sendToNearby(imitator, PlayMaidSoundMessage.AT_POS,
                        PlayMaidSoundMessage.encodeAtPos(voice, maid.getSoundPackId(), x, y, z, volume, pitch), 16);
            }
        } else {
            original.call(level, player, x, y, z, event, category, volume, pitch);
        }
    }
}
