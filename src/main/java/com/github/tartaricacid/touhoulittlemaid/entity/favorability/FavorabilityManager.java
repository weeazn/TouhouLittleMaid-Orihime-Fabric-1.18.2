package com.github.tartaricacid.touhoulittlemaid.entity.favorability;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidFavorabilityLevelChangeEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticleMessage;
import com.google.common.collect.Maps;
import com.github.tartaricacid.touhoulittlemaid.config.ServerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.Map;

/**
 * FIXME：这个好感度机制太落伍了，未来需要重新设计一个更合理的好感度系统
 */
public class FavorabilityManager {
    public static final Map<String, Type> TYPES = Maps.newHashMap();

    private static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;

    private int appliedHealth = 20;
    private int appliedAttack = 2;

    private static final int LEVEL_0_ATTACK_DAMAGE = 2;
    private static final int LEVEL_1_ATTACK_DAMAGE = 3;
    private static final int LEVEL_2_ATTACK_DAMAGE = 4;
    private static final int LEVEL_3_ATTACK_DAMAGE = 6;

    private static final int LEVEL_0_ATTACK_DISTANCE_PLUS = 0;
    private static final int LEVEL_1_ATTACK_DISTANCE_PLUS = 1;
    private static final int LEVEL_2_ATTACK_DISTANCE_PLUS = 3;
    private static final int LEVEL_3_ATTACK_DISTANCE_PLUS = 5;

    private static final double LEVEL_0_SWEEP_RANGE = 1;
    private static final double LEVEL_1_SWEEP_RANGE = 2;
    private static final double LEVEL_2_SWEEP_RANGE = 3;
    private static final double LEVEL_3_SWEEP_RANGE = 4;

    private static final String TAG_NAME = "FavorabilityManagerCounter";

    private final Map<String, Time> counter;
    private final EntityMaid maid;

    public FavorabilityManager(EntityMaid maid) {
        this.counter = Maps.newHashMap();
        this.maid = maid;
    }

    public void tick() {
        if (maid.tickCount % 20 == 0) updateAttributes(getLevel());
        counter.values().forEach(Time::tick);
    }

    public void apply(String type) {
        Type typeInstance = TYPES.get(type);
        if (typeInstance != null) {
            this.apply(typeInstance);
        }
    }

    public void apply(Type type) {
        if (this.canAdd(type.getTypeName())) {
            if (type.isReduce()) {
                this.reduce(type.getPoint());
            } else {
                this.add(type.getPoint());
            }
            this.addCooldown(type.getTypeName(), type.getCooldown());
        }
    }

    public void apply(Type type, int point) {
        if (this.canAdd(type.getTypeName())) {
            if (type.isReduce()) {
                this.reduce(point);
            } else {
                this.add(point);
            }
            this.addCooldown(type.getTypeName(), type.getCooldown());
        }
    }

    private void addCooldown(String type, int tickCount) {
        this.counter.put(type, new Time(tickCount));
    }

    public boolean canAdd(String type) {
        if (this.counter.containsKey(type)) {
            return this.counter.get(type).isZero();
        }
        return true;
    }

    public int getLevel() {
        return this.getLevel(maid.getFavorability());
    }

    private int getLevel(int favorability) {
        if (favorability < getPointByLevel(1)) {
            return LEVEL_0;
        } else if (favorability < getPointByLevel(2)) {
            return LEVEL_1;
        } else if (favorability < getPointByLevel(3)) {
            return LEVEL_2;
        } else {
            return LEVEL_3;
        }
    }

    public double getLevelPercent() {
        int favorability = this.maid.getFavorability();
        if (favorability < getPointByLevel(1)) {
            return favorability / (double) getPointByLevel(1);
        } else if (favorability < getPointByLevel(2)) {
            return (favorability - getPointByLevel(1)) / (double) (getPointByLevel(2) - getPointByLevel(1));
        } else if (favorability < getPointByLevel(3)) {
            return (favorability - getPointByLevel(2)) / (double) (getPointByLevel(3) - getPointByLevel(2));
        } else {
            return 0;
        }
    }

    public int nextLevelPoint() {
        int level = this.getLevel();
        if (level == LEVEL_3) {
            return 0;
        }
        int pointByLevel = getPointByLevel(level + 1);
        return pointByLevel - this.maid.getFavorability();
    }

    public int getHealthByLevel(int level) {
        int health = 20;
        for (int i = 0; i < Math.min(level, LEVEL_3); i++) health += ServerConfig.FAVOR_HEALTH[i].get();
        return health;
    }

    public int getAttackByLevel(int level) {
        switch (level) {
            case LEVEL_1 -> {
                return LEVEL_1_ATTACK_DAMAGE;
            }
            case LEVEL_2 -> {
                return LEVEL_2_ATTACK_DAMAGE;
            }
            case LEVEL_3 -> {
                return LEVEL_3_ATTACK_DAMAGE;
            }
            default -> {
                return LEVEL_0_ATTACK_DAMAGE;
            }
        }
    }

    public int getAttackDistancePlusByPoint(int favorability) {
        if (favorability < getPointByLevel(1)) {
            return LEVEL_0_ATTACK_DISTANCE_PLUS;
        } else if (favorability < getPointByLevel(2)) {
            return LEVEL_1_ATTACK_DISTANCE_PLUS;
        } else if (favorability < getPointByLevel(3)) {
            return LEVEL_2_ATTACK_DISTANCE_PLUS;
        } else {
            return LEVEL_3_ATTACK_DISTANCE_PLUS;
        }
    }

