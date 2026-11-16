package com.github.tartaricacid.touhoulittlemaid.client.resource;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;

public class LegacyPackRepositorySource implements RepositorySource {
    private static final String PACK_NAME = "touhou_little_maid_legacy_resources_pack";

    @Override
    public void loadPacks(Consumer<Pack> consumer, Pack.PackConstructor constructor) {
        var mod = FabricLoader.getInstance().getModContainer(TouhouLittleMaid.MOD_ID).orElseThrow();
        if (mod.findPath("legacy_pack/pack.mcmeta").isPresent()) {
            consumer.accept(new Pack(PACK_NAME, false,
                    () -> ModNioResourcePack.create(PACK_NAME, mod, "legacy_pack", PackType.CLIENT_RESOURCES, ResourcePackActivationType.NORMAL),
                    new TranslatableComponent("pack.touhou_little_maid.legacy_resources_pack.title"),
                    new TranslatableComponent("pack.touhou_little_maid.legacy_resources_pack.desc"),
                    PackCompatibility.COMPATIBLE, Pack.Position.TOP, false, PackSource.BUILT_IN));
        }
    }
}
