package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class ItemKappaCompass extends Item {
    public ItemKappaCompass() {
        super((new Item.Properties()).stacksTo(1));
    }

    public static void addPoint(Activity activity, BlockPos pos, ItemStack compass) {
        CompoundTag tag = compass.getOrCreateTagElement("KappaCompassData");
        tag.put(activity.getName(), NbtUtils.writeBlockPos(pos));
    }

    public static void addDimension(ResourceLocation dimension, ItemStack compass) {
        CompoundTag tag = compass.getOrCreateTagElement("KappaCompassData");
        tag.putString("Dimension", dimension.toString());
    }

    @Nullable
    public static BlockPos getPoint(Activity activity, ItemStack compass) {
        CompoundTag tag = compass.getTagElement("KappaCompassData");
        if (tag != null) {
            String name = activity.getName();
            if (tag.contains(name, Tag.TAG_COMPOUND)) {
                return NbtUtils.readBlockPos(tag.getCompound(name));
            }
            name = Activity.IDLE.getName();
            if (tag.contains(name, Tag.TAG_COMPOUND)) {
                return NbtUtils.readBlockPos(tag.getCompound(name));
            }
            name = Activity.WORK.getName();
            if (tag.contains(name, Tag.TAG_COMPOUND)) {
                return NbtUtils.readBlockPos(tag.getCompound(name));
            }
        }
        return null;
    }

    @Nullable
    public static ResourceLocation getDimension(ItemStack compass) {
        CompoundTag tag = compass.getTagElement("KappaCompassData");
        if (tag != null) {
            return new ResourceLocation(tag.getString("Dimension"));
        }
        return null;
    }

    public static int getRecordCount(ItemStack compass) {
        CompoundTag tag = compass.getTagElement("KappaCompassData");
        int count = 0;
        if (tag != null) {
            if (tag.contains(Activity.WORK.getName(), Tag.TAG_COMPOUND)) {
                count++;
            }
            if (tag.contains(Activity.IDLE.getName(), Tag.TAG_COMPOUND)) {
                count++;
            }
            if (tag.contains(Activity.REST.getName(), Tag.TAG_COMPOUND)) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasKappaCompassData(ItemStack compass) {
        return compass.getTagElement("KappaCompassData") != null;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack compass, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (livingEntity instanceof EntityMaid maid && !maid.level.isClientSide) {
            if (player.isDiscrete()) {
                maid.getSchedulePos().clear(maid);
                player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.maid_clear"), net.minecraft.Util.NIL_UUID);
                player.level.playSound(null, player.blockPosition(), InitSounds.COMPASS_POINT, SoundSource.PLAYERS, 0.8f, 1.5f);
                return InteractionResult.SUCCESS;
            }
            CompoundTag tag = compass.getTagElement("KappaCompassData");
            ResourceLocation dimension = getDimension(compass);
            if (tag != null || dimension != null) {
                if (!maid.level.dimension().location().equals(dimension)) {
                    player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.maid_dimension_check"), net.minecraft.Util.NIL_UUID);
                    return InteractionResult.CONSUME;
                }
                maid.getSchedulePos().setDimension(dimension);
                BlockPos point = getPoint(Activity.WORK, compass);
                if (point != null) {
                    maid.getSchedulePos().setWorkPos(point);
                }
                point = getPoint(Activity.IDLE, compass);
                if (point != null) {
                    maid.getSchedulePos().setIdlePos(point);
                }
                point = getPoint(Activity.REST, compass);
                if (point != null) {
                    maid.getSchedulePos().setSleepPos(point);
                }
                maid.getSchedulePos().setConfigured(true);
                maid.getSchedulePos().restrictTo(maid);
                player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.maid_write"), net.minecraft.Util.NIL_UUID);
                player.level.playSound(null, player.blockPosition(), InitSounds.COMPASS_POINT, SoundSource.PLAYERS, 0.8f, 1.5f);
                return InteractionResult.SUCCESS;
            }
            player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.no_data"), net.minecraft.Util.NIL_UUID);
            return InteractionResult.CONSUME;
        }
        return super.interactLivingEntity(compass, player, livingEntity, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack compass = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        if (player == null || context.getLevel().isClientSide) {
            return super.useOn(context);
        }
        if (player.isDiscrete()) {
            compass.removeTagKey("KappaCompassData");
            player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.clear"), net.minecraft.Util.NIL_UUID);
        } else {
            ResourceLocation dimension = getDimension(compass);
            int recordCount = getRecordCount(compass);
            if (recordCount >= 3) {
                player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.full"), net.minecraft.Util.NIL_UUID);
            } else if (recordCount == 2) {
                BlockPos idlePos = getPoint(Activity.IDLE, compass);
                if (idlePos != null && idlePos.distSqr(clickedPos) > 64 * 64) {
                    player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.far_away"), net.minecraft.Util.NIL_UUID);
                    return super.useOn(context);
                }
                if (dimension != null && !player.level.dimension().location().equals(dimension)) {
                    player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.diff_dimension"), net.minecraft.Util.NIL_UUID);
                    return super.useOn(context);
                }
                addPoint(Activity.REST, clickedPos, compass);
                player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.sleep", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), net.minecraft.Util.NIL_UUID);
            } else if (recordCount == 1) {
                BlockPos workPos = getPoint(Activity.WORK, compass);
                if (workPos != null && workPos.distSqr(clickedPos) > 64 * 64) {
                    player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.far_away"), net.minecraft.Util.NIL_UUID);
                    return super.useOn(context);
                }
                if (dimension != null && !player.level.dimension().location().equals(dimension)) {
                    player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.diff_dimension"), net.minecraft.Util.NIL_UUID);
                    return super.useOn(context);
                }
                addPoint(Activity.IDLE, clickedPos, compass);
                player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.idle", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), net.minecraft.Util.NIL_UUID);
            } else {
                addPoint(Activity.WORK, clickedPos, compass);
                player.sendMessage(new TranslatableComponent("message.touhou_little_maid.kappa_compass.work", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), net.minecraft.Util.NIL_UUID);
            }
            addDimension(player.level.dimension().location(), compass);
        }
        player.level.playSound(null, player.blockPosition(), InitSounds.COMPASS_POINT, SoundSource.PLAYERS, 0.8f, 1.5f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> components, TooltipFlag pIsAdvanced) {
        if (hasKappaCompassData(stack)) {
            ResourceLocation dimension = getDimension(stack);
            BlockPos workPos = getPoint(Activity.WORK, stack);
            BlockPos idlePos = getPoint(Activity.IDLE, stack);
            BlockPos sleepPos = getPoint(Activity.REST, stack);
            if (dimension != null) {
                components.add(new TranslatableComponent("tooltips.touhou_little_maid.fox_scroll.dimension", dimension.toString()).withStyle(ChatFormatting.GOLD));
            }
            if (workPos != null) {
                components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.work", workPos.getX(), workPos.getY(), workPos.getZ()).withStyle(ChatFormatting.RED));
            }
            if (idlePos != null) {
                components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.idle", idlePos.getX(), idlePos.getY(), idlePos.getZ()).withStyle(ChatFormatting.GREEN));
            }
            if (sleepPos != null) {
                components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.sleep", sleepPos.getX(), sleepPos.getY(), sleepPos.getZ()).withStyle(ChatFormatting.BLUE));
            }
            components.add(new TextComponent(""));
        }
        components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.usage.set_pos"));
        components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.usage.clear_pos"));
        components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.usage.write_pos_to_maid"));
        components.add(new TranslatableComponent("message.touhou_little_maid.kappa_compass.usage.clear_maid_pos"));
    }
}