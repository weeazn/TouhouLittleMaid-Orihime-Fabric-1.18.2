package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.BackpackDeposit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.pathfinder.Path;

import java.util.Map;

public class MaidDepositBackpackTask extends Behavior<EntityMaid> {
    private BlockPos destination;
    private WalkTarget walkTarget;
    private long nextSearch;
    private boolean hasRemainder;

    public MaidDepositBackpackTask() {
        super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT), 200);
    }

    private boolean available(EntityMaid maid) {
        return MaidConfig.AUTO_DEPOSIT.get() && maid.isTame() && maid.canBrainMoving()
                && !maid.guiOpening && !maid.isSleeping() && !maid.isOrderedToSit() && !maid.isPassenger()
                && !maid.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        long time = level.getGameTime();
        if (time < nextSearch) return false;
        nextSearch = time + 60 + maid.getRandom().nextInt(20);
        if (!available(maid) || (!hasRemainder && !BackpackDeposit.full(maid.getAvailableBackpackInv()))) return false;
        destination = BackpackDeposit.nearest(level, maid.blockPosition(), MaidConfig.MAID_WORK_RANGE.get(),
                maid.getAvailableBackpackInv(), pos -> {
                    if (!maid.isWithinRestriction(pos)) return false;
                    Path path = maid.getNavigation().createPath(pos, 1);
                    return path != null && path.canReach();
                });
        if (destination == null) hasRemainder = false;
        return destination != null;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long time) {
        walkTarget = new WalkTarget(destination, 0.65f, 1);
        maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long time) {
        return destination != null && available(maid) && level.hasChunkAt(destination)
                && (hasRemainder || BackpackDeposit.full(maid.getAvailableBackpackInv()));
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long time) {
        if (destination.closerToCenterThan(maid.position(), 2.5)) {
            var chest = BackpackDeposit.destination(level, destination);
            if (chest != null) {
                var cargo = maid.getAvailableBackpackInv();
                if (BackpackDeposit.transfer(cargo, chest, true) > 0) {
                    hasRemainder = false;
                    for (int slot = 0; slot < cargo.getSlots(); slot++) {
                        if (!cargo.getStackInSlot(slot).isEmpty()) {
                            hasRemainder = true;
                            break;
                        }
                    }
                    nextSearch = time + 1;
                }
            }
            destination = null;
        } else if (maid.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null) != walkTarget) {
            destination = null;
        }
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long time) {
        if (maid.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null) == walkTarget) {
            maid.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        destination = null;
        walkTarget = null;
    }
}
