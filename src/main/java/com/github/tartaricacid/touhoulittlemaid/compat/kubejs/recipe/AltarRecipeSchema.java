package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.recipe;

import com.github.tartaricacid.touhoulittlemaid.util.EntityCraftingHelper;
import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

public final class AltarRecipeSchema extends RecipeJS {
    @Override
    public void create(ListJS args) {
        if (args.size() < 2 || args.size() > 3) throw new IllegalArgumentException("Expected output, ingredients and optional power");
        EntityCraftingHelper.Output output = AltarComponent.INSTANCE.read(this, args.get(0));
        json.add("output", EntityCraftingHelper.writeEntityData(output));
        for (Object ingredient : ListJS.orSelf(args.get(1))) inputItems.add(IngredientJS.of(ingredient));
        json.addProperty("power", args.size() == 3 ? ((Number) args.get(2)).floatValue() : 0.2f);
        serializeInputs = true;
    }

    @Override
    public void deserialize() {
        for (var entry : json.getAsJsonArray("ingredients"))
            inputItems.add(IngredientJS.ingredientFromRecipeJson(entry));
    }

    @Override
    public void serialize() {
        if (serializeInputs) {
            JsonArray ingredients = new JsonArray();
            for (IngredientJS input : inputItems) ingredients.add(input.toJson());
            json.add("ingredients", ingredients);
        }
    }
}
