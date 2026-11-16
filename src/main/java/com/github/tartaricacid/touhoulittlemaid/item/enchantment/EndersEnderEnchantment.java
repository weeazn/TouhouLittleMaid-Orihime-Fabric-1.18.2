package com.github.tartaricacid.touhoulittlemaid.item.enchantment;

import com.github.tartaricacid.touhoulittlemaid.init.InitEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EndersEnderEnchantment extends Enchantment {
    public EndersEnderEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 20;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.is(InitEnchantments.GOHEI_ENCHANTABLE);
    }
}
