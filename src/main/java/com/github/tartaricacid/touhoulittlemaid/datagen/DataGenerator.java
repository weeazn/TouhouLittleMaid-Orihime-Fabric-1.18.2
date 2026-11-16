package com.github.tartaricacid.touhoulittlemaid.datagen;

import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagBlock;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagEntity;
import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // Model
        fabricDataGenerator.addProvider(ItemModelGenerator::new);

        // Advancements
        fabricDataGenerator.addProvider(AdvancementDataGen::new);

        // Loot Tables
        fabricDataGenerator.addProvider(LootTableGenerator.ChestLootTables::new);
        fabricDataGenerator.addProvider(LootTableGenerator.AdvancementLootTables::new);
        fabricDataGenerator.addProvider(LootTableGenerator.EntityLootTables::new);

        // Global Loot Modifier Fabric使用Event修改
        // fabricDataGenerator.addProvider(packOutput -> new GlobalLootModifier(packOutput, registries, TouhouLittleMaid.MOD_ID));

        // Tags
        fabricDataGenerator.addProvider(TagEntity::new);
        fabricDataGenerator.addProvider(TagBlock::new);
        fabricDataGenerator.addProvider(TagItem::new);

    }
}
