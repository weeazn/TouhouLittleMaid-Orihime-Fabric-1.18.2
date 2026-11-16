package com.github.tartaricacid.touhoulittlemaid.client.init;

import net.minecraft.core.Registry;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.PerspectiveBakedModel;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.ReplaceableBakedModel;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.VanillaConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class InitSpecialItemRender {
    private static final List<Pair<ModelResourceLocation, ResourceLocation>> PERSPECTIVE_MODEL_LIST = Lists.newArrayList();
    private static final List<Triple<ModelResourceLocation, ResourceLocation, Supplier<Boolean>>> REPLACEABLE_MODEL_LIST = Lists.newArrayList();

    private static final ResourceLocation LIFE_POINT = new ResourceLocation(TouhouLittleMaid.MOD_ID, "life_point");
    private static final ResourceLocation POINT_ITEM = new ResourceLocation(TouhouLittleMaid.MOD_ID, "point_item");

    // 祭坛合成占位符的物品模型
    private static final ResourceLocation SPAWN_BOX = new ResourceLocation(TouhouLittleMaid.MOD_ID, "item/spawn_box");
    private static final ResourceLocation REBORN_MAID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "item/reborn_maid");
    private static final ResourceLocation SPAWN_LIGHTNING_BOLT = new ResourceLocation(TouhouLittleMaid.MOD_ID, "item/spawn_lightning_bolt");

    public static void init() {
        register();
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, add) -> registerModels(add));
    }

    public static void wrapBakedModels(Map<ResourceLocation, BakedModel> models) {
        for (var pair : PERSPECTIVE_MODEL_LIST) {
            BakedModel alternate = models.get(pair.getRight());
            if (alternate != null) {
                models.computeIfPresent(pair.getLeft(), (id, model) -> new PerspectiveBakedModel(model, alternate));
            }
        }
        for (var entry : REPLACEABLE_MODEL_LIST) {
            BakedModel alternate = models.get(entry.getMiddle());
            if (alternate != null) {
                models.computeIfPresent(entry.getLeft(), (id, model) -> new ReplaceableBakedModel(model, alternate, entry.getRight()));
            }
        }
    }

    public static void register() {
        addInHandModel(InitItems.HAKUREI_GOHEI);
        addInHandModel(InitItems.SANAE_GOHEI);
        addInHandModel(InitItems.EXTINGUISHER);
        addInHandModel(InitItems.CAMERA);
        addInHandModel(InitItems.MAID_BEACON);
        addInHandModel(InitItems.SNACK_CABINET);

        addReplaceableModel(Items.TOTEM_OF_UNDYING, LIFE_POINT, () -> VanillaConfig.REPLACE_TOTEM_TEXTURE.get());
        addReplaceableModel(Items.EXPERIENCE_BOTTLE, POINT_ITEM, () -> VanillaConfig.REPLACE_XP_BOTTLE_TEXTURE.get());
    }

    public static void registerModels(Consumer<ResourceLocation> add) {
        PERSPECTIVE_MODEL_LIST.forEach(pair -> add.accept(pair.getRight()));
        REPLACEABLE_MODEL_LIST.forEach(entry -> add.accept(entry.getMiddle()));

        // 特殊需要额外注册的模型
        add.accept(SPAWN_BOX);
        add.accept(REBORN_MAID);
        add.accept(SPAWN_LIGHTNING_BOLT);
    }

    public static void addInHandModel(Item item) {
        ResourceLocation res = Registry.ITEM.getKey(item);
        if (!res.equals(Registry.ITEM.getDefaultKey())) {
            ModelResourceLocation rawName = new ModelResourceLocation(res, "inventory");
            ResourceLocation inHandName = new ResourceLocation(res.getNamespace(), "item/" + res.getPath() + "_in_hand");
            PERSPECTIVE_MODEL_LIST.add(Pair.of(rawName, inHandName));
        }
    }

    public static void addReplaceableModel(Item item, ResourceLocation replacedModel, Supplier<Boolean> isReplace) {
        ResourceLocation res = Registry.ITEM.getKey(item);
        if (!res.equals(Registry.ITEM.getDefaultKey())) {
            ModelResourceLocation rawModelResourceLocation = new ModelResourceLocation(res, "inventory");
            ResourceLocation replacedModelResourceLocation = new ResourceLocation(replacedModel.getNamespace(), "item/" + replacedModel.getPath());
            REPLACEABLE_MODEL_LIST.add(Triple.of(rawModelResourceLocation, replacedModelResourceLocation, isReplace));
        }
    }
}
