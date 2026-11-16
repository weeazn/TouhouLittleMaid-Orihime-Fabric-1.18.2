package cn.sh1rocu.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.client.entity.GeckoMaidEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class EntityMixin implements GeckoMaidEntity.Owner {
    @Unique private GeckoMaidEntity<?> tlm$animator;

    @Override
    public GeckoMaidEntity<?> tlm$getAnimator() {
        return tlm$animator;
    }

    @Override
    public void tlm$setAnimator(GeckoMaidEntity<?> animator) {
        tlm$animator = animator;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob.getLevel().isClientSide && tlm$animator == null) {
            IMaid maid = IMaid.convert(mob);
            if (maid != null) {
                GeckoMaidEntity.create(mob, maid);
            }
        }
    }
}
