package com.github.tartaricacid.touhoulittlemaid.entity.backpack.data;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IBackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import com.github.tartaricacid.touhoulittlemaid.compat.furnaces.FurnaceUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import javax.annotation.Nullable;

public class FurnaceBackpackData extends SimpleContainer implements IBackpackData {
    private static final int INPUT_INDEX = 0;
    private static final int FUEL_INDEX = 1;
    private static final int OUTPUT_INDEX = 2;
    public static final int UPGRADE_INDEX = 3;
    private double fuelModifier = 1;
    private double speedModifier = 1;
    private int litTime;
    private int litDuration;
    private int cookingProgress;
    private int cookingTotalTime;
    private final Level level;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> FurnaceBackpackData.this.litTime;
                case 1 -> FurnaceBackpackData.this.litDuration;
                case 2 -> FurnaceBackpackData.this.cookingProgress;
                case 3 -> FurnaceBackpackData.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> FurnaceBackpackData.this.litTime = value;
                case 1 -> FurnaceBackpackData.this.litDuration = value;
                case 2 -> FurnaceBackpackData.this.cookingProgress = value;
                case 3 -> FurnaceBackpackData.this.cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public FurnaceBackpackData(EntityMaid maid) {
        super(4);
        this.level = maid.level;
    }

    @Override
    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    public void load(CompoundTag tag, EntityMaid maid) {
        this.clearContent();
        var items = tag.getList("Items", Tag.TAG_COMPOUND);
        if (!items.isEmpty() && !items.getCompound(0).contains("Slot", Tag.TAG_ANY_NUMERIC)) {
            // Older saves used SimpleContainer's compact list without slot indices.
            int count = items.size();
            if (count < UPGRADE_INDEX) {
                ItemStack last = ItemStack.of(items.getCompound(count - 1));
                if (getBurnDuration(last) == 0 && level.getRecipeManager().getRecipeFor(
                        RecipeType.SMELTING, new SimpleContainer(last), level).isEmpty()
                        && level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).stream()
                        .anyMatch(recipe -> recipe.getResultItem().is(last.getItem()))) {
                    super.setItem(OUTPUT_INDEX, last);
                    count--;
                }
                if (count > 0) {
                    ItemStack fuel = ItemStack.of(items.getCompound(count - 1));
                    if (getBurnDuration(fuel) > 0) {
                        super.setItem(FUEL_INDEX, fuel);
                        count--;
                    }
                }
            }
            for (int i = 0; i < Math.min(count, UPGRADE_INDEX); i++) {
                super.setItem(i, ItemStack.of(items.getCompound(i)));
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                CompoundTag entry = items.getCompound(i);
                int slot = entry.getByte("Slot") & 255;
                if (slot < getContainerSize()) super.setItem(slot, ItemStack.of(entry));
            }
        }
        this.fuelModifier = tag.contains("FuelModifier") ? tag.getDouble("FuelModifier") : 1;
        this.speedModifier = tag.contains("SpeedModifier") ? tag.getDouble("SpeedModifier") : 1;
        this.litTime = tag.getInt("BurnTime");
        this.cookingProgress = tag.getInt("CookTime");
        this.cookingTotalTime = tag.getInt("CookTimeTotal");
        this.litDuration = tag.contains("BurnDuration") ? tag.getInt("BurnDuration")
                : Math.max(litTime, getBurnDuration(getItem(FUEL_INDEX)));
    }

    @Override
    public void save(CompoundTag tag, EntityMaid maid) {
        tag.putDouble("FuelModifier", fuelModifier);
        tag.putDouble("SpeedModifier", speedModifier);
        tag.putInt("BurnTime", this.litTime);
        tag.putInt("CookTime", this.cookingProgress);
        tag.putInt("CookTimeTotal", this.cookingTotalTime);
        tag.putInt("BurnDuration", litDuration);
        var items = new net.minecraft.nbt.ListTag();
        for (int slot = 0; slot < getContainerSize(); slot++) {
            if (getItem(slot).isEmpty()) continue;
            CompoundTag entry = getItem(slot).save(new CompoundTag());
            entry.putByte("Slot", (byte) slot);
            items.add(entry);
        }
        tag.put("Items", items);
    }

    @Override
    public void serverTick(EntityMaid maid) {
        Level level = maid.getLevel();
        FurnaceUpgrade upgrade = FurnaceUpgrade.of(getItem(UPGRADE_INDEX));
        double newSpeed = upgrade == null ? 1 : upgrade.getSpeedModifier();
        double newFuel = upgrade == null ? 1 : upgrade.getFuelModifier();
        if (newFuel > 0 && newFuel != fuelModifier) {
            litTime = (int) (litTime * fuelModifier / newFuel);
            litDuration = (int) (litDuration * fuelModifier / newFuel);
            fuelModifier = newFuel;
        }
        if (newSpeed > 0 && newSpeed != speedModifier) {
            speedModifier = newSpeed;
            cookingTotalTime = getTotalCookTime(level);
            cookingProgress = 0;
        }
        // 如果是燃烧状态，继续燃烧
        if (this.isLit()) {
            --this.litTime;
        }
        ItemStack fuelItem = this.getItem(FUEL_INDEX);
        boolean inputNotEmpty = !this.getItem(INPUT_INDEX).isEmpty();
        boolean fuelNotEmpty = !fuelItem.isEmpty();
        boolean readyForLit = inputNotEmpty && fuelNotEmpty;
        // 要么正在燃烧，要么具备燃烧条件
        if (this.isLit() || readyForLit) {
            // 从缓存中获取配方
            SmeltingRecipe recipe = null;
            if (inputNotEmpty) {
                recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this, level).orElse(null);
            }

            int maxStackSize = this.getMaxStackSize();
            // 没有燃烧，但是可以燃！
            if (!this.isLit() && this.canBurn(recipe, maxStackSize)) {
                this.litTime = this.getBurnDuration(fuelItem);
                this.litDuration = this.litTime;
                // 如果此时点燃了
                if (this.isLit()) {
                    // 如果燃料有残留物，比如熔岩桶燃烧后残留一个桶
                    if (fuelItem.getItem().hasCraftingRemainingItem()) {
                        this.setItem(FUEL_INDEX, new ItemStack(fuelItem.getItem().getCraftingRemainingItem()));
                    } else if (fuelNotEmpty) {
                        // 普通燃料减一
                        fuelItem.shrink(1);
                        if (fuelItem.isEmpty()) {
                            this.setItem(FUEL_INDEX, new ItemStack(fuelItem.getItem().getCraftingRemainingItem()));
                        }
                    }
                }
            }

            // 点燃了，而且也能燃！
            if (this.isLit() && this.canBurn(recipe, maxStackSize)) {
                // 各种进度增加
                ++this.cookingProgress;
                // 如果进度满了，重置，并给出产物
                if (this.cookingProgress >= this.cookingTotalTime) {
                    this.cookingProgress = 0;
                    this.cookingTotalTime = getTotalCookTime(level);
                    // 如果烧制成功，把经验给女仆
                    if (this.burn(recipe, maxStackSize, upgrade)) {
                        int exp = this.createExperience(recipe.getExperience());
                        maid.setExperience(maid.getExperience() + exp);
                    }
                }
            } else {
                // 否则直接重置烧制进度
                this.cookingProgress = 0;
            }
        } else if (this.cookingProgress > 0) {
            // 什么，燃料不足，那就逐 tick 递减烧制进度
            this.cookingProgress = Mth.clamp(this.cookingProgress - 2, 0, this.cookingTotalTime);
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack slotItem = this.getItem(index);
        boolean isSameItem = !stack.isEmpty() && ItemStack.isSameItemSameTags(slotItem, stack);
        super.setItem(index, stack);
        if (index == 0 && !isSameItem) {
            this.cookingTotalTime = getTotalCookTime(this.level);
            this.cookingProgress = 0;
        }
    }

    private int createExperience(float recipeExp) {
        int integer = Mth.floor(recipeExp);
        float decimal = Mth.frac(recipeExp);
        if (decimal != 0 && Math.random() < (double) decimal) {
            ++integer;
        }
        return integer;
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    private int getBurnDuration(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            //return fuel.getBurnTime(RecipeType.SMELTING);
            Integer burnTime = FuelRegistry.INSTANCE.get(fuel.getItem());
            return burnTime == null ? 0 : (int) (burnTime / fuelModifier);
        }
    }

    private boolean canBurn(@Nullable SmeltingRecipe recipe, int maxStackSize) {
        if (recipe == null || getItem(INPUT_INDEX).isEmpty()) return false;
        ItemStack result = recipe.assemble(this);
        ItemStack output = getItem(OUTPUT_INDEX);
        return !result.isEmpty() && (output.isEmpty() || ItemStack.isSameItemSameTags(output, result))
                && output.getCount() + result.getCount() <= Math.min(maxStackSize, result.getMaxStackSize());
    }

    private boolean burn(@Nullable SmeltingRecipe recipe, int maxStackSize, @Nullable FurnaceUpgrade upgrade) {
        if (this.canBurn(recipe, maxStackSize)) {
            ItemStack input = getItem(INPUT_INDEX);
            ItemStack result = recipe.assemble(this).copy();
            ItemStack output = getItem(OUTPUT_INDEX);
            if (upgrade != null && output.getCount() + result.getCount() < Math.min(maxStackSize, result.getMaxStackSize())
                    && level.random.nextInt(100) < upgrade.getDuplicationChance()) result.grow(1);
            if (output.isEmpty()) setItem(OUTPUT_INDEX, result);
            else output.grow(result.getCount());
            // 如果是海绵和桶
            if (input.is(Blocks.WET_SPONGE.asItem()) && !getItem(FUEL_INDEX).isEmpty() && getItem(FUEL_INDEX).is(Items.BUCKET)) {
                setItem(FUEL_INDEX, new ItemStack(Items.WATER_BUCKET));
            }
            input.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    private int getTotalCookTime(Level level) {
        int base = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this, level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
        return Math.max(1, (int) (base / speedModifier));
    }
}
