package com.github.tartaricacid.touhoulittlemaid.compat.ipn;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class SortButtonScreen {
    private static final ResourceLocation SIDE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_side.png");
    private static final String IPN_ID = "inventoryprofilesnext";

    public static void renderBackground(PoseStack graphics, int x, int y) {
        if (FabricLoader.getInstance().isModLoaded(IPN_ID)) {
            MaidGui.texture(graphics, SIDE, x, y, 0, 73, 17, 48);
        }
    }
}
