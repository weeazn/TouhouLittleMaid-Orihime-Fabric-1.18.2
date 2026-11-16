package com.github.tartaricacid.touhoulittlemaid.compat.tbackpack.accessories;

import com.github.tartaricacid.touhoulittlemaid.compat.extracontainer.accessories.AccessoriesSlotRef;
import com.github.tartaricacid.touhoulittlemaid.compat.tbackpack.TBackpackCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class TBackpackSlotRef extends AccessoriesSlotRef {
    public TBackpackSlotRef(String slotType, int slotIndex) {
        super(slotType, slotIndex);
    }

    private ItemStack getBackpackStack(EntityMaid maid) {
        ItemStack stack = getCuriosStack(maid);
        if (TBackpackCompat.isBackpack(stack)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private TravelersBackpackInventory openCopy(ItemStack bag, EntityMaid maid) {
        Player owner = maid.getOwner() instanceof Player player ? player : null;
        return new TravelersBackpackInventory(bag.copy(), owner, Reference.NO_SCREEN_ID);
    }

    @Override
    public boolean containing(EntityMaid maid, ItemStack wanted) {
        ItemStack bag = getBackpackStack(maid);
        if (bag.isEmpty() || wanted.isEmpty()) return false;
        var contents = openCopy(bag, maid).getInventory();
        for (int i = 0; i <= Reference.END_OF_INVENTORY; i++) {
            if (ItemStack.isSameItemSameTags(contents.getItem(i), wanted)) return true;
        }
        return false;
    }

    @Override
    public ItemStack insert(EntityMaid maid, ItemStack offered, boolean simulate) {
        ItemStack bag = getBackpackStack(maid);
        if (offered.isEmpty() || bag.isEmpty()) return offered;
        var inventory = openCopy(bag, maid);
        var contents = inventory.getInventory();
        ItemStack remaining = offered.copy();
        for (int i = 0; i <= Reference.END_OF_INVENTORY && !remaining.isEmpty(); i++) {
            var slot = new BackpackSlot(contents, i, 0, 0);
            if (slot.mayPlace(remaining)) slot.safeInsert(remaining);
        }
        if (!simulate && remaining.getCount() != offered.getCount()) {
            inventory.writeItems(bag.getOrCreateTag());
        }
        return remaining;
    }

    @Override
    public ItemStack extract(EntityMaid maid, Predicate<ItemStack> filter, int maxCount) {
        ItemStack bag = getBackpackStack(maid);
        if (bag.isEmpty() || maxCount == 0) return ItemStack.EMPTY;
        var inventory = openCopy(bag, maid);
        var contents = inventory.getInventory();
        for (int i = 0; i <= Reference.END_OF_INVENTORY; i++) {
            ItemStack stack = contents.getItem(i);
            if (stack.isEmpty() || !filter.test(stack)) continue;
            int limit = maxCount < 0 ? stack.getMaxStackSize() : Math.min(maxCount, stack.getMaxStackSize());
            ItemStack extracted = contents.removeItem(i, limit);
            inventory.writeItems(bag.getOrCreateTag());
            return extracted;
        }
        return ItemStack.EMPTY;
    }
}
