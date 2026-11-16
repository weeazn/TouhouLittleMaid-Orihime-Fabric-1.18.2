package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.ReportedException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.TranslatableComponent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAndItemTransformEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SpawnParticleMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemFilm extends AbstractStoreMaidItem {
    private static final String ID_TAG = "id";

    public ItemFilm() {
        super((new Properties()).stacksTo(1));
    }

    public static ItemStack maidToFilm(EntityMaid maid) {
        ItemStack film = InitItems.FILM.getDefaultInstance();
        CompoundTag filmTag = new CompoundTag();
        CompoundTag maidTag = new CompoundTag();
        maid.setHomeModeEnable(false);
        maid.saveWithoutId(maidTag);
        removeMaidSomeData(maidTag);
        maidTag.putString(ID_TAG, Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(InitEntities.MAID)).toString());

        var event = new MaidAndItemTransformEvent.ToItem(maid, film, maidTag);
        MaidAndItemTransformEvent.TO_ITEM.invoker().onToItem(event);

        filmTag.put(MAID_INFO, maidTag);
        film.setTag(filmTag);
        return film;
    }

    public static void filmToMaid(ItemStack film, Level world, BlockPos pos, Player player) {
        if (world instanceof ServerLevel server && reviveAtShrine(film, server, pos, player)) {
            film.shrink(1);
        }
    }

    public static boolean reviveAtShrine(ItemStack film, ServerLevel world, BlockPos pos, Player player) {
        EntityMaid maid = new EntityMaid(world);
        if (!restore(film, maid, world, pos, player)) {
            return false;
        }
        NetworkHandler.sendToNearby(maid, SpawnParticleMessage.ID, SpawnParticleMessage.encode(maid.getId(), SpawnParticleMessage.Type.EXPLOSION));
        world.playSound(null, pos, InitSounds.ALTAR_CRAFT, SoundSource.VOICE, 1.0f, 1.0f);
        return true;
    }

    private static boolean hasRecord(CompoundTag data) {
        return Registry.ENTITY_TYPE.getKey(InitEntities.MAID).equals(ResourceLocation.tryParse(data.getString(ID_TAG)))
                && (data.hasUUID("UUID") || data.hasUUID("Owner") || data.contains(EntityMaid.MODEL_ID_TAG, Tag.TAG_STRING));
    }

    public static boolean restore(ItemStack film, EntityMaid maid, ServerLevel world, BlockPos pos, @Nullable Player player) {
        CompoundTag data = getMaidData(film).copy();
        if (!hasRecord(data)) {
            return fail(player, "tooltips.touhou_little_maid.film.no_data.desc");
        }
        var event = new MaidAndItemTransformEvent.ToMaid(maid, film, data);
        MaidAndItemTransformEvent.TO_MAID.invoker().onToMaid(event);
        if (data.hasUUID("UUID")) {
            for (ServerLevel dimension : world.getServer().getAllLevels()) {
                if (dimension.getEntity(data.getUUID("UUID")) != null) {
                    return fail(player, "message.touhou_little_maid.film.revive_failed");
                }
            }
        }
        // Films omit position, motion and health. Start with a living entity at the resurrection site.
        maid.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, maid.getYRot(), maid.getXRot());
        try {
            maid.load(maid.saveWithoutId(new CompoundTag()).merge(data));
        } catch (ReportedException error) {
            TouhouLittleMaid.LOGGER.warn("Could not restore the maid recorded on a film", error);
            return fail(player, "message.touhou_little_maid.film.revive_failed");
        }
        maid.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, maid.getYRot(), maid.getXRot());
        maid.setHealth(maid.getMaxHealth());
        if (!world.addFreshEntity(maid)) {
            return fail(player, "message.touhou_little_maid.film.revive_failed");
        }
        return true;
    }

    private static boolean fail(@Nullable Player player, String key) {
        if (player != null) {
            player.displayClientMessage(new TranslatableComponent(key), false);
        }
        return false;
    }

    private static void removeMaidSomeData(CompoundTag nbt) {
        nbt.remove(EntityMaid.MAID_BACKPACK_TYPE);
        nbt.remove(EntityMaid.MAID_INVENTORY_TAG);
        nbt.remove(EntityMaid.MAID_BAUBLE_INVENTORY_TAG);
        nbt.remove(EntityMaid.EXPERIENCE_TAG);
        nbt.remove("ArmorItems");
        nbt.remove("HandItems");
        nbt.remove("Leash");
        nbt.remove("Health");
        nbt.remove("HurtTime");
        nbt.remove("DeathTime");
        nbt.remove("HurtByTimestamp");
        nbt.remove("Pos");
        nbt.remove("Motion");
        nbt.remove("FallDistance");
        nbt.remove("Fire");
        nbt.remove("Air");
        nbt.remove("TicksFrozen");
        nbt.remove("HasVisualFire");
        nbt.remove("Passengers");
        nbt.remove("ActiveEffects");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag data = getMaidData(stack);
        if (!hasRecord(data)) {
            tooltip.add(new TranslatableComponent("tooltips.touhou_little_maid.film.no_data.desc").withStyle(ChatFormatting.DARK_RED));
            return;
        }
        if (data.contains(CUSTOM_NAME, Tag.TAG_STRING)) {
            Component name = readStoredName(data.getString(CUSTOM_NAME));
            if (name != null) {
                tooltip.add(new TranslatableComponent("tooltips.touhou_little_maid.film.name", name));
            }
        }
        if (data.contains(EntityMaid.MODEL_ID_TAG, Tag.TAG_STRING)) {
            tooltip.add(new TranslatableComponent("tooltips.touhou_little_maid.film.model", data.getString(EntityMaid.MODEL_ID_TAG)));
        }
        if (data.contains(EntityMaid.FAVORABILITY_TAG, Tag.TAG_ANY_NUMERIC)) {
            tooltip.add(new TranslatableComponent("tooltips.touhou_little_maid.film.favor", data.getInt(EntityMaid.FAVORABILITY_TAG)));
        }
        tooltip.add(new TranslatableComponent("tooltips.touhou_little_maid.film.shrine").withStyle(ChatFormatting.GRAY));
    }
}
