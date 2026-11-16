package com.github.tartaricacid.touhoulittlemaid.crafting;

import cn.sh1rocu.touhoulittlemaid.util.forge.AbstractIngredient;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import io.github.tropheusj.serialization_hooks.ingredient.CustomIngredient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FallbackIngredient extends AbstractIngredient {
    public static final ResourceLocation ID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "fallback_ingredient");

    private final Ingredient fallbacks;

    protected FallbackIngredient(Ingredient fallbacks) {
        this.fallbacks = fallbacks;
    }

    public static FallbackIngredient of(Ingredient fallbacks) {
        return new FallbackIngredient(fallbacks);
    }


    @Override
    public boolean test(@Nullable ItemStack stack) {
        return this.fallbacks.test(stack);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return List.of(getItems());
    }

    @Override
    public boolean requiresTesting() {
        return CustomIngredient.customDeserializer(fallbacks);
    }

    @Override
    public ItemStack[] getItems() {
        return this.fallbacks.getItems();
    }

    @Override
    public IntList getStackingIds() {
        return this.fallbacks.getStackingIds();
    }

    @Override
    public IngredientDeserializer getDeserializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        throw new UnsupportedOperationException("FallbackIngredient does not support datagen toJson");
    }

    @Override
    public boolean isEmpty() {
        return fallbacks.isEmpty();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(ID);
        fallbacks.toNetwork(buffer);
    }

    public static class Serializer implements IngredientDeserializer {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public FallbackIngredient fromJson(JsonObject json) {
            JsonArray jsonArray = json.getAsJsonArray("fallbacks");
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String modId = jsonObject.get("modid").getAsString();
                if (FabricLoader.getInstance().isModLoaded(modId)) {
                    Ingredient ingredient = Ingredient.fromJson(jsonObject.get("value"));
                    return new FallbackIngredient(ingredient);
                }
            }
            return new FallbackIngredient(Ingredient.EMPTY);
        }

        @Override
        public FallbackIngredient fromNetwork(FriendlyByteBuf buffer) {
            return new FallbackIngredient(Ingredient.fromNetwork(buffer));
        }
    }
}
