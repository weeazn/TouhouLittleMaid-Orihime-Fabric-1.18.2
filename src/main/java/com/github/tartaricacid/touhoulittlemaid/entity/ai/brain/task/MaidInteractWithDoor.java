package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.LevelEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import javax.annotation.Nullable;
import java.util.*;

public final class MaidInteractWithDoor extends Behavior<LivingEntity> {
    private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 3;
    private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 8;
    private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2;

    private Node lastNode;
    private int cooldown;

    public MaidInteractWithDoor() {
        super(Map.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.DOORS_TO_CLOSE, MemoryStatus.REGISTERED,
                MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity entity) {
        Path path = entity.getBrain().getMemory(MemoryModuleType.PATH).orElseThrow();
        if (path.notStarted() || path.isDone()) return false;
        if (Objects.equals(lastNode, path.getNextNode())) {
            cooldown = COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE;
            return true;
        }
        return --cooldown <= 0;
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity entity, long time) {
        Brain<?> brain = entity.getBrain();
        Path path = brain.getMemory(MemoryModuleType.PATH).orElseThrow();
        Optional<Set<GlobalPos>> doorToClosePos = brain.getMemory(MemoryModuleType.DOORS_TO_CLOSE);
        boolean canOpenDoor = entity instanceof EntityMaid maid && maid.getConfigManager().isOpenDoor();
        boolean canOpenFenceGate = entity instanceof EntityMaid maid && maid.getConfigManager().isOpenFenceGate();

        lastNode = path.getNextNode();
        Node previousNode = path.getPreviousNode();
        Node nextNode = path.getNextNode();
        BlockPos previousNodeBlockPos = previousNode.asBlockPos();
        BlockState previousNodeBlockState = serverLevel.getBlockState(previousNodeBlockPos);

        if (canOpenDoor && previousNodeBlockState.is(BlockTags.WOODEN_DOORS, (stateBase) -> stateBase.getBlock() instanceof DoorBlock)) {
            DoorBlock doorBlock = (DoorBlock) previousNodeBlockState.getBlock();
            if (!doorBlock.isOpen(previousNodeBlockState)) {
                doorBlock.setOpen(entity, serverLevel, previousNodeBlockState, previousNodeBlockPos, true);
            }
            doorToClosePos = rememberDoorToClose(brain, doorToClosePos, serverLevel, previousNodeBlockPos);
        } else if (canOpenFenceGate && previousNodeBlockState.is(BlockTags.FENCE_GATES, (stateBase) -> stateBase.getBlock() instanceof FenceGateBlock)) {
            if (!previousNodeBlockState.getValue(FenceGateBlock.OPEN)) {
                setFenceGate(entity, serverLevel, previousNodeBlockState, previousNodeBlockPos, true);
            }
            doorToClosePos = rememberDoorToClose(brain, doorToClosePos, serverLevel, previousNodeBlockPos);
        }

        BlockPos nextNodeBlockPos = nextNode.asBlockPos();
        BlockState nextNodeBlockState = serverLevel.getBlockState(nextNodeBlockPos);
        if (canOpenDoor && nextNodeBlockState.is(BlockTags.WOODEN_DOORS, (stateBase) -> stateBase.getBlock() instanceof DoorBlock)) {
            DoorBlock doorBlock = (DoorBlock) nextNodeBlockState.getBlock();
            if (!doorBlock.isOpen(nextNodeBlockState)) {
                doorBlock.setOpen(entity, serverLevel, nextNodeBlockState, nextNodeBlockPos, true);
                doorToClosePos = rememberDoorToClose(brain, doorToClosePos, serverLevel, nextNodeBlockPos);
            }
        } else if (canOpenFenceGate && nextNodeBlockState.is(BlockTags.FENCE_GATES, (stateBase) -> stateBase.getBlock() instanceof FenceGateBlock)) {
            if (!nextNodeBlockState.getValue(FenceGateBlock.OPEN)) {
                setFenceGate(entity, serverLevel, nextNodeBlockState, nextNodeBlockPos, true);
                doorToClosePos = rememberDoorToClose(brain, doorToClosePos, serverLevel, nextNodeBlockPos);
            }
        }

        doorToClosePos.ifPresent((doorPos) -> closeDoorsThatIHaveOpenedOrPassedThrough(serverLevel, entity, previousNode, nextNode, doorPos, brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES)));
    }

