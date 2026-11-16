package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ProjectileWeaponItem;

public final class MaidMeleeAttack<E extends Mob> extends Behavior<E> {
    private final int cooldown;
    private final BiPredicate<E, LivingEntity> withinReach;

    public MaidMeleeAttack(int cooldown, BiPredicate<E, LivingEntity> withinReach) {
        super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.cooldown = cooldown;
        this.withinReach = withinReach;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E maid) {
        LivingEntity target = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
        return !maid.isHolding(stack -> stack.getItem() instanceof ProjectileWeaponItem weapon && maid.canFireProjectileWeapon(weapon))
                && withinReach.test(maid, target)
                && maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElseThrow().contains(target);
    }

    @Override
    protected void start(ServerLevel level, E maid, long time) {
        LivingEntity target = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
        maid.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
        maid.swing(InteractionHand.MAIN_HAND);
        maid.doHurtTarget(target);
        AttributeInstance attackSpeed = maid.getAttribute(Attributes.ATTACK_SPEED);
        double speed = attackSpeed == null ? 0 : attackSpeed.getValue();
        long ticks = speed > 0 ? (long) (cooldown / speed) : cooldown;
        maid.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, ticks);
    }
}
