package com.github.tartaricacid.touhoulittlemaid.entity.backpack.data;

import net.minecraft.core.Registry;
import cn.sh1rocu.touhoulittlemaid.util.itemhandler.CombinedInvWrapper;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.SyncFluidAmountMessage;
import com.github.tartaricacid.touhoulittlemaid.util.MaidFluidUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TankBackpackData extends SimpleContainer implements IBackpackData {
    public static final long CAPACITY = 10 * FluidConstants.BUCKET;
    private static final int INPUT_INDEX = 0;
    private static final int OUTPUT_INDEX = 1;
    private final EntityMaid maid;
    private final SingleFluidStorage tank = new SingleFluidStorage() {
        @Override
        protected long getCapacity(FluidVariant variant) {
            return CAPACITY;
        }

        @Override
        protected void markDirty() {
            tankFluidCount = amount;
            if (maid.getOwner() instanceof ServerPlayer player) {
                ServerPlayNetworking.send(player, SyncFluidAmountMessage.ID, SyncFluidAmountMessage.encode(amount));
            }
        }
    };
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return (int) TankBackpackData.this.tankFluidCount;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                TankBackpackData.this.tankFluidCount = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    private long tankFluidCount = 0;

    public TankBackpackData(EntityMaid maid) {
        super(2);
        this.maid = maid;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        if (!this.maid.level.isClientSide) {
            CombinedInvWrapper availableInv = this.maid.getAvailableInv(false);
            if (index == INPUT_INDEX) {
                MaidFluidUtil.bucketToTank(stack, tank, availableInv);
            }
            if (index == OUTPUT_INDEX) {
                MaidFluidUtil.tankToBucket(stack, tank, availableInv);
            }
            this.tankFluidCount = tank.amount;
            ResourceLocation key = Registry.FLUID.getKey(tank.getResource().getFluid());
            maid.setBackpackFluid(key.toString());
        }
        super.setItem(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public void load(CompoundTag tag, EntityMaid maid) {
        this.loadTank(tag.getCompound("Tanks"), maid);
        this.fromTag(tag.getList("Items", Tag.TAG_COMPOUND));
    }

    @Override
    public void save(CompoundTag tag, EntityMaid maid) {
        CompoundTag tankNbt = new CompoundTag();
        saveTank(tankNbt);
        tag.put("Tanks", tankNbt);
        tag.put("Items", this.createTag());
    }

    @Override
    public void serverTick(EntityMaid maid) {
    }

    public SingleFluidStorage getTank() {
        return tank;
    }

    public void saveTank(CompoundTag nbt) {
        nbt.put("variant", tank.fluidVariant.toNbt());
        nbt.putLong("amount", tank.amount);
    }

    public void loadTank(CompoundTag nbt, EntityMaid maid) {
        FluidVariant variant = FluidVariant.fromNbt(nbt.getCompound("variant"));
        tank.amount = variant.isBlank() ? 0 : Math.min(CAPACITY, Math.max(0, nbt.getLong("amount")));
        tank.fluidVariant = tank.amount == 0 ? FluidVariant.blank() : variant;
        this.tankFluidCount = tank.getAmount();
        ResourceLocation key = Registry.FLUID.getKey(tank.getResource().getFluid());
        maid.setBackpackFluid(key.toString());
    }
}
