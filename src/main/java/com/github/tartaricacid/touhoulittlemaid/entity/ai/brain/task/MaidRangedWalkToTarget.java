package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import net.minecraft.world.entity.ai.behavior.Behavior;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

/**
 * 修改了原版的走向攻击目标的 AI，现在能够依据超远视距和 home 范围进行行走判断
 * <p>
 * 主要用于远程射击，近战的还是请走 SetWalkTargetFromAttackTargetIfTargetOutOfReach
 */
public final class MaidRangedWalkToTarget extends Behavior<EntityMaid> {
    private final Function<LivingEntity, Float> speed;

    private MaidRangedWalkToTarget(Function<LivingEntity, Float> speed) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.REGISTERED));
        this.speed = speed;
    }

    public static Behavior<EntityMaid> create(float speed) {
        return create(entity -> speed);
    }

    public static Behavior<EntityMaid> create(Function<LivingEntity, Float> speed) {
        return new MaidRangedWalkToTarget(speed);
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long time) {
        var brain = maid.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
        if (maid.canSee(target) && shouldEraseWalkTarget(maid, target)) {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        } else {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(target, false), speed.apply(maid), 0));
        }
    }

    private static boolean shouldEraseWalkTarget(EntityMaid maid, LivingEntity target) {
        float restrictRadius = maid.getRestrictRadius() - 2;
        double checkRadius = 8;
        if (maid.hasRestriction()) {
            BlockPos center = maid.getRestrictCenter();
            checkRadius = Math.sqrt(target.distanceToSqr(center.getX(), center.getY(), center.getZ()));
        } else if (maid.getOwner() instanceof Player player) {
            checkRadius = target.distanceTo(player);
        }
        // 如果目标在 4 格外，在 最大限制范围-2 内，可以直接走过去，否则清除目标
        return checkRadius <= 4 || checkRadius >= restrictRadius;
    }
}
