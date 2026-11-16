package com.github.tartaricacid.touhoulittlemaid.entity.task;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.task.IFarmTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.ISpecialCropHandler;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.crop.SpecialCropManager;
import com.github.tartaricacid.touhoulittlemaid.mixin.accessor.CropBlockAccessor;
import net.minecraft.core.BlockPos;
import com.github.tartaricacid.touhoulittlemaid.entity.task.crop.CropPlanting;
import com.github.tartaricacid.touhoulittlemaid.compat.farmersdelight.FarmersDelightCrops;
import net.fabricmc.loader.api.FabricLoader;
import cn.sh1rocu.touhoulittlemaid.util.itemhandler.ItemHandlerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class TaskNormalFarm implements IFarmTask {
    private static final boolean FARMERS_DELIGHT = FabricLoader.getInstance().isModLoaded("farmersdelight");
    private static final ResourceLocation NAME = new ResourceLocation(TouhouLittleMaid.MOD_ID, "farm");

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }

    @Override
    public ItemStack getIcon() {
        return Items.IRON_HOE.getDefaultInstance();
    }

    @Override
    public boolean isSeed(ItemStack stack) {
        Item item = stack.getItem();
        // 先判断特殊作物
        ISpecialCropHandler handler = SpecialCropManager.getItemSeedHandlers().get(item);
        if (handler != null) {
            return handler.isSeed(stack);
        }
        // 然后是默认情况
        return FARMERS_DELIGHT && FarmersDelightCrops.isSeed(stack) || CropPlanting.isSeed(stack);
    }

    @Override
    public boolean canHarvest(EntityMaid maid, BlockPos cropPos, BlockState cropState) {
        Block block = cropState.getBlock();
        // 先判断特殊情况
        ISpecialCropHandler handler = SpecialCropManager.getBlockCropHandlers().get(block);
        if (handler != null) {
            return handler.canHarvest(maid, cropPos, cropState);
        }
        // 其他情况
        if (FARMERS_DELIGHT && FarmersDelightCrops.isColumn(cropState)) {
            return FarmersDelightCrops.findRipe(maid.level, cropPos) != null;
        }
        return block instanceof CropBlock crop && crop.isMaxAge(cropState);
    }

    @Override
    public void harvest(EntityMaid maid, BlockPos cropPos, BlockState cropState) {
        boolean isDestroyMode = maid.getMainHandItem().getItem() instanceof HoeItem;
        Block cropBlock = cropState.getBlock();

        // 先判断特殊情况
        ISpecialCropHandler handler = SpecialCropManager.getBlockCropHandlers().get(cropBlock);
        if (handler != null) {
            handler.harvest(maid, cropPos, cropState, isDestroyMode);
            return;
        }

        if (FARMERS_DELIGHT && FarmersDelightCrops.isColumn(cropState)) {
            BlockPos ripe = FarmersDelightCrops.findRipe(maid.level, cropPos);
            if (ripe == null || !maid.canDestroyBlock(ripe)) return;
            if (FarmersDelightCrops.harvestTomato((net.minecraft.server.level.ServerLevel) maid.level, ripe, stack -> {
                ItemStack left = ItemHandlerHelper.insertItemStacked(maid.getAvailableInv(false), stack, false);
                if (!left.isEmpty()) Block.popResource(maid.level, ripe, left);
            })) return;
            // A rice panicle grows on a separate submerged block.
            cropPos = ripe;
            cropState = maid.level.getBlockState(ripe);
            cropBlock = cropState.getBlock();
        }

        // 其他情况
        if (isDestroyMode) {
            maid.destroyBlock(cropPos);
        } else if (cropBlock instanceof CropBlockAccessor crop) {
            BlockEntity blockEntity = cropState.hasBlockEntity() ? maid.level.getBlockEntity(cropPos) : null;
            maid.dropResourcesToMaidInv(cropState, maid.level, cropPos, blockEntity, maid, maid.getMainHandItem());
            maid.level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, cropPos, Block.getId(cropState));
            // 直接设置 Age 为 0
            if (cropState.hasProperty(crop.tlmAgeProperty())) {
                try {
                    cropState = cropState.setValue(crop.tlmAgeProperty(), 0);
                } catch (IllegalArgumentException ignore) {
                }
            }
            maid.level.setBlock(cropPos, cropState, Block.UPDATE_ALL);
            maid.level.gameEvent(maid, GameEvent.BLOCK_CHANGE, cropPos);
        }
    }

    @Override
    public boolean canPlant(EntityMaid maid, BlockPos basePos, BlockState baseState, ItemStack seed) {
        // 先判断特殊情况
        ISpecialCropHandler handler = SpecialCropManager.getItemSeedHandlers().get(seed.getItem());
        if (handler != null) {
            return handler.canPlant(maid, basePos, baseState, seed);
        }

        // 其他情况
        if (FARMERS_DELIGHT && FarmersDelightCrops.isSeed(seed)) {
            return FarmersDelightCrops.canPlant(maid.level, basePos, seed);
        }
        return CropPlanting.canPlant(maid.level, basePos, baseState, seed);
    }

    @Override
    public ItemStack plant(EntityMaid maid, BlockPos basePos, BlockState baseState, ItemStack seed) {
        // 先判断特殊作物
        Item item = seed.getItem();
        ISpecialCropHandler handler = SpecialCropManager.getItemSeedHandlers().get(item);
        if (handler != null) {
            return handler.plant(maid, basePos, baseState, seed);
        }
        if (FARMERS_DELIGHT && FarmersDelightCrops.isSeed(seed)) {
            if (FarmersDelightCrops.canPlant(maid.level, basePos, seed)) maid.placeItemBlock(basePos.above(), seed);
            return seed;
        }
        // 然后是默认情况
        if (CropPlanting.isSeed(seed) && CropPlanting.canPlant(maid.level, basePos, baseState, seed)) {
            maid.placeItemBlock(basePos.above(), seed);
        }
        return seed;
    }

    @Override
    public String getMaidActionSummary() {
        return "Plant and harvest crops";
    }
}
