package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import cn.sh1rocu.touhoulittlemaid.util.itemhandler.IItemHandler;
import cn.sh1rocu.touhoulittlemaid.util.itemhandler.ItemHandlerHelper;
import com.github.tartaricacid.touhoulittlemaid.compat.furnaces.FurnaceUpgrade;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.NearbyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import java.util.Map;

public class MaidTendFurnaceTask extends MaidCheckRateTask {
    private BlockPos destination;
    private WalkTarget walk;

    public MaidTendFurnaceTask() {
        super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT), 240);
        setMaxCheckRate(40);
    }

    private static boolean supported(BlockEntity entity) {
        return entity instanceof FurnaceBlockEntity || entity instanceof AbstractFurnaceBlockEntity
                && entity.getBlockState().getBlock() instanceof FurnaceUpgrade;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        if (!super.checkExtraStartConditions(level, maid) || !maid.canBrainMoving() || maid.guiOpening) return false;
        int range = maid.hasRestriction() ? (int) maid.getRestrictRadius() : MaidConfig.MAID_WORK_RANGE.get();
        for (BlockPos pos : NearbyBlockEntities.find(level, maid.getBrainSearchPos(), range, MaidTendFurnaceTask::supported)) {
            if (!maid.isWithinRestriction(pos) || !service(level, maid, pos, true)) continue;
            var path = maid.getNavigation().createPath(pos, 1);
            if (path != null && path.canReach()) {
                destination = pos;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long time) {
        walk = new WalkTarget(destination, 0.65f, 1);
        maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walk);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long time) {
        return destination != null && maid.isWithinRestriction(destination) && level.hasChunkAt(destination) && maid.canBrainMoving() && !maid.guiOpening
                && !maid.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long time) {
        if (destination.closerToCenterThan(maid.position(), 2.5)) {
            service(level, maid, destination, false);
            destination = null;
        } else if (maid.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null) != walk) {
            destination = null;
        }
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long time) {
        if (maid.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null) == walk)
            maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        destination = null;
        walk = null;
    }

    private static boolean service(ServerLevel level, EntityMaid maid, BlockPos pos, boolean simulate) {
        if (!level.hasChunkAt(pos) || !(level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace)
                || !supported(furnace)) return false;
        var data = furnace.saveWithoutMetadata();
        if (!data.getString("Lock").isEmpty() || data.contains("HTM_Lock")) return false;
        IItemHandler cargo = maid.getAvailableBackpackInv();
        boolean moved = collect(cargo, furnace, 2, simulate);
        if (furnace.getItem(1).is(Items.BUCKET) || furnace.getItem(1).is(Items.WATER_BUCKET))
            moved |= collect(cargo, furnace, 1, simulate);
        moved |= supply(level, cargo, furnace, 0, simulate);
        if (!furnace.getItem(0).isEmpty()) moved |= supply(level, cargo, furnace, 1, simulate);
        if (moved && !simulate) furnace.setChanged();
        return moved;
    }

    private static boolean collect(IItemHandler cargo, AbstractFurnaceBlockEntity furnace, int slot, boolean simulate) {
        ItemStack stack = furnace.getItem(slot);
        if (stack.isEmpty() || !furnace.canTakeItemThroughFace(slot, stack, Direction.DOWN)) return false;
        ItemStack left = ItemHandlerHelper.insertItemStacked(cargo, stack.copy(), simulate);
        int count = stack.getCount() - left.getCount();
        if (count > 0 && !simulate) furnace.removeItem(slot, count);
        return count > 0;
    }

    private static boolean supply(ServerLevel level, IItemHandler cargo, AbstractFurnaceBlockEntity furnace, int slot, boolean simulate) {
        ItemStack current = furnace.getItem(slot);
        Direction face = slot == 0 ? Direction.UP : Direction.NORTH;
        for (int i = 0; i < cargo.getSlots(); i++) {
            ItemStack from = cargo.getStackInSlot(i);
            if (from.isEmpty() || (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, from))
                    || !furnace.canPlaceItemThroughFace(slot, from, face)) continue;
            int space = Math.min(furnace.getMaxStackSize(), from.getMaxStackSize()) - current.getCount();
            if (space <= 0 || slot == 1 && !AbstractFurnaceBlockEntity.isFuel(from)) continue;
            if (slot == 0 && level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(from), level).isEmpty()) continue;
            ItemStack taken = cargo.extractItem(i, space, simulate);
            if (taken.isEmpty()) continue;
            if (!simulate) {
                taken.setCount(taken.getCount() + current.getCount());
                furnace.setItem(slot, taken);
            }
            return true;
        }
        return false;
    }
}
