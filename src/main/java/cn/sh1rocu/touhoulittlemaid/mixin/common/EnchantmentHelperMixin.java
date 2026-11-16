package cn.sh1rocu.touhoulittlemaid.mixin.common;

import cn.sh1rocu.touhoulittlemaid.api.extension.IEnchantment;
import com.github.tartaricacid.touhoulittlemaid.init.InitEnchantments;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @WrapOperation(
            method = "getAvailableEnchantmentResults",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private static boolean tlm$canApplyAtEnchantingTable(
            EnchantmentCategory category, Item item, Operation<Boolean> original,
            int level, ItemStack stack, boolean allowTreasure, @Local Enchantment enchantment
    ) {
        if (InitEnchantments.isGoheiEnchantment(enchantment)) return enchantment.canEnchant(stack);
        if (InitEnchantments.isCrossbowEnchantment(enchantment) && stack.is(InitEnchantments.GOHEI_ENCHANTABLE)) return true;
        if (stack.getItem() instanceof IEnchantment ex) {
            return ex.tlm$canApplyAtEnchantingTable(stack, enchantment);
        }
        return original.call(category, item);
    }
}