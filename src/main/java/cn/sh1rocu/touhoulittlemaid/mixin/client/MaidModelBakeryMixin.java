package cn.sh1rocu.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.client.init.InitSpecialItemRender;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public abstract class MaidModelBakeryMixin {
    @Inject(method = "uploadTextures", at = @At("RETURN"))
    private void tlm$wrapSpecialModels(CallbackInfoReturnable<AtlasSet> callback) {
        InitSpecialItemRender.wrapBakedModels(((ModelBakery) (Object) this).getBakedTopLevelModels());
    }
}
