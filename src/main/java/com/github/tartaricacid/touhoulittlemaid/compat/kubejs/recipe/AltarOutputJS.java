package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.recipe;

import net.minecraft.core.Registry;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCraftingHelper;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.Optional;

public final class AltarOutputJS {
    /**
     * Converting KubeJS' Item object to the altar's' output item object
     * @param stack The ItemStack to convert to an altar output item.
     */
    public static EntityCraftingHelper.Output item(ItemStack stack) {
        EntityType<ItemEntity> item = EntityType.ITEM;
        CompoundTag data = new CompoundTag();
        data.put("Item", stack.save(new CompoundTag()));
        return new EntityCraftingHelper.Output(item, data);
    }

    /**
     * Directly converting an entity ID and extra NBT data to the altar's output entity object
     * @param entityId The ID of the entity type to output.
     * @param data Additional NBT data to attach to the entity.
     */
    public static EntityCraftingHelper.Output entity(String entityId, CompoundTag data) {
        Optional<EntityType<?>> value = Registry.ENTITY_TYPE.getOptional(new ResourceLocation(entityId));
        if (value.isEmpty()) {
            throw new JsonParseException("Entity Type Not Found: " + entityId);
        }
        return new EntityCraftingHelper.Output(value.get(), data);
    }

    /**
     * Directly converting an entity ID to the altar's output entity object with empty NBT data
     * @param entityId The ID of the entity type to output.
     */
    public static EntityCraftingHelper.Output entity(String entityId) {
        return entity(entityId, new CompoundTag());
    }

    /**
     * Spawn a new maid with a cake box
     */
    public static EntityCraftingHelper.Output spawnMaidWithBox() {
        ResourceLocation key = Registry.ENTITY_TYPE.getKey(InitEntities.MAID);
        if (key.equals(Registry.ENTITY_TYPE.getDefaultKey())) {
            throw new JsonParseException("Maid Entity Type Not Found");
        }
        CompoundTag data = new CompoundTag();
        ListTag passengers = new ListTag();
        CompoundTag maid = new CompoundTag();
        maid.putString("id", key.toString());
        passengers.add(maid);
        data.put("Passengers", passengers);
        return new EntityCraftingHelper.Output(InitEntities.BOX, data);
    }

    /**
     * Reborn the maid from film, the ingredient must have a film
     */
    public static EntityCraftingHelper.Output rebornMaid() {
        return new EntityCraftingHelper.Output(InitEntities.MAID, new CompoundTag(), Ingredient.of(InitItems.FILM), "MaidInfo");
    }
}