    public AABB getSweepRange(Entity target, int favorability) {
        AABB boundingBox = target.getBoundingBox();
        if (favorability < getPointByLevel(1)) {
            return sweepRangeTransform(boundingBox, LEVEL_0_SWEEP_RANGE);
        } else if (favorability < getPointByLevel(2)) {
            return sweepRangeTransform(boundingBox, LEVEL_1_SWEEP_RANGE);
        } else if (favorability < getPointByLevel(3)) {
            return sweepRangeTransform(boundingBox, LEVEL_2_SWEEP_RANGE);
        } else {
            return sweepRangeTransform(boundingBox, LEVEL_3_SWEEP_RANGE);
        }
    }

    private AABB sweepRangeTransform(AABB boundingBox, double range) {
        return boundingBox.inflate(range, Math.max(range / 4, 0.25), range);
    }

    public int getPointByLevel(int level) {
        int points = 0;
        for (int i = 0; i < Math.min(level, LEVEL_3); i++) points += ServerConfig.FAVOR_COST[i].get();
        return points;
    }

    private void updateAttributes(int level) {
        int health = getHealthByLevel(level);
        int attack = getAttackByLevel(level);
        if (health != appliedHealth) {
            AttributeInstance attr = maid.getAttribute(Attributes.MAX_HEALTH);
            if (attr != null) {
                attr.setBaseValue(attr.getBaseValue() + health - appliedHealth);
                if (maid.getHealth() > maid.getMaxHealth()) maid.setHealth(maid.getMaxHealth());
            }
            appliedHealth = health;
        }
        if (attack != appliedAttack) {
            AttributeInstance attr = maid.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attr != null) attr.setBaseValue(attr.getBaseValue() + attack - appliedAttack);
            appliedAttack = attack;
        }
    }

    public void add(int addPoint) {
        int favorability = maid.getFavorability();
        int levelBefore = getLevel();
        int result = (int) Mth.clamp((long) favorability + addPoint, 0L, Math.max(getPointByLevel(3), favorability));
        maid.setFavorability(result);
        int levelAfter = getLevel();

        if (levelBefore < levelAfter) {
            updateAttributes(levelAfter);
            if (maid.getMaxHealth() >= 100 && maid.getOwner() instanceof ServerPlayer owner) {
                InitTrigger.MAID_EVENT.trigger(owner, TriggerType.MAID_100_HEALTHY);
            }

            if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
                InitTrigger.MAID_EVENT.trigger(serverPlayer, TriggerType.FAVORABILITY_INCREASED);
                if (levelAfter == LEVEL_3) {
                    InitTrigger.MAID_EVENT.trigger(serverPlayer, TriggerType.FAVORABILITY_INCREASED_MAX);
                }
            }

            // 最后触发事件和饰品
            this.onFavorabilityLevelChange(levelBefore, levelAfter);
        }
        NetworkHandler.sendToNearby(maid, SpawnParticleMessage.ID, SpawnParticleMessage.encode(maid.getId(), SpawnParticleMessage.Type.HEART));
    }

    public void reduce(int reducePoint) {
        int favorability = maid.getFavorability();
        int pointByLevel = getPointByLevel(getLevel());
        int result = Mth.clamp(favorability - reducePoint, pointByLevel, getPointByLevel(3));
        maid.setFavorability(result);
    }

    public void reduceWithoutLevel(int reducePoint) {
        int favorability = maid.getFavorability();
        int levelBefore = getLevel();
        int result = Mth.clamp(favorability - reducePoint, 0, getPointByLevel(3));
        maid.setFavorability(result);
        int levelAfter = getLevel();

        if (levelBefore > levelAfter) {
            updateAttributes(levelAfter);

            // 最后触发事件和饰品
            this.onFavorabilityLevelChange(levelBefore, levelAfter);
        }
    }

    public void onFavorabilityLevelChange(int oldLevel, int newLevel) {
        // 最后触发事件和饰品
        this.maid.getMaidBauble().fireEvent((b, s) -> {
            b.onFavorabilityLevelChange(this.maid, s, oldLevel, newLevel);
            return false;
        });
        var event = new MaidFavorabilityLevelChangeEvent(maid, oldLevel, newLevel);
        MaidFavorabilityLevelChangeEvent.CALLBACK.invoker().post(event);
    }

    public void max() {
        this.add(getPointByLevel(3));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("AppliedFavorHealth", appliedHealth);
        compound.putInt("AppliedFavorAttack", appliedAttack);
        CompoundTag data = new CompoundTag();
        this.counter.forEach((name, time) -> data.putInt(name, time.getTickCount()));
        compound.put(TAG_NAME, data);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        int points = maid.getFavorability();
        int legacyLevel = points >= 384 ? 3 : points >= 192 ? 2 : points >= 64 ? 1 : 0;
        appliedHealth = compound.contains("AppliedFavorHealth", Tag.TAG_INT) ? compound.getInt("AppliedFavorHealth")
                : (legacyLevel == 3 ? 80 : 20 + 10 * legacyLevel);
        appliedAttack = compound.contains("AppliedFavorAttack", Tag.TAG_INT) ? compound.getInt("AppliedFavorAttack") : getAttackByLevel(legacyLevel);
        if (!maid.level.isClientSide) updateAttributes(getLevel());
        if (compound.contains(TAG_NAME, Tag.TAG_COMPOUND)) {
            CompoundTag data = compound.getCompound(TAG_NAME);
            for (String name : data.getAllKeys()) {
                this.counter.put(name, new Time(data.getInt(name)));
            }
        }
    }

    public static class Time {
        private int tickCount;

        public Time(int tickCount) {
            this.tickCount = tickCount;
        }

        public int getTickCount() {
            return tickCount;
        }

        public void setTickCount(int tickCount) {
            this.tickCount = tickCount;
        }

        public void tick() {
            if (tickCount > 0) {
                tickCount--;
            }
        }

        public boolean isZero() {
            return this.tickCount <= 0;
        }
    }
}