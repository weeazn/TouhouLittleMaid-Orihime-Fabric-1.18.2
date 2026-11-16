package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(PillagerOutpostPools.class)
public abstract class PillagerOutpostPoolsMixin {
    @ModifyArg(method = "<clinit>", index = 2,
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=pillager_outpost/features")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool$Projection;)V"))
    private static List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> addCaptiveCage(
            List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> features) {
        var result = new ArrayList<>(features);
        var cage = StructurePoolElement.legacy("minecraft:pillager_outpost/feature_cage_with_allays");
        result.add(Pair.of(cage, 1));
        return result;
    }
}
