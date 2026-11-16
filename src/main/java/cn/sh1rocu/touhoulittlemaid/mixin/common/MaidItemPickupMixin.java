package cn.sh1rocu.touhoulittlemaid.mixin.common;

import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MaidItemPickupMixin {
    @Inject(method = "playerTouch", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
    private void tlm$pickupAdvancement(Player player, CallbackInfo callback, @com.llamalad7.mixinextras.sugar.Local net.minecraft.world.item.Item pickedUp) {
        if (player instanceof ServerPlayer server && pickedUp == InitItems.GARAGE_KIT) {
            var thrower = ((ItemEntity) (Object) this).getThrower();
            if (thrower != null && server.getLevel().getEntity(thrower) != null) {
                InitTrigger.MAID_EVENT.trigger(server, "pickup_garage_kit");
            }
        }
    }
}
