package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableSet;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MaidNearestLivingEntitySensor extends Sensor<EntityMaid> {
    @Override
    protected void doTick(ServerLevel world, EntityMaid maid) {
        AABB aabb = maid.searchDimension();
        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, aabb, (entity) -> entity != maid && entity.isAlive());
        list.sort(Comparator.comparingDouble(maid::distanceToSqr));
        Brain<EntityMaid> brain = maid.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, list);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(maid, list));
        LivingEntity current = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (current != null && maid.getTask() instanceof IRangedAttackTask && !maid.getSensing().hasLineOfSight(current)) {
            // Preserve pursuit when nothing else is visible. Recheck alternatives at sensor cadence.
            // Forget the blocked target only. The combat behavior rechecks weapon and ammo before choosing another.
            if (IRangedAttackTask.findFirstValidAttackTarget(maid).filter(candidate -> candidate != current).isPresent()) {
                brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}
