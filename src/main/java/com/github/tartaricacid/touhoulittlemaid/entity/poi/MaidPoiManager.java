package com.github.tartaricacid.touhoulittlemaid.entity.poi;

import com.github.tartaricacid.touhoulittlemaid.block.BlockPicnicMat;
import com.github.tartaricacid.touhoulittlemaid.block.BlockScarecrow;
import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Set;

public final class MaidPoiManager {
    private static final Set<BlockState> JOYS = ImmutableList.of(InitBlocks.COMPUTER, InitBlocks.BOOKSHELF, InitBlocks.KEYBOARD,
                    InitBlocks.GOMOKU, InitBlocks.CCHESS, InitBlocks.WCHESS)
            .stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .collect(ImmutableSet.toImmutableSet());
    private static final Set<BlockState> BEDS = ImmutableList.of(InitBlocks.MAID_BED)
            .stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(blockState -> blockState.getValue(BedBlock.PART) == BedPart.HEAD)
            .filter(blockState -> !blockState.getValue(BedBlock.OCCUPIED))
            .collect(ImmutableSet.toImmutableSet());
    private static final Set<BlockState> HOME_MEAL = ImmutableList.of(InitBlocks.PICNIC_MAT)
            .stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(blockState -> blockState.getValue(BlockPicnicMat.PART).isCenter())
            .collect(ImmutableSet.toImmutableSet());
    private static final Set<BlockState> SCARECROW = ImmutableList.of(InitBlocks.SCARECROW)
            .stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(blockState -> blockState.getValue(BlockScarecrow.HALF) == DoubleBlockHalf.LOWER)
            .collect(ImmutableSet.toImmutableSet());

    public static Set<BlockState> getMaidBed() {
        return BEDS;
    }

    public static Set<BlockState> getJoyBlock() {
        return JOYS;
    }

    public static Set<BlockState> getHomeMeal() {
        return HOME_MEAL;
    }

    public static Set<BlockState> getScarecrow() {
        return SCARECROW;
    }
}
