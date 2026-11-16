package com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.event.IEventHandler;

public class MaidEventsJS {
    private final ScriptManager manager;

    public MaidEventsJS(ScriptManager manager) { this.manager = manager; }

    public void listen(String event, IEventHandler handler) {
        manager.events.listen("maid." + event, handler);
    }

    public void addJadeInfo(IEventHandler handler) {
        listen("addJadeInfo", handler);
    }

    public void addTopInfo(IEventHandler handler) {
        listen("addTopInfo", handler);
    }

    public void interactMaid(IEventHandler handler) {
        listen("interactMaid", handler);
    }

    public void interactMaid(String id, IEventHandler handler) {
        listen("interactMaid." + id, handler);
    }

    public void maidAfterEat(IEventHandler handler) {
        listen("maidAfterEat", handler);
    }

    public void maidAfterEat(String id, IEventHandler handler) {
        listen("maidAfterEat." + id, handler);
    }

    public void maidAttack(IEventHandler handler) {
        listen("maidAttack", handler);
    }

    public void maidAttack(String id, IEventHandler handler) {
        listen("maidAttack." + id, handler);
    }

    public void maidDamage(IEventHandler handler) {
        listen("maidDamage", handler);
    }

    public void maidDamage(String id, IEventHandler handler) {
        listen("maidDamage." + id, handler);
    }

    public void maidDeath(IEventHandler handler) {
        listen("maidDeath", handler);
    }

    public void maidDeath(String id, IEventHandler handler) {
        listen("maidDeath." + id, handler);
    }

    public void maidEquip(IEventHandler handler) {
        listen("maidEquip", handler);
    }

    public void maidEquip(String id, IEventHandler handler) {
        listen("maidEquip." + id, handler);
    }

    public void maidFished(IEventHandler handler) {
        listen("maidFished", handler);
    }

    public void maidHurt(IEventHandler handler) {
        listen("maidHurt", handler);
    }

    public void maidHurt(String id, IEventHandler handler) {
        listen("maidHurt." + id, handler);
    }

    public void maidPickupItemResultPre(IEventHandler handler) {
        listen("maidPickupItemResultPre", handler);
    }

    public void maidPickupItemResultPost(IEventHandler handler) {
        listen("maidPickupItemResultPost", handler);
    }

    public void maidPickupExperienceResult(IEventHandler handler) {
        listen("maidPickupExperienceResult", handler);
    }

    public void maidPickupArrowResult(IEventHandler handler) {
        listen("maidPickupArrowResult", handler);
    }

    public void maidPickupPowerPointResult(IEventHandler handler) {
        listen("maidPickupPowerPointResult", handler);
    }

    public void maidPlaySound(IEventHandler handler) {
        listen("maidPlaySound", handler);
    }

    public void maidTick(IEventHandler handler) {
        listen("maidTick", handler);
    }

    public void maidTaskEnable(IEventHandler handler) {
        listen("maidTaskEnable", handler);
    }

    public void maidTaskEnable(String id, IEventHandler handler) {
        listen("maidTaskEnable." + id, handler);
    }

    public void maidTamed(IEventHandler handler) {
        listen("maidTamed", handler);
    }
}
