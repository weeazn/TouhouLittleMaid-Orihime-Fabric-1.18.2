package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event;

import com.github.tartaricacid.touhoulittlemaid.api.event.*;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.common.*;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CommonEventsPostJS {
    public void interactMaid(InteractMaidEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        Item item = event.getStack().getItem();
        boolean cancelled = new InteractMaidEventJS(event).post(scriptType, "maid.interactMaid", net.minecraft.core.Registry.ITEM.getKey(item).toString());
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidAfterEat(MaidAfterEatEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        Item item = event.getFoodAfterEat().getItem();
        new MaidAfterEatEventJS(event).post(scriptType, "maid.maidAfterEat", net.minecraft.core.Registry.ITEM.getKey(item).toString());
    }

    public void maidAttack(MaidAttackEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        String id = event.getSource().getMsgId();
        boolean cancelled = new MaidAttackEventJS(event).post(scriptType, "maid.maidAttack", id);
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidDamage(MaidDamageEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        String id = event.getSource().getMsgId();
        boolean cancelled = new MaidDamageEventJS(event).post(scriptType, "maid.maidDamage", id);
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidDeath(MaidDeathEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        String id = event.getSource().getMsgId();
        boolean cancelled = new MaidDeathEventJS(event).post(scriptType, "maid.maidDeath", id);
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidEquip(MaidEquipEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        Item item = event.getStack().getItem();
        new MaidEquipEventJS(event).post(scriptType, "maid.maidEquip", net.minecraft.core.Registry.ITEM.getKey(item).toString());
    }

    public void maidFish(MaidFishedEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        new MaidFishedEventJS(event).post(scriptType, "maid.maidFished");
    }

    public void maidHurt(MaidHurtEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        String id = event.getSource().getMsgId();
        boolean cancelled = new MaidHurtEventJS(event).post(scriptType, "maid.maidHurt", id);
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidPickupItemResultPre(MaidPickupEvent.ItemResultPre event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        boolean cancelled = new MaidPickupEventJS.ItemResultPre(event).post(scriptType, "maid.maidPickupItemResultPre");
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidPickupItemResultPost(MaidPickupEvent.ItemResultPost event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        new MaidPickupEventJS.ItemResultPost(event).post(scriptType, "maid.maidPickupItemResultPost");
    }

    public void maidPickupExperienceResult(MaidPickupEvent.ExperienceResult event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        boolean cancelled = new MaidPickupEventJS.ExperienceResult(event).post(scriptType, "maid.maidPickupExperienceResult");
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidPickupArrowResult(MaidPickupEvent.ArrowResult event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        boolean cancelled = new MaidPickupEventJS.ArrowResult(event).post(scriptType, "maid.maidPickupArrowResult");
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidPickupPowerPointResult(MaidPickupEvent.PowerPointResult event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        boolean cancelled = new MaidPickupEventJS.PowerPointResult(event).post(scriptType, "maid.maidPickupPowerPointResult");
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidPlaySound(MaidPlaySoundEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        boolean cancelled = new MaidPlaySoundEventJS(event).post(scriptType, "maid.maidPlaySound");
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidTick(MaidTickEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        boolean cancelled = new MaidTickEventJS(event).post(scriptType, "maid.maidTick");
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidTaskEnable(MaidTaskEnableEvent event) {
        ScriptType scriptType = event.getEntityMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        ResourceLocation uid = event.getTargetTask().getUid();
        boolean cancelled = new MaidTaskEnableEventJS(event).post(scriptType, "maid.maidTaskEnable", uid.toString());
        if (cancelled) {
            event.setCanceled(true);
        }
    }

    public void maidTamed(MaidTamedEvent event) {
        ScriptType scriptType = event.getMaid().level.isClientSide ? ScriptType.CLIENT : ScriptType.SERVER;
        new MaidTamedEventJS(event).post(scriptType, "maid.maidTamed");
    }
}
