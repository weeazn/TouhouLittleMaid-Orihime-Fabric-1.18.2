package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class MaidCheckRateTask extends Behavior<EntityMaid> {
    private int maxCheckRate = 20;
    private int nextCheckTickCount;

    public MaidCheckRateTask(Map<MemoryModuleType<?>, MemoryStatus> requiredMemoryStateIn) {
        super(requiredMemoryStateIn);
    }

    public MaidCheckRateTask(Map<MemoryModuleType<?>, MemoryStatus> requiredMemoryStateIn, int duration) {
        super(requiredMemoryStateIn, duration);
    }

    public MaidCheckRateTask(Map<MemoryModuleType<?>, MemoryStatus> requiredMemoryStateIn, int durationMinIn, int durationMaxIn) {
        super(requiredMemoryStateIn, durationMinIn, durationMaxIn);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        if (this.nextCheckTickCount > 0) {
            --this.nextCheckTickCount;
            return false;
        }
        this.nextCheckTickCount = maxCheckRate + owner.getRandom().nextInt(maxCheckRate);
        return true;
    }

    @Nullable
    protected static BlockPos findReachablePoi(ServerLevel level, EntityMaid maid, Predicate<PoiType> type, Predicate<BlockPos> available) {
        Set<BlockPos> positions = level.getPoiManager().getInRange(type, maid.getBrainSearchPos(),
                        (int) maid.getRestrictRadius(), PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos).filter(maid::isWithinRestriction).filter(available).collect(Collectors.toSet());
        if (positions.isEmpty()) return null;
        Path path = maid.getNavigation().createPath(positions, 1);
        return path != null && path.canReach() && positions.contains(path.getTarget()) ? path.getTarget() : null;
    }

    protected void setMaxCheckRate(int maxCheckRate) {
        this.maxCheckRate = maxCheckRate;
    }

    protected void setNextCheckTickCount(int nextCheckTickCount) {
        this.nextCheckTickCount = nextCheckTickCount;
    }
}
