package com.github.tartaricacid.touhoulittlemaid.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class NearbyBlockEntities {
    private NearbyBlockEntities() {}

    public static List<BlockPos> find(ServerLevel level, BlockPos center, int radius, Predicate<BlockEntity> filter) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = (center.getX()-radius) >> 4; x <= (center.getX()+radius) >> 4; x++) {
            for (int z = (center.getZ()-radius) >> 4; z <= (center.getZ()+radius) >> 4; z++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
                if (chunk == null) continue;
                chunk.getBlockEntities().forEach((pos, entity) -> {
                    if (pos.distSqr(center) <= radius * radius && filter.test(entity)) positions.add(pos);
                });
            }
        }
        positions.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
        return positions;
    }

}
