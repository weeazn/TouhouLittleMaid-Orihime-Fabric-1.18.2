package com.github.tartaricacid.touhoulittlemaid.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class DanmakuDamage extends IndirectEntityDamageSource {
    private DanmakuDamage(Entity owner, Entity projectile) {
        super("touhou_little_maid.danmaku", projectile, owner);
        setProjectile();
    }

    public static DamageSource create(Entity owner, Entity projectile, boolean enderKiller) {
        return enderKiller ? new EnderDanmakuDamage(owner, projectile) : new DanmakuDamage(owner, projectile);
    }

    @Override
    public boolean scalesWithDifficulty() {
        return getEntity() instanceof LivingEntity && !(getEntity() instanceof Player);
    }
}
