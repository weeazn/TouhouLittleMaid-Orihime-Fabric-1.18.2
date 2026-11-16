package com.github.tartaricacid.touhoulittlemaid.entity.task;

import net.minecraft.world.entity.ai.behavior.Behavior;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidMeleeAttack;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidUseShieldTask;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityExtinguishingAgent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidStartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import java.util.function.Predicate;

public class TaskAttack implements IAttackTask {
    public static final ResourceLocation UID = new ResourceLocation(TouhouLittleMaid.MOD_ID, "attack");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return Items.DIAMOND_SWORD.getDefaultInstance();
    }

    @Override
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return SoundUtil.attackSound(maid, InitSounds.MAID_ATTACK, 0.5f);
    }

    @Override
    public List<Pair<Integer, Behavior<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        Behavior<EntityMaid> supplementedTask = new MaidStartAttacking(this::hasAssaultWeapon, this::findTarget);
        Behavior<EntityMaid> findTargetTask = new StopAttackingIfTargetInvalid<>(target -> !hasAssaultWeapon(maid) || farAway(target, maid));
        Behavior<Mob> moveToTargetTask = new SetWalkTargetFromAttackTargetIfTargetOutOfReach(0.6f);
        Behavior<EntityMaid> attackTargetTask = new MaidMeleeAttack<>(20, EntityMaid::isWithinMeleeAttackRange);
        MaidUseShieldTask maidUseShieldTask = new MaidUseShieldTask();

        return Lists.newArrayList(
                Pair.of(5, supplementedTask),
                Pair.of(5, findTargetTask),
                Pair.of(5, moveToTargetTask),
                Pair.of(5, attackTargetTask),
                Pair.of(5, maidUseShieldTask)
        );
    }

    @Override
    public List<Pair<Integer, Behavior<? super EntityMaid>>> createRideBrainTasks(EntityMaid maid) {
        Behavior<EntityMaid> supplementedTask = new MaidStartAttacking(this::hasAssaultWeapon, this::findTarget);
        Behavior<EntityMaid> findTargetTask = new StopAttackingIfTargetInvalid<>(target -> !hasAssaultWeapon(maid) || farAway(target, maid));
        Behavior<EntityMaid> attackTargetTask = new MaidMeleeAttack<>(20, EntityMaid::isWithinMeleeAttackRange);
        MaidUseShieldTask maidUseShieldTask = new MaidUseShieldTask();

        return Lists.newArrayList(
                Pair.of(5, supplementedTask),
                Pair.of(5, findTargetTask),
                Pair.of(5, attackTargetTask),
                Pair.of(5, maidUseShieldTask)
        );
    }

    @Override
    public boolean hasExtraAttack(EntityMaid maid, Entity target) {
        return maid.getOffhandItem().is(InitItems.EXTINGUISHER) && target.fireImmune();
    }

    @Override
    public boolean doExtraAttack(EntityMaid maid, Entity target) {
        Level world = maid.level;
        AABB aabb = target.getBoundingBox().inflate(1.5, 1, 1.5);
        List<EntityExtinguishingAgent> extinguishingAgents = world.getEntitiesOfClass(EntityExtinguishingAgent.class, aabb, Entity::isAlive);
        if (extinguishingAgents.isEmpty()) {
            world.addFreshEntity(new EntityExtinguishingAgent(world, target.position()));
            maid.getOffhandItem().hurtAndBreak(1, maid, (m) -> m.broadcastBreakEvent(InteractionHand.OFF_HAND));
            return true;
        }
        return false;
    }

    @Override
    public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
        return Lists.newArrayList(Pair.of("assault_weapon", this::hasAssaultWeapon), Pair.of("extinguisher", this::hasExtinguisher));
    }

    @Override
    public boolean isWeapon(EntityMaid maid, ItemStack stack) {
        return stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    private boolean hasAssaultWeapon(EntityMaid maid) {
        return isWeapon(maid, maid.getMainHandItem());
    }

    private boolean hasExtinguisher(EntityMaid maid) {
        return maid.getOffhandItem().is(InitItems.EXTINGUISHER);
    }

    private Optional<? extends LivingEntity> findTarget(EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .flatMap(visible -> visible.findClosest(target -> !farAway(target, maid) && maid.canAttack(target)));
    }

    private boolean farAway(LivingEntity target, EntityMaid maid) {
        if (!target.isAlive() || !maid.isWithinRestriction(target.blockPosition())) return true;
        LivingEntity owner = maid.hasRestriction() ? null : maid.getOwner();
        double radius = maid.getRestrictRadius();
        return (owner == null ? maid : owner).distanceToSqr(target) > radius * radius;
    }

    @Override
    public String getMaidActionSummary() {
        return "Melee attack entities";
    }
}
