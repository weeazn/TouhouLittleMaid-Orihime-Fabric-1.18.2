package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.entity.projectile.DanmakuDamage;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EnderDanmakuDamage;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public final class MaidDamageTags implements SimpleSynchronousResourceReloadListener {
    public static final ResourceLocation PROJECTILE = new ResourceLocation("is_projectile");
    public static final ResourceLocation FIRE = new ResourceLocation("is_fire");
    public static final ResourceLocation EXPLOSION = new ResourceLocation("is_explosion");
    public static final ResourceLocation DROWNING = new ResourceLocation("is_drowning");
    public static final ResourceLocation FALL = new ResourceLocation("is_fall");
    public static final ResourceLocation MAGIC = new ResourceLocation("witch_resistant_to");
    public static final ResourceLocation BYPASSES_INVULNERABILITY = new ResourceLocation("bypasses_invulnerability");
    public static final ResourceLocation DRAGON = new ResourceLocation("always_hurts_ender_dragons");
    public static final ResourceLocation LICH = new ResourceLocation("twilightforest", "breaks_lich_shields");

    private static final ResourceLocation DANMAKU = maidId("danmaku");
    private static final ResourceLocation ENDER_DANMAKU = maidId("danmaku_ender_killer");
    private static final ResourceLocation OLD_PROJECTILE = maidId("legacy_projectile");
    private static final ResourceLocation OLD_MAGIC = maidId("legacy_magic");
    private static final ResourceLocation OLD_FIRE = maidId("legacy_fire");
    private static final ResourceLocation OLD_EXPLOSION = maidId("legacy_explosion");
    private static final ResourceLocation OLD_INVUL = maidId("legacy_bypasses_invulnerability");
    private static volatile Map<ResourceLocation, Set<ResourceLocation>> tags = Map.of();

    private static ResourceLocation maidId(String path) {
        return new ResourceLocation("touhou_little_maid", path);
    }

    @Override
    public ResourceLocation getFabricId() {
        return maidId("damage_tags");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resources) {
        // 1.18 sources have message IDs instead of registry entries. The native tag loader
        // still handles pack ordering, replace, nested tags and optional references.
        var loader = new TagLoader<ResourceLocation>(Optional::of, "tags/damage_type");
        tags = loader.loadAndBuild(resources).entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> Set.copyOf(entry.getValue().getValues())));
    }

    public static boolean matches(ResourceLocation tag, DamageSource source) {
        Set<ResourceLocation> values = tags.get(tag);
        if (values == null) return false;
        if (source instanceof EnderDanmakuDamage) return values.contains(ENDER_DANMAKU);
        if (source instanceof DanmakuDamage) return values.contains(DANMAKU);
        ResourceLocation id = damageId(source);
        return (id != null && values.contains(id))
                || (source.isMagic() && values.contains(OLD_MAGIC))
                || (source.isFire() && values.contains(OLD_FIRE))
                || (source.isProjectile() && values.contains(OLD_PROJECTILE))
                || (source.isExplosion() && values.contains(OLD_EXPLOSION))
                || (source.isBypassInvul() && values.contains(OLD_INVUL));
    }

    private static ResourceLocation damageId(DamageSource source) {
        String name = source.getMsgId();
        String id = switch (name) {
            case "player" -> "player_attack";
            case "mob" -> source instanceof IndirectEntityDamageSource ? "mob_projectile" : "mob_attack";
            case "onFire" -> source instanceof IndirectEntityDamageSource ? "unattributed_fireball" : "on_fire";
            case "inFire" -> "in_fire";
            case "lightningBolt" -> "lightning_bolt";
            case "hotFloor" -> "hot_floor";
            case "inWall" -> "in_wall";
            case "flyIntoWall" -> "fly_into_wall";
            case "outOfWorld" -> "out_of_world";
            case "anvil" -> "falling_anvil";
            case "fallingBlock" -> "falling_block";
            case "fallingStalactite" -> "falling_stalactite";
            case "dragonBreath" -> "dragon_breath";
            case "dryout" -> "dry_out";
            case "sweetBerryBush" -> "sweet_berry_bush";
            case "witherSkull" -> "wither_skull";
            case "indirectMagic" -> "indirect_magic";
            case "explosion.player" -> "player_explosion";
            case "badRespawnPoint" -> "bad_respawn_point";
            case "bullet" -> "tacz:bullet";
            default -> name.indexOf(':') < 0 && name.contains(".") ? name.replaceFirst("\\.", ":") : name;
        };
        return ResourceLocation.tryParse(id);
    }
}
