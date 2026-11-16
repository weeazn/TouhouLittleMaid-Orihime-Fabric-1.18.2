package com.github.tartaricacid.touhoulittlemaid.entity.ai.navigation;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.path.MaidSelectivePathFinding;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class MaidUnderWaterPathNavigation extends WaterBoundPathNavigation {
    public MaidUnderWaterPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !level.getBlockState(pos.below()).isAir();
    }

    @Override
    protected boolean canMoveDirectly(Vec3 from, Vec3 to) {
        return mob.isInWater() && super.canMoveDirectly(from, to);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new MaidUnderWaterNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new MaidSelectivePathFinding(this.nodeEvaluator, maxVisitedNodes, level);
    }
}
