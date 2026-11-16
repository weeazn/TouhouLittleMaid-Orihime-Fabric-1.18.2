package com.github.tartaricacid.touhoulittlemaid.init;

import net.minecraft.world.entity.decoration.Motive;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;


public class InitPaintingVariants {
    public static void init() {

    }

    public static final Motive WINE_FOX = register("wine_fox", new Motive(32, 48));

    private static Motive register(String name, Motive paintingVariant) {
        return Registry.register(Registry.MOTIVE, new ResourceLocation(TouhouLittleMaid.MOD_ID, name), paintingVariant);
    }
}
