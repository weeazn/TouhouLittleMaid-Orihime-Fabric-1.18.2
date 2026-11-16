package cn.sh1rocu.touhoulittlemaid.mixin.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.PerspectiveBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class MaidItemRendererMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private BakedModel tlm$chooseDisplayModel(BakedModel model, ItemStack stack, TransformType transform) {
        return model instanceof PerspectiveBakedModel perspective ? perspective.forTransform(transform) : model;
    }
}
