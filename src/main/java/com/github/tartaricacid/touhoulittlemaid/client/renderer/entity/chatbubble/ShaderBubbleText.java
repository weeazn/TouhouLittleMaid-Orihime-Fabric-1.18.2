package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.chatbubble;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import java.util.ArrayList;
import java.util.List;

public final class ShaderBubbleText {
    private record Line(Font font, FormattedCharSequence text, float x, float y, float scale, int color, boolean shadow) {}
    private static final List<Line> lines = new ArrayList<>();
    private static boolean collecting;

    public static void init() {
        if (!FabricLoader.getInstance().isModLoaded("iris")) return;
        WorldRenderEvents.START.register(context -> {
            lines.clear();
            collecting = IrisApi.getInstance().isShaderPackInUse();
        });
        WorldRenderEvents.END.register(context -> collecting = false);
    }

    static boolean collect(EntityGraphics graphics, Font font, FormattedCharSequence text, float x, float y, int color, boolean shadow) {
        if (!collecting) return false;
        if (IrisApi.getInstance().isRenderingShadowPass()) return true;
        var game = Minecraft.getInstance();
        var maid = graphics.getMaid();
        if (game.level == null || game.level.getEntity(maid.getId()) != maid) return false;
        var camera = game.gameRenderer.getMainCamera();
        var anchor = maid.getPosition(graphics.getPartialTicks()).add(0, maid.getBbHeight() + 0.5, 0);
        if (game.level.clip(new ClipContext(camera.getPosition(), anchor, ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE, camera.getEntity())).getType() != HitResult.Type.MISS) return true;

        Matrix4f clip = RenderSystem.getProjectionMatrix().copy();
        clip.multiply(RenderSystem.getModelViewMatrix());
        clip.multiply(graphics.getPose().last().pose());
        var origin = new Vector4f(x, y, 0, 1);
        var right = new Vector4f(x + 1, y, 0, 1);
        origin.transform(clip);
        right.transform(clip);
        if (origin.w() <= 0 || right.w() <= 0) return true;
        float guiWidth = (float)(game.getWindow().getWidth() / game.getWindow().getGuiScale());
        float guiHeight = (float)(game.getWindow().getHeight() / game.getWindow().getGuiScale());
        float ndcX = origin.x() / origin.w();
        float scale = (right.x() / right.w() - ndcX) * guiWidth / 2;
        lines.add(new Line(font, text, (1 + ndcX) * guiWidth / 2, (1 - origin.y() / origin.w()) * guiHeight / 2,
                scale, color, shadow));
        return true;
    }

    public static void draw() {
        if (lines.isEmpty()) return;
        var pose = new PoseStack();
        for (Line line : lines) {
            pose.pushPose();
            pose.translate(line.x, line.y, 0);
            pose.scale(line.scale, line.scale, 1);
            if (line.shadow) line.font.drawShadow(pose, line.text, 0, 0, line.color);
            else line.font.draw(pose, line.text, 0, 0, line.color);
            pose.popPose();
        }
        lines.clear();
    }
}
