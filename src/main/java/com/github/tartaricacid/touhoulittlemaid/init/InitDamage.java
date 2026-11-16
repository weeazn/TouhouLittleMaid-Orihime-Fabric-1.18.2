package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.entity.projectile.DanmakuDamage;
import com.github.tartaricacid.touhoulittlemaid.entity.projectile.EntityDanmaku;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public final class InitDamage {
    public static DamageSource danmakuDamage(Entity thrower, EntityDanmaku danmaku) {
        return DanmakuDamage.create(thrower, danmaku, danmaku.isHurtEnderman());
    }

}
