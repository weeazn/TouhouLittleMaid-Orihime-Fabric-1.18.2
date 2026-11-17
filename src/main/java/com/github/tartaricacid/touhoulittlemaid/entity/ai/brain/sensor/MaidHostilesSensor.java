package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.sensor;

import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Optional;
import java.util.Set;

public class MaidHostilesSensor extends Sensor<LivingEntity> {
    private static final float CREEPER_DISTANCE = 8.0F;

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    @Override
    protected void doTick(ServerLevel worldIn, LivingEntity entityIn) {
        entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(entityIn));
    }

    private Optional<LivingEntity> getNearestHostile(LivingEntity livingEntity) {
        return this.getVisibleEntities(livingEntity).flatMap(entities ->
                entities.find(enemy -> TagEntity.isCreeper(enemy) && enemy.closerThan(livingEntity, CREEPER_DISTANCE))
                        .min((enemy1, enemy2) -> this.compareMobDistance(livingEntity, enemy1, enemy2)));
    }

    private Optional<NearestVisibleLivingEntities> getVisibleEntities(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    private int compareMobDistance(LivingEntity livingEntity, LivingEntity target1, LivingEntity target2) {
        return Mth.floor(target1.distanceToSqr(livingEntity) - target2.distanceToSqr(livingEntity));
    }
}
