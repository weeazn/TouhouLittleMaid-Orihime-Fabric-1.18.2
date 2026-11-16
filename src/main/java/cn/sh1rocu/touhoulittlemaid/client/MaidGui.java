package cn.sh1rocu.touhoulittlemaid.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public final class MaidGui extends GuiComponent {
    private MaidGui() {}

    public static void renderEntityInInventory(PoseStack graphics, int x, int y, int scale,
                                                float mouseX, float mouseY, LivingEntity entity) {
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        try {
            modelView.mulPoseMatrix(graphics.last().pose());
            InventoryScreen.renderEntityInInventory(x, y, scale, mouseX, mouseY, entity);
        } finally {
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    public static void texture(PoseStack pose, ResourceLocation image, int x, int y, float u, float v, int w, int h) {
        texture(pose, image, x, y, u, v, w, h, 256, 256);
    }

    public static void texture(PoseStack pose, ResourceLocation image, int x, int y, float u, float v,
                               int w, int h, int texWidth, int texHeight) {
        RenderSystem.setShaderTexture(0, image);
        GuiComponent.blit(pose, x, y, u, v, w, h, texWidth, texHeight);
    }

    public static void texture(PoseStack pose, ResourceLocation image, int x, int y, int width, int height,
                               float u, float v, int regionWidth, int regionHeight, int texWidth, int texHeight) {
        RenderSystem.setShaderTexture(0, image);
        GuiComponent.blit(pose, x, y, width, height, u, v, regionWidth, regionHeight, texWidth, texHeight);
    }

    public static void gradient(PoseStack pose, int x1, int y1, int x2, int y2, int top, int bottom) {
        gradient(pose, x1, y1, x2, y2, 0, top, bottom);
    }

    public static void gradient(PoseStack pose, int x1, int y1, int x2, int y2, int z, int top, int bottom) {
        fillGradient(pose, x1, y1, x2, y2, top, bottom, z);
    }

    public static int text(PoseStack pose, Font font, String value, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, value, x, y, color) : font.draw(pose, value, x, y, color);
    }

    public static int text(PoseStack pose, Font font, FormattedCharSequence value, int x, int y, int color, boolean shadow) {
        return shadow ? font.drawShadow(pose, value, x, y, color) : font.draw(pose, value, x, y, color);
    }

    public static int text(PoseStack pose, Font font, Component value, int x, int y, int color, boolean shadow) {
        return text(pose, font, value.getVisualOrderText(), x, y, color, shadow);
    }

    public static int text(PoseStack pose, Font font, String value, int x, int y, int color) {
        return text(pose, font, value, x, y, color, true);
    }

    public static int text(PoseStack pose, Font font, Component value, int x, int y, int color) {
        return text(pose, font, value, x, y, color, true);
    }

    public static int text(PoseStack pose, Font font, FormattedCharSequence value, int x, int y, int color) {
        return text(pose, font, value, x, y, color, true);
    }

    public static void wrap(PoseStack pose, Font font, FormattedText text, int x, int y, int width, int color) {
        for (FormattedCharSequence line : font.split(text, width)) {
            font.draw(pose, line, x, y, color);
            y += font.lineHeight;
        }
    }

    public static void item(PoseStack pose, ItemStack stack, int x, int y) {
        withModelView(pose, () -> Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y));
    }

    public static void fakeItem(PoseStack pose, ItemStack stack, int x, int y) {
        withModelView(pose, () -> Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(stack, x, y));
    }

    public static void itemDecorations(PoseStack pose, Font font, ItemStack stack, int x, int y) {
        withModelView(pose, () -> Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(font, stack, x, y));
    }

    private static void withModelView(PoseStack pose, Runnable draw) {
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        try {
            modelView.mulPoseMatrix(pose.last().pose());
            RenderSystem.applyModelViewMatrix();
            draw.run();
        } finally {
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    public static void tooltip(PoseStack pose, Font font, Component text, int x, int y) {
        Minecraft.getInstance().screen.renderTooltip(pose, text, x, y);
    }

    public static void tooltip(PoseStack pose, Font font, List<? extends FormattedCharSequence> lines, int x, int y) {
        Minecraft.getInstance().screen.renderTooltip(pose, lines, x, y);
    }

    public static void componentTooltip(PoseStack pose, Font font, List<Component> lines, int x, int y) {
        Minecraft.getInstance().screen.renderComponentTooltip(pose, lines, x, y);
    }

    private record Clip(int left, int top, int right, int bottom) {}
    private static final Deque<Clip> clips = new ArrayDeque<>();

    public static void enableScissor(int left, int top, int right, int bottom) {
        Clip parent = clips.peek();
        if (parent != null) {
            left = Math.max(left, parent.left);
            top = Math.max(top, parent.top);
            right = Math.min(right, parent.right);
            bottom = Math.min(bottom, parent.bottom);
        }
        clips.push(new Clip(left, top, right, bottom));
        applyClip(clips.element());
    }

    public static void disableScissor() {
        clips.pop();
        if (clips.isEmpty()) RenderSystem.disableScissor();
        else applyClip(clips.element());
    }

    private static void applyClip(Clip clip) {
        Window window = Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        RenderSystem.enableScissor((int) (clip.left * scale), (int) (window.getHeight() - clip.bottom * scale),
                Math.max(0, (int) ((clip.right - clip.left) * scale)), Math.max(0, (int) ((clip.bottom - clip.top) * scale)));
    }
}
