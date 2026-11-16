package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import net.minecraft.core.Registry;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class TagItem extends FabricTagProvider<Item> {
    /**
     * 能够驯服女仆的物品
     * <p>
     * 默认已经把所有带有 <code>#forge:cakes</code> 和 <code>#c:cakes</code> 标签的物品加入其中了
     */
    public static final TagKey<Item> MAID_TAMED_ITEM = createTagKey("maid_tamed_item");

    /**
     * 物品拥有经验修补后，女仆在吸收经验或者 P 点时能够进行修复；
     * <p>
     * 但是部分物品不能这么做，可将其加入此 tag 下
     */
    public static final TagKey<Item> MAID_MENDING_BLOCKLIST_ITEM = createTagKey("maid_mending_blocklist_item");

    /**
     * 女仆和玩家类似，在穿戴拥有消失诅咒附魔的装备（或者饰品）后死亡，其对应的物品会直接消失；
     * <p>
     * 但是部分物品不能这么做，可将其加入此 tag 下
     */
    public static final TagKey<Item> MAID_VANISHING_BLOCKLIST_ITEM = createTagKey("maid_vanishing_blocklist_item");

    /**
     * 女仆进食黑名单，与配置文件协同作用，方便拓展兼容
     * <p>
     * 全局的，适用于工作餐、回血餐和家庭餐
     */
    public static final TagKey<Item> MAID_EAT_BLOCKLIST_ITEM = createTagKey("maid_eat_blocklist_item");

    public TagItem(FabricDataGenerator output) {
        super(output, Registry.ITEM, "Item Tags");
    }

    public static TagKey<Item> createTagKey(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(TouhouLittleMaid.MOD_ID, name));
    }

    public static TagKey<Item> createTagKey(ResourceLocation resourceLocation) {
        return TagKey.create(Registry.ITEM_REGISTRY, resourceLocation);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(MAID_TAMED_ITEM)
                .add(Items.CAKE)
                .addOptionalTag(new ResourceLocation("forge:cakes"))
                .addOptionalTag(new ResourceLocation("c:cakes"))
                .addOptionalTag(new ResourceLocation("jmc:cakes"))
                .addOptional(new ResourceLocation("kawaiidishes:cheese_cake"))
                .addOptional(new ResourceLocation("kawaiidishes:honey_cheese_cake"))
                .addOptional(new ResourceLocation("kawaiidishes:chocolate_cheese_cake"))
                .addOptional(new ResourceLocation("kawaiidishes:piece_of_cake"))
                .addOptional(new ResourceLocation("kawaiidishes:piece_of_cheesecake"))
                .addOptional(new ResourceLocation("kawaiidishes:piece_of_chocolate_cheesecake"))
                .addOptional(new ResourceLocation("kawaiidishes:piece_of_honey_cheesecake"));

        getOrCreateTagBuilder(MAID_MENDING_BLOCKLIST_ITEM).add(InitItems.ULTRAMARINE_ORB_ELIXIR);
        getOrCreateTagBuilder(MAID_VANISHING_BLOCKLIST_ITEM).add(InitItems.ULTRAMARINE_ORB_ELIXIR);

        // 森罗物语辣椒
        getOrCreateTagBuilder(MAID_EAT_BLOCKLIST_ITEM)
                .addOptional(new ResourceLocation("heartytrinkets:red_heart"))
                .addOptional(new ResourceLocation("heartytrinkets:orange_heart"))
                .addOptional(new ResourceLocation("heartytrinkets:green_heart"))
                .addOptional(new ResourceLocation("heartytrinkets:blue_heart"))
                .addOptional(new ResourceLocation("kaleidoscope_cookery:red_chili"))
                .addOptional(new ResourceLocation("kaleidoscope_cookery:green_chili"));
    }
}
