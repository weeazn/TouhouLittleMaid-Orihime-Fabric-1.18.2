package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ItemEntityIdCopy extends Item {
    public ItemEntityIdCopy() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> components, TooltipFlag pIsAdvanced) {
        components.add(new TranslatableComponent("tooltips.touhou_little_maid.entity_id_copy.desc").withStyle(ChatFormatting.GRAY));
    }
}
