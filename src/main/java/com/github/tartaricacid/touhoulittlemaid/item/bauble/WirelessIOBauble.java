package com.github.tartaricacid.touhoulittlemaid.item.bauble;

import cn.sh1rocu.touhoulittlemaid.util.itemhandler.IItemHandler;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidWirelessIOEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.ChestManager;
import com.github.tartaricacid.touhoulittlemaid.item.ItemWirelessIO;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

import static com.github.tartaricacid.touhoulittlemaid.util.BytesBooleansConvert.bytes2Booleans;

public class WirelessIOBauble implements IMaidBauble {
    private static final int SLOT_NUM = 38;

    @Nonnull
    public static ItemStack insertItemStacked(IItemHandler inventory, @Nonnull ItemStack stack, boolean simulate, @Nullable boolean[] slotConfig) {
        if (stack.isEmpty()) {
            return stack;
        }
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate, slotConfig);
        }
        int sizeInventory = inventory.getSlots();
        for (int i = 0; i < sizeInventory; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (slotConfig != null && i < slotConfig.length && slotConfig[i]) {
                continue;
            }
            if (canItemStacksStackRelaxed(slot, stack)) {
                stack = inventory.insertItem(i, stack, simulate);
                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        if (!stack.isEmpty()) {
            for (int i = 0; i < sizeInventory; i++) {
                if (slotConfig != null && i < slotConfig.length && slotConfig[i]) {
                    continue;
                }
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static ItemStack insertItem(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate, @Nullable boolean[] slotConfig) {
        if (stack.isEmpty()) {
            return stack;
        }
        for (int i = 0; i < dest.getSlots(); i++) {
            if (slotConfig != null && i < slotConfig.length && slotConfig[i]) {
                continue;
            }
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public static boolean canItemStacksStackRelaxed(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem()) {
            return false;
        }
        if (!a.isStackable()) {
            return false;
        }
        if (a.hasTag() != b.hasTag()) {
            return false;
        }
        return (!a.hasTag() || Objects.equals(a.getTag(), Objects.requireNonNull(b.getTag())))/* && a.areCapsCompatible(b)*/;
    }

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.tickCount % 100 == 0 && !maid.guiOpening) {
            BlockPos bindingPos = ItemWirelessIO.getBindingPos(baubleItem);
            if (bindingPos == null) {
                return;
            }
            float maxDistance = maid.getRestrictRadius();
            if (maid.distanceToSqr(bindingPos.getX(), bindingPos.getY(), bindingPos.getZ()) > (maxDistance * maxDistance)) {
                return;
            }
            BlockEntity te = maid.level.getBlockEntity(bindingPos);
            if (te == null) {
                return;
            }
            for (IChestType type : ChestManager.getAllChestTypes()) {
                if (!type.isChest(te)) {
                    continue;
                }
                int openCount = type.getOpenCount(maid.level, bindingPos, te);
                if (openCount > 0) {
                    return;
                }
                Storage<ItemVariant> chestInv = type.storage(te);
                if (chestInv != null) {
                    IItemHandler maidInv = maid.getAvailableInv(false);
                    boolean isMaidToChest = ItemWirelessIO.isMaidToChest(baubleItem);
                    boolean isBlacklist = ItemWirelessIO.isBlacklist(baubleItem);
                    byte[] slotConfig = ItemWirelessIO.getSlotConfig(baubleItem);
                    byte[] slotConfigTmp = null;
                    if (slotConfig != null) {
                        slotConfigTmp = Arrays.copyOf(slotConfig, Math.max(SLOT_NUM, maidInv.getSlots()));
                        slotConfigTmp[maidInv.getSlots() - 2] = slotConfigTmp[SLOT_NUM - 2];
                        slotConfigTmp[maidInv.getSlots() - 1] = slotConfigTmp[SLOT_NUM - 1];
                    }
                    boolean[] slotConfigData = bytes2Booleans(slotConfigTmp, SLOT_NUM);
                    IItemHandler filterList = ItemWirelessIO.getFilterList(baubleItem);

                    if (isMaidToChest) {
                        var event = new MaidWirelessIOEvent.MaidToChest(maid, maidInv, chestInv, filterList, isBlacklist, slotConfigData);
                        MaidWirelessIOEvent.MAID_TO_CHEST.invoker().post(event);
                        if (!event.isCanceled()) {
                            maidToChest(maidInv, chestInv, isBlacklist, filterList, slotConfigData);
                        }
                    } else {
                        var event = new MaidWirelessIOEvent.ChestToMaid(maid, maidInv, chestInv, filterList, isBlacklist, slotConfigData);
                        MaidWirelessIOEvent.CHEST_TO_MAID.invoker().post(event);
                        if (!event.isCanceled()) {
                            chestToMaid(chestInv, maidInv, isBlacklist, filterList, slotConfigData);
                        }
                    }
                }
                if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
                    InitTrigger.MAID_EVENT.trigger(serverPlayer, TriggerType.USE_WIRELESS_IO);
                }
                return;
            }
        }
    }

    private void maidToChest(IItemHandler maid, Storage<ItemVariant> chest, boolean isBlacklist, IItemHandler filterList, boolean[] slotConfig) {
        for (int i = 0; i < maid.getSlots(); i++) {
            if (i < slotConfig.length && slotConfig[i]) {
                continue;
            }
            ItemStack maidInvItem = maid.getStackInSlot(i);
            if (maidInvItem.isEmpty())
                continue;
            boolean allowMove = isBlacklist;
            for (int j = 0; j < filterList.getSlots(); j++) {
                ItemStack filterItem = filterList.getStackInSlot(j);
                boolean isEqual = ItemStack.isSame(maidInvItem, filterItem);
                if (isEqual) {
                    allowMove = !isBlacklist;
                    break;
                }
            }
            if (allowMove) {
                ItemStack extractable = maid.extractItem(i, maidInvItem.getCount(), true);
                if (extractable.isEmpty()) continue;
                try (Transaction transaction = Transaction.openOuter()) {
                    long inserted = chest.insert(ItemVariant.of(extractable), extractable.getCount(), transaction);
                    if (inserted > 0) {
                        maid.extractItem(i, (int) inserted, false);
                        transaction.commit();
                    }
                }
            }
        }
    }

    private void chestToMaid(Storage<ItemVariant> chest, IItemHandler maid, boolean isBlacklist, IItemHandler filterList, boolean[] slotConfig) {
        var contents = new java.util.LinkedHashMap<ItemVariant, Long>();
        try (Transaction scan = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : chest.iterable(scan)) {
                if (!view.isResourceBlank()) contents.merge(view.getResource(), view.getAmount(), Long::sum);
            }
        }
        for (var entry : contents.entrySet()) {
            ItemVariant chestInvStack = entry.getKey();
            boolean allowMove = isBlacklist;
            for (int j = 0; j < filterList.getSlots(); j++) {
                ItemStack filterItem = filterList.getStackInSlot(j);
                boolean isEqual = ItemStack.isSame(chestInvStack.toStack(), filterItem);
                if (isEqual) {
                    allowMove = !isBlacklist;
                    break;
                }
            }
            if (allowMove) {
                int offered = (int) Math.min(entry.getValue(), (long) maid.getSlots() * chestInvStack.getItem().getMaxStackSize());
                int capacity = offered - insertItemStacked(maid, chestInvStack.toStack(offered), true, slotConfig).getCount();
                if (capacity <= 0) continue;
                try (Transaction transaction = Transaction.openOuter()) {
                    int extracted = (int) chest.extract(chestInvStack, capacity, transaction);
                    if (extracted > 0) {
                        insertItemStacked(maid, chestInvStack.toStack(extracted), false, slotConfig);
                        transaction.commit();
                    }
                }
            }
        }
    }
}