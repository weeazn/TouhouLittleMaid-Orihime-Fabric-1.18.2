package com.github.tartaricacid.touhoulittlemaid.client.event;

import net.minecraft.network.chat.TranslatableComponent;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.inner.InnerAnimation;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.GeckoModelLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.models.PlayerMaidModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.time.StopWatch;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Environment(EnvType.CLIENT)
public final class ReloadResourceEvent extends SimplePreparableReloadListener<Void> implements IdentifiableResourceReloadListener {
    public static void asyncReloadAllPack() {
        CompletableFuture.supplyAsync(() -> {
            reloadAllPack();
            return null;
        }, Util.backgroundExecutor());
    }

    private static void reloadAllPack() {
        StopWatch watch = StopWatch.createStarted();
        {
            GeckoModelLoader.reload();
            InnerAnimation.init();
            CustomPackLoader.reloadPacks();
            PlayerMaidModels.reload();
        }
        watch.stop();
        double time = watch.getTime(TimeUnit.MICROSECONDS) / 1000.0;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("message.touhou_little_maid.reload.tip", time), net.minecraft.Util.NIL_UUID);
        }
        TouhouLittleMaid.LOGGER.info("Model loading time: {} ms", time);
    }

    @Override
    protected Void prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return null;
    }

    @Override
    protected void apply(Void pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        reloadAllPack();
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(TouhouLittleMaid.MOD_ID, "reload_resource_event");
    }
}
