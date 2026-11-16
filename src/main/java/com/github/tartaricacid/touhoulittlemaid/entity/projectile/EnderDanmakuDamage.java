package com.github.tartaricacid.touhoulittlemaid.entity.projectile;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

/** 1.18 endermen evade all IndirectEntityDamageSource hits, including unflagged ones. */
public final class EnderDanmakuDamage extends EntityDamageSource {
    private final Entity projectile;

    public EnderDanmakuDamage(Entity owner, Entity projectile) {
        super("touhou_little_maid.danmaku", owner);
        this.projectile = projectile;
    }

    @Override
    public Entity getDirectEntity() {
        return projectile;
    }
}
