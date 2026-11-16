package com.github.tartaricacid.touhoulittlemaid.compat.farmersdelight;

import com.nhoryzon.mc.farmersdelight.block.BuddingTomatoBlock;
import com.nhoryzon.mc.farmersdelight.block.RiceCropBlock;
import com.nhoryzon.mc.farmersdelight.block.RiceUpperCropBlock;
import com.nhoryzon.mc.farmersdelight.block.TomatoVineBlock;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public final class FarmersDelightCrops {
    private FarmersDelightCrops() {}

    public static boolean isSeed(ItemStack seed) {
        return seed.is(ItemsRegistry.TOMATO_SEEDS.get()) || seed.is(ItemsRegistry.RICE.get());
    }

    public static boolean canPlant(Level level, BlockPos soil, ItemStack seed) {
        BlockPos crop = soil.above();
        BlockState space = level.getBlockState(crop);
        if (seed.is(ItemsRegistry.RICE.get())) {
            return space.is(Blocks.WATER) && space.getFluidState().isSource()
                    && BlocksRegistry.RICE_CROP.get().defaultBlockState().canSurvive(level, crop);
        }
        return seed.is(ItemsRegistry.TOMATO_SEEDS.get()) && space.getMaterial().isReplaceable()
                && space.getFluidState().isEmpty()
                && BlocksRegistry.BUDDING_TOMATO_CROP.get().defaultBlockState().canSurvive(level, crop);
    }

    public static boolean isColumn(BlockState state) {
        return state.getBlock() instanceof TomatoVineBlock || state.getBlock() instanceof BuddingTomatoBlock
                || state.getBlock() instanceof RiceCropBlock || state.getBlock() instanceof RiceUpperCropBlock;
    }

    public static BlockPos findRipe(Level level, BlockPos first) {
        boolean rice = level.getBlockState(first).getBlock() instanceof RiceCropBlock
                || level.getBlockState(first).getBlock() instanceof RiceUpperCropBlock;
        for (BlockPos pos = first; pos.getY() < level.getMaxBuildHeight(); pos = pos.above()) {
            BlockState state = level.getBlockState(pos);
            if (rice) {
                if (state.getBlock() instanceof RiceUpperCropBlock panicle && panicle.isMaxAge(state)) return pos;
                if (!(state.getBlock() instanceof RiceCropBlock)) break;
            } else {
                if (state.getBlock() instanceof TomatoVineBlock vine) {
                    if (vine.isMaxAge(state)) return pos;
                } else if (!(state.getBlock() instanceof BuddingTomatoBlock)) break;
            }
        }
        return null;
    }

    public static boolean harvestTomato(ServerLevel level, BlockPos pos, Consumer<ItemStack> collect) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TomatoVineBlock vine) || !vine.isMaxAge(state)) return false;
        if (!level.setBlockAndUpdate(pos, state.setValue(vine.getAgeProperty(), 0))) return false;
        collect.accept(new ItemStack(ItemsRegistry.TOMATO.get(), 1 + level.random.nextInt(2)));
        if (level.random.nextFloat() < 0.05f) collect.accept(new ItemStack(ItemsRegistry.ROTTEN_TOMATO.get()));
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1f,
                0.8f + level.random.nextFloat() * 0.4f);
        return true;
    }
}
