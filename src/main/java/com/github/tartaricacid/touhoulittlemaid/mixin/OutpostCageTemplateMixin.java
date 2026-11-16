package com.github.tartaricacid.touhoulittlemaid.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StructureManager.class)
public abstract class OutpostCageTemplateMixin {
    @Shadow private Optional<StructureTemplate> loadFromResource(ResourceLocation name) { throw new AssertionError(); }

    @Inject(method = "loadFromResource", at = @At("RETURN"), cancellable = true)
    private void supplyCaptiveCage(ResourceLocation name, CallbackInfoReturnable<Optional<StructureTemplate>> result) {
        if (result.getReturnValue().isPresent() || !name.equals(new ResourceLocation("minecraft", "pillager_outpost/feature_cage_with_allays"))) return;
        // 1.18 has the empty cage, including its jigsaw connectors, but lacks the captive variant.
        loadFromResource(new ResourceLocation("minecraft", "pillager_outpost/feature_cage2")).ifPresent(cage -> {
            CompoundTag data = cage.save(new CompoundTag());
            ListTag occupants = new ListTag();
            for (int offset = 3; offset <= 4; offset++) {
                CompoundTag marker = new CompoundTag();
                marker.putString("id", "touhou_little_maid:outpost_captive");
                ListTag pos = new ListTag();
                pos.add(DoubleTag.valueOf(offset + 0.5));
                pos.add(DoubleTag.valueOf(0));
                pos.add(DoubleTag.valueOf(offset + 0.5));
                ListTag blockPos = new ListTag();
                blockPos.add(IntTag.valueOf(offset));
                blockPos.add(IntTag.valueOf(0));
                blockPos.add(IntTag.valueOf(offset));
                CompoundTag entry = new CompoundTag();
                entry.put("pos", pos);
                entry.put("blockPos", blockPos);
                entry.put("nbt", marker);
                occupants.add(entry);
            }
            data.put("entities", occupants);
            StructureTemplate captiveCage = new StructureTemplate();
            captiveCage.load(data);
            result.setReturnValue(Optional.of(captiveCage));
        });
    }
}
