package com.github.tartaricacid.touhoulittlemaid.entity.task.crop;

import io.github.fabricators_of_create.porting_lib.extensions.BlockStateExtensions;
import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.util.PlantType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public final class CropPlanting {
    private static final TagKey<Item> SEEDS = TagKey.create(Registry.ITEM_REGISTRY,
            new ResourceLocation("touhou_little_maid", "maid_plantable_seeds"));

    private CropPlanting() {}

    public static boolean isSeed(ItemStack stack) {
        if (stack.is(SEEDS) && stack.getItem() instanceof BlockItem) return true;
        return stack.getItem() instanceof ItemNameBlockItem seed && seed.getBlock() instanceof IPlantable plant
                && plant.getPlantType(EmptyBlockGetter.INSTANCE, BlockPos.ZERO) == PlantType.CROP
                && !plant.getPlant(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).is(Blocks.AIR);
    }

    public static boolean canPlant(LevelReader level, BlockPos soilPos, BlockState soil, ItemStack stack) {
        Material above = level.getBlockState(soilPos.above()).getMaterial();
        if (!above.isReplaceable() || above.isLiquid()) return false;
        if (stack.is(SEEDS) && stack.getItem() instanceof BlockItem seed) {
            return seed.getBlock().defaultBlockState().canSurvive(level, soilPos.above());
        }
        return stack.getItem() instanceof ItemNameBlockItem seed && seed.getBlock() instanceof IPlantable plant
                && ((BlockStateExtensions) soil).canSustainPlant(level, soilPos, Direction.UP, plant);
    }
}
