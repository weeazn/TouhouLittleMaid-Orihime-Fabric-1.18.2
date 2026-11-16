package com.github.tartaricacid.touhoulittlemaid.client.gui.block;

import cn.sh1rocu.touhoulittlemaid.client.MaidGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TextComponent;
import cn.sh1rocu.touhoulittlemaid.mixin.accessor.ScreenAccessor;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.BeaconEffectButton;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import com.github.tartaricacid.touhoulittlemaid.network.message.SetBeaconOverflowMessage;
import com.github.tartaricacid.touhoulittlemaid.network.message.StorageAndTakePowerMessage;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBeacon;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBeacon.BeaconEffect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import java.text.DecimalFormat;

public class MaidBeaconGui extends Screen {
    private static final ResourceLocation BG = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_beacon.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private final TileEntityMaidBeacon beacon;

    protected int imageWidth = 300;
    protected int imageHeight = 113;
    protected int leftPos;
    protected int topPos;
    private boolean overflowDelete;
    private int potionIndex;

    public MaidBeaconGui(TileEntityMaidBeacon beacon) {
        super(new TextComponent("Maid Beacon GUI"));
        this.beacon = beacon;
        this.overflowDelete = beacon.isOverflowDelete();
        this.potionIndex = beacon.getPotionIndex();
    }

    @Override
    public void init() {
        this.clearWidgets();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.addEffectButton(this.leftPos + 146, 26, this.topPos + 19);
        this.addStorageAndTakeButton();
        this.addRenderableWidget(new Button(leftPos + 118, topPos + 94, 154, 20, getOverflowDeleteButtonText(this.overflowDelete), b -> {
            this.overflowDelete = !this.overflowDelete;
            ClientPlayNetworking.send(SetBeaconOverflowMessage.ID, SetBeaconOverflowMessage.encode(beacon.getBlockPos(), this.overflowDelete));
            this.init();
        }));
    }

    private void addStorageAndTakeButton() {
        this.addRenderableWidget(new Button(leftPos + 118, topPos + 72, 76, 20, new TranslatableComponent("gui.touhou_little_maid.maid_beacon.add_one")
                , b -> ClientPlayNetworking.send(StorageAndTakePowerMessage.ID, StorageAndTakePowerMessage.encode(beacon.getBlockPos(), 1, true))));

        this.addRenderableWidget(new Button(leftPos + 196, topPos + 72, 76, 20, new TranslatableComponent("gui.touhou_little_maid.maid_beacon.min_one")
                , b -> ClientPlayNetworking.send(StorageAndTakePowerMessage.ID, StorageAndTakePowerMessage.encode(beacon.getBlockPos(), 1, false))));
    }

    private void addEffectButton(int start, int spacing, int y) {
        this.addRenderableWidget(new BeaconEffectButton(BeaconEffect.SPEED, start, y, potionIndex, beacon, (isStateTriggered) -> {
            this.potionIndex = isStateTriggered ? BeaconEffect.SPEED.ordinal() : -1;
            this.init();
        }));
        this.addRenderableWidget(new BeaconEffectButton(BeaconEffect.FIRE_RESISTANCE, start + spacing, y, potionIndex, beacon, (isStateTriggered) -> {
            this.potionIndex = isStateTriggered ? BeaconEffect.FIRE_RESISTANCE.ordinal() : -1;
            this.init();
        }));
        this.addRenderableWidget(new BeaconEffectButton(BeaconEffect.STRENGTH, start + spacing * 2, y, potionIndex, beacon, (isStateTriggered) -> {
            this.potionIndex = isStateTriggered ? BeaconEffect.STRENGTH.ordinal() : -1;
            this.init();
        }));
        this.addRenderableWidget(new BeaconEffectButton(BeaconEffect.RESISTANCE, start + spacing * 3, y, potionIndex, beacon, (isStateTriggered) -> {
            this.potionIndex = isStateTriggered ? BeaconEffect.RESISTANCE.ordinal() : -1;
            this.init();
        }));
        this.addRenderableWidget(new BeaconEffectButton(BeaconEffect.REGENERATION, start + spacing * 4, y, potionIndex, beacon, (isStateTriggered) -> {
            this.potionIndex = isStateTriggered ? BeaconEffect.REGENERATION.ordinal() : -1;
            this.init();
        }));
    }

    @Override
    public void render(PoseStack graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        MaidGui.texture(graphics, BG, leftPos, topPos + 2, 0, 0, 142, 111);
        MaidGui.texture(graphics, BG, leftPos + 118, topPos + 1, 44, 111, 154, 15);

        MaidGui.texture(graphics, BG, leftPos + 224, topPos + 44, 44, 126, 12, 12);
        MaidGui.texture(graphics, BG, leftPos + 224, topPos + 58, 44, 138, 12, 12);

        MaidGui.texture(graphics, BG, leftPos + 146, topPos + 46, 58, 128, 74, 9);
        MaidGui.texture(graphics, BG, leftPos + 146, topPos + 59, 58, 128, 74, 9);
        float percent = beacon.getStoragePower() / beacon.getMaxStorage();
        MaidGui.texture(graphics, BG, leftPos + 146, topPos + 48, 58, 138, (int) (74 * percent), 5);

        this.renderPlayerPower(graphics);
        MaidGui.text(graphics, font, DECIMAL_FORMAT.format(beacon.getStoragePower()), leftPos + 240, topPos + 46, 0xffffff);
        if (potionIndex == -1) {
            this.drawCenteredLabel(graphics, font, I18n.get("gui.touhou_little_maid.maid_beacon.cost_power", DECIMAL_FORMAT.format(0)), leftPos + 195, topPos + 5, ChatFormatting.DARK_GRAY.getColor());
        } else {
            this.drawCenteredLabel(graphics, font, new TranslatableComponent("gui.touhou_little_maid.maid_beacon.cost_power", DECIMAL_FORMAT.format(beacon.getEffectCost() * 900)).withStyle(ChatFormatting.RED), leftPos + 195, topPos + 5, 0xffffff);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
        ((ScreenAccessor) this).tlm$getRenderables().stream().filter(b -> b instanceof BeaconEffectButton).forEach(b -> ((BeaconEffectButton) b).renderToolTip(graphics, this, mouseX, mouseY));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyCode, scanCode)/*isActiveAndMatches(mouseKey)*/) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void renderPlayerPower(PoseStack graphics) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            PowerAttachment power = InitDataAttachment.POWER_NUM.get(player);
            float percent = power.get() / PowerAttachment.MAX_POWER;
            MaidGui.texture(graphics, BG, leftPos + 146, topPos + 61, 58, 143, (int) (74 * percent), 5);
            MaidGui.text(graphics, font, DECIMAL_FORMAT.format(power.get()), leftPos + 240, topPos + 60, 0xffffff);
        }
    }

    private MutableComponent getOverflowDeleteButtonText(boolean overflowDelete) {
        return overflowDelete ? new TranslatableComponent("gui.touhou_little_maid.maid_beacon.overflow_delete_true") :
                new TranslatableComponent("gui.touhou_little_maid.maid_beacon.overflow_delete_false");
    }

    private void drawCenteredLabel(PoseStack graphics, Font font, String text, int pX, int pY, int color) {
        MaidGui.text(graphics, font, text, pX - font.width(text) / 2, pY, color, false);
    }

    private void drawCenteredLabel(PoseStack graphics, Font font, Component text, int pX, int pY, int color) {
        MaidGui.text(graphics, font, text, pX - font.width(text) / 2, pY, color, false);
    }
}
