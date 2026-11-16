package com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.accessories;

import com.github.tartaricacid.touhoulittlemaid.compat.extracontainer.accessories.AccessoriesSlotRef;
import com.github.tartaricacid.touhoulittlemaid.compat.sbackpack.SBackpackCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import java.util.function.Predicate;

public class SBackpackSlotRef extends AccessoriesSlotRef {
    public SBackpackSlotRef(String slotType, int slotIndex) {
        super(slotType, slotIndex);
    }

    public ItemStack getBackpackStack(EntityMaid maid) {
        ItemStack stack = getCuriosStack(maid);
        if (SBackpackCompat.isBackpack(stack)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("unchecked")
    private Storage<ItemVariant> inventory(EntityMaid maid) {
        ItemStack bag = getBackpackStack(maid);
        if (bag.isEmpty()) return null;
        var wrapper = BackpackWrapperLookup.get(bag).orElse(null);
        if (wrapper == null) return null;
        Object handler = wrapper.getInventoryForUpgradeProcessing();
        return handler instanceof Storage<?> storage ? (Storage<ItemVariant>) storage : null;
    }

    @Override
    public boolean containing(EntityMaid maid, ItemStack wanted) {
        var inventory = inventory(maid);
        if (inventory == null || wanted.isEmpty()) return false;
        var item = ItemVariant.of(wanted);
        for (var view : inventory.iterable(null)) {
            if (view.getAmount() > 0 && item.equals(view.getResource())) return true;
        }
        return false;
    }

    @Override
    public ItemStack insert(EntityMaid maid, ItemStack offered, boolean simulate) {
        var inventory = inventory(maid);
        if (inventory == null || offered.isEmpty()) return offered;
        try (Transaction transaction = Transaction.openOuter()) {
            long moved = inventory.insert(ItemVariant.of(offered), offered.getCount(), transaction);
            if (!simulate) transaction.commit();
            ItemStack remainder = offered.copy();
            remainder.shrink((int) moved);
            return remainder;
        }
    }

    @Override
    public ItemStack extract(EntityMaid maid, Predicate<ItemStack> filter, int maxCount) {
        var inventory = inventory(maid);
        if (inventory == null) return ItemStack.EMPTY;
        try (Transaction transaction = Transaction.openOuter()) {
            for (var view : inventory.iterable(transaction)) {
                if (view.isResourceBlank() || view.getAmount() <= 0) continue;
                ItemVariant variant = view.getResource();
                ItemStack sample = variant.toStack((int) Math.min(view.getAmount(), variant.toStack().getMaxStackSize()));
                if (!filter.test(sample)) continue;
                long limit = maxCount < 0 ? sample.getCount() : Math.min(sample.getCount(), maxCount);
                long moved = view.extract(variant, limit, transaction);
                if (moved > 0) {
                    transaction.commit();
                    return variant.toStack((int) moved);
                }
            }
            return ItemStack.EMPTY;
        }
    }
}
