package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.block.BlockGarageKit;
import com.github.tartaricacid.touhoulittlemaid.item.ItemChair;
import com.github.tartaricacid.touhoulittlemaid.item.ItemEntityPlaceholder;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import vazkii.patchouli.common.item.ItemModBook;
import static com.github.tartaricacid.touhoulittlemaid.init.InitItems.*;

public class InitCreativeTabs {
    public static void init() {

    }

    public static CreativeModeTab MAIN_TAB = FabricItemGroupBuilder.create(new ResourceLocation(TouhouLittleMaid.MOD_ID, "main"))
            .icon(() -> InitItems.HAKUREI_GOHEI.getDefaultInstance())
            .appendItems(output -> {
                if (FabricLoader.getInstance().isModLoaded("patchouli")) {
                    output.add(ItemModBook.forBook(MEMORIZABLE_GENSOKYO_LOCATION));
                }
                output.add(new ItemStack(MAID_SPAWN_EGG));
                output.add(new ItemStack(FAIRY_SPAWN_EGG));
                output.add(new ItemStack(HAKUREI_GOHEI));
                output.add(new ItemStack(SANAE_GOHEI));
                output.add(new ItemStack(POWER_POINT));
                output.add(new ItemStack(SMART_SLAB_EMPTY));
                output.add(new ItemStack(SMART_SLAB_INIT));
                output.add(new ItemStack(MAID_BACKPACK_SMALL));
                output.add(new ItemStack(MAID_BACKPACK_MIDDLE));
                output.add(new ItemStack(MAID_BACKPACK_BIG));
                output.add(new ItemStack(CRAFTING_TABLE_BACKPACK));
                output.add(new ItemStack(ENDER_CHEST_BACKPACK));
                output.add(new ItemStack(FURNACE_BACKPACK));
                output.add(new ItemStack(TANK_BACKPACK));
                output.add(new ItemStack(SUBSTITUTE_JIZO));
                output.add(new ItemStack(ULTRAMARINE_ORB_ELIXIR));
                output.add(new ItemStack(EXPLOSION_PROTECT_BAUBLE));
                output.add(new ItemStack(FIRE_PROTECT_BAUBLE));
                output.add(new ItemStack(PROJECTILE_PROTECT_BAUBLE));
                output.add(new ItemStack(MAGIC_PROTECT_BAUBLE));
                output.add(new ItemStack(FALL_PROTECT_BAUBLE));
                output.add(new ItemStack(DROWN_PROTECT_BAUBLE));
                output.add(new ItemStack(NIMBLE_FABRIC));
                output.add(new ItemStack(ITEM_MAGNET_BAUBLE));
                output.add(new ItemStack(MUTE_BAUBLE));
                output.add(new ItemStack(WIRELESS_IO));
                output.add(new ItemStack(TRUMPET));
                output.add(new ItemStack(RED_FOX_SCROLL));
                output.add(new ItemStack(WHITE_FOX_SCROLL));
                output.add(new ItemStack(SERVANT_BELL));
                output.add(new ItemStack(KAPPA_COMPASS));
                output.add(new ItemStack(EXTINGUISHER));
                output.add(new ItemStack(GOMOKU));
                output.add(new ItemStack(CCHESS));
                output.add(new ItemStack(WCHESS));
                output.add(new ItemStack(GOMOKU_BOARD_STATE));
                output.add(new ItemStack(CCHESS_BOARD_STATE));
                output.add(new ItemStack(WCHESS_BOARD_STATE));
                output.add(new ItemStack(KEYBOARD));
                output.add(new ItemStack(BOOKSHELF));
                output.add(new ItemStack(COMPUTER));
                output.add(new ItemStack(FAVORABILITY_TOOL_ADD));
                output.add(new ItemStack(FAVORABILITY_TOOL_REDUCE));
                output.add(new ItemStack(FAVORABILITY_TOOL_FULL));
                output.add(new ItemStack(CAMERA));
                output.add(new ItemStack(PHOTO));
                output.add(new ItemStack(FILM));
                output.add(new ItemStack(CHISEL));
                output.add(new ItemStack(MAID_BED));
                output.add(new ItemStack(PICNIC_BASKET));
                output.add(new ItemStack(MAID_BEACON));
                output.add(new ItemStack(SNACK_CABINET));
                output.add(new ItemStack(SHRINE));
                output.add(new ItemStack(MODEL_SWITCHER));
                output.add(new ItemStack(CHAIR_SHOW));
                output.add(new ItemStack(BROOM));
                output.add(new ItemStack(SCARECROW));
                output.add(new ItemStack(ENTITY_ID_COPY));
                output.add(new ItemStack(OWNER_CONVERSION_TOOL));
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                    ItemEntityPlaceholder.fillItemCategory(output);
                }
                output.add(getEnchantmentBook(InitEnchantments.IMPEDING));
                output.add(getEnchantmentBook(InitEnchantments.SPEEDY));
                output.add(getEnchantmentBook(InitEnchantments.ENDERS_ENDER));
            }).build();

    public static CreativeModeTab GARAGE_KIT_TAB = FabricItemGroupBuilder.create(new ResourceLocation(TouhouLittleMaid.MOD_ID, "chair"))
            .icon(() -> InitItems.CHAIR.getDefaultInstance())
            .appendItems(output -> {
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                    ItemChair.fillItemCategory(output);
                }
            }).build();

    public static CreativeModeTab CHAIR_TAB = FabricItemGroupBuilder.create(new ResourceLocation(TouhouLittleMaid.MOD_ID, "garage_kit"))
            .icon(() -> InitItems.GARAGE_KIT.getDefaultInstance())
            .appendItems(output -> {
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                    BlockGarageKit.fillItemCategory(output);
                }
            }).build();

    private static ItemStack getEnchantmentBook(Enchantment enchantment) {
        EnchantmentInstance instance = new EnchantmentInstance(enchantment, enchantment.getMaxLevel());
        return EnchantedBookItem.createForEnchantment(instance);
    }


}
