package com.github.tartaricacid.touhoulittlemaid.client.gui.mod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.apache.commons.lang3.StringUtils;

public class ClothConfigScreen extends Screen {
    private final String clothConfigUrl = "https://www.curseforge.com/minecraft/mc-mods/cloth-config";
    private final Screen lastScreen;
    private MultiLineLabel message = MultiLineLabel.EMPTY;

    protected ClothConfigScreen(Screen lastScreen) {
        super(new TextComponent("Cloth Config API"));
        this.lastScreen = lastScreen;
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new ClothConfigScreen(Minecraft.getInstance().screen));
    }

    @Override
    public void init() {
        int posX = (this.width - 200) / 2;
        int posY = this.height / 2;
        this.message = MultiLineLabel.create(this.font, new TranslatableComponent("gui.touhou_little_maid.cloth_config_warning.tips"), 300);
        this.addRenderableWidget(new Button(posX, posY - 15, 200, 20, new TranslatableComponent("gui.touhou_little_maid.cloth_config_warning.download"), b -> openUrl(clothConfigUrl)));
        this.addRenderableWidget(new Button(posX, posY + 50, 200, 20, CommonComponents.GUI_BACK, (pressed) -> Minecraft.getInstance().setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack graphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(graphics);
        this.message.renderCentered(graphics, this.width / 2, 80);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }

    private void openUrl(String url) {
        if (StringUtils.isNotBlank(url) && minecraft != null) {
            minecraft.setScreen(new ConfirmLinkScreen(yes -> {
                if (yes) {
                    Util.getPlatform().openUri(url);
                }
                minecraft.setScreen(this);
            }, url, true));
        }
    }

//    public static void registerNoClothConfigPage() {
//        if (!FabricLoader.getInstance().isModLoaded(CompatRegistry.CLOTH_CONFIG)) {
//            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
//                    new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new ClothConfigScreen(parent)));
//        }
//    }
}