    public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerLevel serverLevel, LivingEntity entity, @Nullable Node previous, @Nullable Node next, Set<GlobalPos> doorPositions, Optional<List<LivingEntity>> nearestLivingEntities) {
        Iterator<GlobalPos> doorPosIterator = doorPositions.iterator();
        while (doorPosIterator.hasNext()) {
            GlobalPos globalPos = doorPosIterator.next();
            BlockPos blockPos = globalPos.pos();
            if ((previous == null || !previous.asBlockPos().equals(blockPos) || (next != null && entity.blockPosition().equals(next.asBlockPos())))
                    && (next == null || !next.asBlockPos().equals(blockPos))) {
                if (isDoorTooFarAway(serverLevel, entity, globalPos)) {
                    doorPosIterator.remove();
                } else {
                    BlockState blockstate = serverLevel.getBlockState(blockPos);

                    if (blockstate.is(BlockTags.WOODEN_DOORS, (stateBase) -> stateBase.getBlock() instanceof DoorBlock)) {
                        DoorBlock doorblock = (DoorBlock) blockstate.getBlock();
                        if (!doorblock.isOpen(blockstate)) {
                            doorPosIterator.remove();
                        } else if (areOtherMobsComingThroughDoor(entity, blockPos, nearestLivingEntities)) {
                            doorPosIterator.remove();
                        } else {
                            doorblock.setOpen(entity, serverLevel, blockstate, blockPos, false);
                            doorPosIterator.remove();
                        }
                    } else if ((blockstate.is(BlockTags.FENCE_GATES, (stateBase) -> stateBase.getBlock() instanceof FenceGateBlock))) {
                        if (!blockstate.getValue(FenceGateBlock.OPEN)) {
                            doorPosIterator.remove();
                        } else if (areOtherMobsComingThroughDoor(entity, blockPos, nearestLivingEntities)) {
                            doorPosIterator.remove();
                        } else {
                            setFenceGate(entity, serverLevel, blockstate, blockPos, false);
                        }
                    } else {
                        doorPosIterator.remove();
                    }
                }
            }
        }

    }

    private static boolean areOtherMobsComingThroughDoor(LivingEntity entity, BlockPos pos, Optional<List<LivingEntity>> nearestLivingEntities) {
        return nearestLivingEntities.map(entities -> entities.stream()
                .filter((livingEntity) -> livingEntity.getType() == entity.getType())
                .filter((livingEntity) -> pos.closerToCenterThan(livingEntity.position(), MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS))
                .anyMatch((livingEntity) -> isMobComingThroughDoor(livingEntity.getBrain(), pos))).orElse(false);
    }

    private static boolean isMobComingThroughDoor(Brain<?> brain, BlockPos pos) {
        if (!brain.hasMemoryValue(MemoryModuleType.PATH)) {
            return false;
        } else {
            Path path = brain.getMemory(MemoryModuleType.PATH).get();
            if (path.isDone()) {
                return false;
            } else {
                Node previousNode = path.getPreviousNode();
                if (previousNode == null) {
                    return false;
                } else {
                    Node nextNode = path.getNextNode();
                    return pos.equals(previousNode.asBlockPos()) || pos.equals(nextNode.asBlockPos());
                }
            }
        }
    }

    private static boolean isDoorTooFarAway(ServerLevel level, LivingEntity entity, GlobalPos pos) {
        return pos.dimension() != level.dimension() || !pos.pos().closerToCenterThan(entity.position(), SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN);
    }

    private static Optional<Set<GlobalPos>> rememberDoorToClose(Brain<?> brain, Optional<Set<GlobalPos>> doorPositions, ServerLevel level, BlockPos blockPos) {
        GlobalPos globalPos = GlobalPos.of(level.dimension(), blockPos);
        return Optional.of(doorPositions.map((pos) -> {
            pos.add(globalPos);
            return pos;
        }).orElseGet(() -> {
            Set<GlobalPos> posSet = Sets.newHashSet(globalPos);
            brain.setMemory(MemoryModuleType.DOORS_TO_CLOSE, posSet);
            return posSet;
        }));
    }

    private static void setFenceGate(@Nullable Entity entity, Level serverLevel, BlockState blockstate, BlockPos blockPos, boolean isOpen) {
        serverLevel.setBlock(blockPos, blockstate.setValue(FenceGateBlock.OPEN, isOpen), Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        serverLevel.levelEvent(null, isOpen ? LevelEvent.SOUND_OPEN_FENCE_GATE : LevelEvent.SOUND_CLOSE_FENCE_GATE, blockPos, 0);
        serverLevel.gameEvent(entity, isOpen ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockPos);
    }
}