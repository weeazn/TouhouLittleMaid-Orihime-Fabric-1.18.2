package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.recipe;

import com.github.tartaricacid.touhoulittlemaid.util.EntityCraftingHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.ItemStack;

public class AltarComponent {
    public static final AltarComponent INSTANCE = new AltarComponent();

    public Class<?> componentClass() {
        return EntityCraftingHelper.Output.class;
    }

    public JsonElement write(RecipeJS recipe, EntityCraftingHelper.Output value) {
        return EntityCraftingHelper.writeEntityData(value);
    }

    public EntityCraftingHelper.Output read(RecipeJS recipe, Object from) {
        if (from instanceof JsonObject jsonObject) {
            return EntityCraftingHelper.getEntityData(jsonObject);
        }
        if (from instanceof EntityCraftingHelper.Output output) {
            return output;
        }
        ItemStack itemStack = ItemStackJS.of(from).getItemStack();
        if (!itemStack.isEmpty()) {
            return AltarOutputJS.item(itemStack);
        }
        throw new IllegalArgumentException("Expected JSON Output object!");
    }
}
