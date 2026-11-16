package com.github.tartaricacid.touhoulittlemaid.block;

import net.minecraft.network.chat.TranslatableComponent;
import cn.sh1rocu.touhoulittlemaid.util.itemhandler.ItemHandlerHelper;
import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.item.ItemFilm;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityShrine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockShrine extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 5, 16),
            Block.box(2, 5, 2, 14, 10, 14),
            Block.box(4, 10, 4, 12, 16, 12));

    public BlockShrine() {
        super(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.0F, 3.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !(worldIn.getBlockEntity(pos) instanceof TileEntityShrine shrine)) {
            return InteractionResult.PASS;
        }
        if (playerIn.isShiftKeyDown()) {
            if (shrine.isEmpty()) return InteractionResult.PASS;
            if (!worldIn.isClientSide) {
                ItemHandlerHelper.giveItemToPlayer(playerIn, shrine.extractStorageItem());
                worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1, 1);
            }
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        }
        ItemStack held = playerIn.getMainHandItem();
        if (shrine.isEmpty()) {
            if (!shrine.canInsert(held)) {
                if (!worldIn.isClientSide) {
                    playerIn.displayClientMessage(new TranslatableComponent("message.touhou_little_maid.shrine.not_film"), false);
                }
                return InteractionResult.PASS;
            }
            if (!worldIn.isClientSide) {
                shrine.insertStorageItem(ItemHandlerHelper.copyStackWithSize(held, 1));
                held.shrink(1);
                worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1, 1);
            }
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        }
        if (!held.isEmpty()) return InteractionResult.PASS;
        if (worldIn instanceof ServerLevel server && playerIn instanceof ServerPlayer player) {
            if (!player.isCreative() && player.getHealth() < player.getMaxHealth() / 2 + 1) {
                player.displayClientMessage(new TranslatableComponent("message.touhou_little_maid.shrine.health_low"), false);
                return InteractionResult.FAIL;
            }
            if (!ItemFilm.reviveAtShrine(shrine.getStorageItem(), server, pos.above(), player)) {
                return InteractionResult.FAIL;
            }
            shrine.extractStorageItem();
            if (!player.isCreative()) player.setHealth(0.25f);
            InitTrigger.MAID_EVENT.trigger(player, TriggerType.SHRINE_REBORN_MAID);
        }
        return InteractionResult.sidedSuccess(worldIn.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !isMoving) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof TileEntityShrine shrine) {
                ItemStack storageItem = shrine.extractStorageItem();
                if (!storageItem.isEmpty()) {
                    Block.popResource(worldIn, pos.offset(0, 1, 0), storageItem);
                }
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new TileEntityShrine(pos, blockState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
}
