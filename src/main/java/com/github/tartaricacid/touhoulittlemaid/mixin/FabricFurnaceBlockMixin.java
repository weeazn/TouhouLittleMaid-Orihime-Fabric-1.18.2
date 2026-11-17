package com.github.tartaricacid.touhoulittlemaid.mixin;

import com.github.tartaricacid.touhoulittlemaid.compat.furnaces.FurnaceUpgrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

// Fabric Furnaces supplies the three getters on the block, including crystal variants.
@Pseudo
@Mixin(targets = "draylar.fabricfurnaces.block.FabricFurnaceBlock", remap = false)
abstract class FabricFurnaceBlockMixin implements FurnaceUpgrade {}
