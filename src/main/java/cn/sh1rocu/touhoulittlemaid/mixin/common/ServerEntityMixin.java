package cn.sh1rocu.touhoulittlemaid.mixin.common;

import cn.sh1rocu.touhoulittlemaid.api.extension.IEntityAdditionalSpawnData;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "sendPairingData", at = @At("TAIL"))
    private void sendAdditionalSpawnData(Consumer<Packet<?>> send, CallbackInfo ci) {
        if (entity instanceof IEntityAdditionalSpawnData extra) {
            send.accept(IEntityAdditionalSpawnData.additionalData(entity, extra));
        }
    }
}
