package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.item.enchantment.EndersEnderEnchantment;
import com.github.tartaricacid.touhoulittlemaid.item.enchantment.ImpedingEnchantment;
import com.github.tartaricacid.touhoulittlemaid.item.enchantment.SpeedyEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class InitEnchantments {
    public static void init() {

    }

    public static final Enchantment IMPEDING = register("impeding", new ImpedingEnchantment());
    public static final Enchantment SPEEDY = register("speedy", new SpeedyEnchantment());
    public static final Enchantment ENDERS_ENDER = register("enders_ender", new EndersEnderEnchantment());

    public static final TagKey<Item> GOHEI_ENCHANTABLE = TagKey.create(Registry.ITEM_REGISTRY,
            new ResourceLocation(TouhouLittleMaid.MOD_ID, "gohei_enchantable"));

    public static boolean isGoheiEnchantment(Enchantment enchantment) {
        return enchantment == IMPEDING || enchantment == SPEEDY || enchantment == ENDERS_ENDER;
    }

    public static boolean isCrossbowEnchantment(Enchantment enchantment) {
        // Some 1.18 mods replace an enchantment's category to support additional weapons.
        return enchantment.category == EnchantmentCategory.CROSSBOW || enchantment == Enchantments.PIERCING
                || enchantment == Enchantments.MULTISHOT || enchantment == Enchantments.QUICK_CHARGE;
    }

    private static Enchantment register(String name, Enchantment enchantment) {

        return Registry.register(Registry.ENCHANTMENT, new ResourceLocation(TouhouLittleMaid.MOD_ID, name), enchantment);
    }
}
