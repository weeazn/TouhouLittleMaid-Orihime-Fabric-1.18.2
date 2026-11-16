package com.github.tartaricacid.touhoulittlemaid.inventory.chest;

import cn.sh1rocu.touhoulittlemaid.util.itemhandler.IItemHandler;
import cn.sh1rocu.touhoulittlemaid.util.itemhandler.IItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class BackpackDeposit {
    static final TagKey<Block> DEPOSIT_CONTAINERS = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("touhou_little_maid", "maid_deposit_containers"));

    private BackpackDeposit() {}

    public static boolean full(IItemHandler cargo) {
        for (int slot = 0; slot < cargo.getSlots(); slot++) {
            if (cargo.getStackInSlot(slot).isEmpty()) return false;
        }
        return cargo.getSlots() > 0;
    }

    public static Container chest(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) return null;
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock block) || !available(level, pos)) return null;
        if (state.getValue(ChestBlock.TYPE) != ChestType.SINGLE
                && !available(level, pos.relative(ChestBlock.getConnectedDirection(state)))) return null;
        return ChestBlock.getContainer(block, state, level, pos, false);
    }

    private static boolean available(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos) || !(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)
                || ChestBlockEntity.getOpenCount(level, pos) > 0) return false;
        var tag = chest.saveWithoutMetadata();
        return tag.getString("Lock").isEmpty() && !tag.contains("LootTable");
    }

    public static Storage<ItemVariant> destination(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) return null;
        var state = level.getBlockState(pos);
        if (Registry.BLOCK.getKey(state.getBlock()).getNamespace().equals("lootr")) return null;
        if (state.getBlock() instanceof ChestBlock && level.getBlockEntity(pos) instanceof ChestBlockEntity) {
            Container chest = chest(level, pos);
            return chest == null ? null : InventoryStorage.of(chest, null);
        }
        if (!state.is(DEPOSIT_CONTAINERS)) return null;
        var entity = level.getBlockEntity(pos);
        if (entity == null || state.hasProperty(BlockStateProperties.OPEN) && state.getValue(BlockStateProperties.OPEN)) return null;
        var data = entity.saveWithoutMetadata();
        if (!data.getString("Lock").isEmpty() || data.contains("LootTable") || data.contains("HTM_Lock")) return null;
        return ItemStorage.SIDED.find(level, pos, Direction.UP);
    }

    public static List<BlockPos> nearby(ServerLevel level, BlockPos center, int radius) {
        List<BlockPos> chests = new ArrayList<>();
        for (int x = (center.getX()-radius) >> 4; x <= (center.getX()+radius) >> 4; x++) {
            for (int z = (center.getZ()-radius) >> 4; z <= (center.getZ()+radius) >> 4; z++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
                if (chunk == null) continue;
                chunk.getBlockEntities().forEach((pos, entity) -> {
                    if (pos.distSqr(center) <= radius * radius && (entity instanceof ChestBlockEntity || entity.getBlockState().is(DEPOSIT_CONTAINERS))) chests.add(pos);
                });
            }
        }
        chests.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
        return chests;
    }

    public static BlockPos nearest(ServerLevel level, BlockPos center, int radius, IItemHandlerModifiable cargo,
                                   Predicate<BlockPos> reachable) {
        for (BlockPos pos : nearby(level, center, radius)) {
            var chest = destination(level, pos);
            if (chest != null && transfer(cargo, chest, false) > 0 && reachable.test(pos)) return pos;
        }
        return null;
    }

    public static long transfer(IItemHandlerModifiable cargo, Container chest, boolean commit) {
        return transfer(cargo, InventoryStorage.of(chest, null), commit);
    }

    public static long transfer(IItemHandlerModifiable cargo, Storage<ItemVariant> destination, boolean commit) {
        long moved = 0;
        try (Transaction transaction = Transaction.openOuter()) {
            for (int i = 0; i < cargo.getSlots(); i++) {
                if (cargo.getStackInSlot(i).isEmpty()) continue;
                final int slot = i;
                var source = new SingleStackStorage() {
                    protected ItemStack getStack() { return cargo.getStackInSlot(slot); }
                    protected void setStack(ItemStack stack) { cargo.setStackInSlot(slot, stack); }
                };
                moved += StorageUtil.move(source, destination, variant -> true, commit ? Long.MAX_VALUE : 1, transaction);
                if (!commit && moved > 0) break;
            }
            if (commit && moved > 0) transaction.commit();
        }
        return moved;
    }
}
