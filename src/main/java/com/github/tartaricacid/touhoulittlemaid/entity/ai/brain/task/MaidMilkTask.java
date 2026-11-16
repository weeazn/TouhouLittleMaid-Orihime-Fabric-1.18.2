package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import cn.sh1rocu.touhoulittlemaid.util.itemhandler.CombinedInvWrapper;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.Items;

public class MaidMilkTask extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = 40;
    private final float speedModifier;
    private Cow milkTarget;

    public MaidMilkTask(float speedModifier) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = speedModifier;
        this.setMaxCheckRate(MAX_DELAY_TIME);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel worldIn, EntityMaid owner) {
        if (super.checkExtraStartConditions(worldIn, owner)) {
            CombinedInvWrapper availableInv = owner.getAvailableInv(true);
            return ItemsUtil.fillContainer(availableInv, Items.BUCKET, Items.MILK_BUCKET, true);
        }
        return false;
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid maid, long gameTimeIn) {
        milkTarget = getEntities(maid).find(e -> e instanceof Cow && !e.isBaby() && maid.isWithinRestriction(e.blockPosition()))
                .map(Cow.class::cast)
                .filter(cow -> cow.closerThan(maid, 2) || maid.canPathReach(cow))
                .findFirst().orElse(null);
        if (milkTarget != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(maid, milkTarget, speedModifier, 1);
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long gameTime) {
        return milkTarget != null && milkTarget.isAlive() && !milkTarget.isBaby()
                && maid.isWithinRestriction(milkTarget.blockPosition());
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long gameTime) {
        if (!milkTarget.closerThan(maid, 2) || !maid.hasLineOfSight(milkTarget)) return;
        if (ItemsUtil.fillContainer(maid.getAvailableInv(false), Items.BUCKET, Items.MILK_BUCKET, false)) {
            maid.swing(InteractionHand.MAIN_HAND);
            maid.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
        }
        milkTarget = null;
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long gameTime) {
        milkTarget = null;
    }

    private NearestVisibleLivingEntities getEntities(EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
    }
}
