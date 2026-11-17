package com.github.tartaricacid.touhoulittlemaid.compat.furnaces;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public interface FurnaceUpgrade {
    double getSpeedModifier();
    double getFuelModifier();
    int getDuplicationChance();

    static FurnaceUpgrade of(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof FurnaceUpgrade furnace
                ? furnace : null;
    }
}
