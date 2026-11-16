package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Inject(method = "createEntityIgnoreException", at = @At("HEAD"), cancellable = true)
    private static void createCaptiveMaid(ServerLevelAccessor world, CompoundTag tag, CallbackInfoReturnable<Optional<Entity>> result) {
        String id = tag.getString("id");
        boolean marker = id.equals("touhou_little_maid:outpost_captive");
        if (!marker && !id.equals("minecraft:allay")) return;

        if (world.getRandom().nextDouble() < MaidConfig.REPLACE_ALLAY_PERCENT.get()) {
            result.setReturnValue(Optional.ofNullable(InitEntities.MAID.create(world.getLevel())));
        } else if (marker || !net.minecraft.core.Registry.ENTITY_TYPE.containsKey(new net.minecraft.resources.ResourceLocation(id))) {
            result.setReturnValue(Optional.empty());
        }
    }
}
