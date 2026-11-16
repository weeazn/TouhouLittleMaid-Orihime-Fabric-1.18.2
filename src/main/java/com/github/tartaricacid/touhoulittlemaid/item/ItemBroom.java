package com.github.tartaricacid.touhoulittlemaid.item;

import net.minecraft.network.chat.TranslatableComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityBroom;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ItemBroom extends Item {
    public ItemBroom() {
        super((new Properties()).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.DOWN) return InteractionResult.FAIL;

        Level world = context.getLevel();
        BlockPos block = context.getClickedPos();
        var dimensions = EntityBroom.TYPE.getDimensions();
        var player = context.getPlayer();
        Vec3 placement;
        if (face == Direction.UP) {
            placement = new Vec3(block.getX() + 0.5, context.getClickLocation().y, block.getZ() + 0.5);
        } else {
            double offset = 0.5 + dimensions.width / 2.0;
            placement = Vec3.atBottomCenterOf(block).add(face.getStepX() * offset, 0, face.getStepZ() * offset);
        }
        AABB bounds = dimensions.makeBoundingBox(placement);
        if (!world.noCollision(player, bounds) || !world.getEntities(player, bounds,
                entity -> !entity.isSpectator() && (entity instanceof LivingEntity || entity.canBeCollidedWith())).isEmpty()) {
            return InteractionResult.FAIL;
        }
        if (world instanceof ServerLevel serverWorld) {
            ItemStack stack = context.getItemInHand();
            Component customName = stack.hasCustomHoverName() ? stack.getHoverName() : null;
            EntityBroom broom = EntityBroom.TYPE.create(serverWorld, stack.getTag(), customName, player,
                    new BlockPos(placement), MobSpawnType.SPAWN_EGG, false, false);
            if (broom == null) return InteractionResult.FAIL;
            broom.moveTo(placement.x, placement.y, placement.z, player == null ? 0 : player.getYRot(), 0);
            if (player != null) broom.setOwnerUUID(player.getUUID());
            if (!world.addFreshEntity(broom)) return InteractionResult.FAIL;
            if (player == null || !player.getAbilities().instabuild) stack.shrink(1);
            world.playSound(null, placement.x, placement.y, placement.z, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(new TranslatableComponent("tooltips.touhou_little_maid.broom.desc").withStyle(ChatFormatting.GRAY));
    }
}