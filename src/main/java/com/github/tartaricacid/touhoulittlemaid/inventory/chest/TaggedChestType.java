package com.github.tartaricacid.touhoulittlemaid.inventory.chest;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

final class TaggedChestType implements IChestType {
    @Override
    public boolean isChest(BlockEntity chest) {
        return chest != null && chest.getBlockState().is(BackpackDeposit.DEPOSIT_CONTAINERS);
    }

    @Override
    public boolean canOpenByPlayer(BlockEntity chest, Player player) {
        return player.level.isClientSide || storage(chest) != null;
    }

    @Override
    public int getOpenCount(BlockGetter level, BlockPos pos, BlockEntity chest) {
        // destination() checks for locks, open lids and unclaimed loot on each transfer.
        return ALLOW_COUNT;
    }

    @Override
    public Storage<ItemVariant> storage(BlockEntity chest) {
        return chest.getLevel() instanceof ServerLevel world ? BackpackDeposit.destination(world, chest.getBlockPos()) : null;
    }
}
