package com.github.tartaricacid.touhoulittlemaid.compat.rei.transfer;

import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.CraftingTableBackpackContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.backpack.FurnaceBackpackContainer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleMenuInfoProvider;
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.world.inventory.AbstractContainerMenu;
import java.util.stream.IntStream;

public class BackpackTransferHandler implements REIServerPlugin {
    @Override
    public void registerMenuInfo(MenuInfoRegistry registry) {
        register(registry, BuiltinPlugin.CRAFTING, CraftingTableBackpackContainer.class, 62, 9);
        register(registry, BuiltinPlugin.SMELTING, FurnaceBackpackContainer.class, 61, 1);
        register(registry, BuiltinPlugin.FUEL, FurnaceBackpackContainer.class, 62, 1);
    }

    private static <M extends AbstractContainerMenu, D extends Display> void register(MenuInfoRegistry registry,
            CategoryIdentifier<D> category, Class<M> container, int start, int count) {
        registry.register(category, container, SimpleMenuInfoProvider.of(display -> new SimplePlayerInventoryMenuInfo<M, D>() {
            @Override
            public D getDisplay() { return display; }

            @Override
            public Iterable<SlotAccessor> getInventorySlots(MenuInfoContext<M, ?, D> context) {
                return slots(context.getMenu(), 0, 61);
            }

            @Override
            public Iterable<SlotAccessor> getInputSlots(MenuInfoContext<M, ?, D> context) {
                return slots(context.getMenu(), start, count);
            }
        }));
    }

    private static Iterable<SlotAccessor> slots(AbstractContainerMenu menu, int start, int count) {
        return IntStream.range(start, start + count).mapToObj(i -> SlotAccessor.fromSlot(menu.getSlot(i))).toList();
    }
}
