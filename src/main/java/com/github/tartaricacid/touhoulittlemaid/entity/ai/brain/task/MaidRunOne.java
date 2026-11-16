package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import net.minecraft.world.entity.ai.behavior.Behavior;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.RunOne;
import java.util.List;
import java.util.function.Predicate;

public class MaidRunOne extends RunOne<EntityMaid> {
    private final Predicate<EntityMaid> enableCondition;

    public MaidRunOne(List<Pair<? extends Behavior<? super EntityMaid>, Integer>> entryCondition, Predicate<EntityMaid> enableCondition) {
        super(entryCondition.stream().map(entry -> Pair.<Behavior<? super EntityMaid>, Integer>of(entry.getFirst(), entry.getSecond())).toList());
        this.enableCondition = enableCondition;
    }

    public MaidRunOne(List<Pair<? extends Behavior<? super EntityMaid>, Integer>> entryCondition) {
        this(entryCondition, maid -> true);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, EntityMaid maid) {
        if (!enableCondition.test(maid)) {
            return false;
        }
        return !maid.isBegging() && !maid.isSleeping() && !(maid.getVehicle() instanceof EntitySit) && super.checkExtraStartConditions(pLevel, maid);
    }
}