package com.github.tartaricacid.touhoulittlemaid.event;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.data.MaidNumAttachment;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.MAID_NUM;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.POWER_NUM;

public class EntityDeathEvent {
    public static void onEntityDeath(LivingEntity target, DamageSource cause) {
        if (!target.level.isClientSide && cause.getEntity() instanceof EntityMaid maid) {
            maid.getKillRecordManager().onTargetDeath(maid, target);
        }
    }

    public static void onPlayerCloned() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            boolean wasDeath = !alive;
            boolean isKeep = newPlayer.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);

            PowerAttachment power = POWER_NUM.get(oldPlayer);
            MaidNumAttachment maidNum = MAID_NUM.get(oldPlayer);
            if (wasDeath && !isKeep) {
                power.min(MiscConfig.PLAYER_DEATH_LOSS_POWER_POINT.get().floatValue());
            }
            POWER_NUM.get(newPlayer).copyFrom(power);
            MAID_NUM.get(newPlayer).copyFrom(maidNum);
        });
    }
}
