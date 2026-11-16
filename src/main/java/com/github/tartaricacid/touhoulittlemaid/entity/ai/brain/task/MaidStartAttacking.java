package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/** Keep the candidate from this check and briefly back off after an unsuccessful search. */
public final class MaidStartAttacking extends Behavior<EntityMaid> {
    private final Predicate<EntityMaid> hasWeapon;
    private final Function<EntityMaid, Optional<? extends LivingEntity>> findTarget;
    private LivingEntity candidate;
    private long nextSearch;

    public MaidStartAttacking(Predicate<EntityMaid> hasWeapon,
                             Function<EntityMaid, Optional<? extends LivingEntity>> findTarget) {
        super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.hasWeapon = hasWeapon;
        this.findTarget = findTarget;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        long now = level.getGameTime();
        if (now < nextSearch) return false;
        candidate = hasWeapon.test(maid) ? findTarget.apply(maid).orElse(null) : null;
        if (candidate != null && maid.canAttack(candidate)) return true;
        candidate = null;
        nextSearch = now + 5;
        return false;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long time) {
        maid.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, candidate);
        maid.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        candidate = null;
    }
}
