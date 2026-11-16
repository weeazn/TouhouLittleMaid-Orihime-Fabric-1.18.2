package com.github.tartaricacid.touhoulittlemaid.client.sound.data;

import com.github.tartaricacid.touhoulittlemaid.client.sound.CustomSoundLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;

public class MaidSoundInstance extends AbstractTickableSoundInstance {
    private final String id;
    @Nullable private final EntityMaid maid;
    private final boolean testSound;

    public MaidSoundInstance(SoundEvent soundEvent, String id, EntityMaid maid) {
        this(soundEvent, id, maid, false);
    }

    public MaidSoundInstance(SoundEvent soundEvent, String id, EntityMaid maid, boolean testSound) {
        super(soundEvent, SoundSource.NEUTRAL);
        this.id = id;
        this.maid = maid;
        this.testSound = testSound;
        this.x = this.maid.getX();
        this.y = this.maid.getY();
        this.z = this.maid.getZ();
    }

    public MaidSoundInstance(SoundEvent event, String id, double x, double y, double z, float volume, float pitch) {
        super(event, SoundSource.NEUTRAL);
        this.id = id;
        this.maid = null;
        this.testSound = false;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public boolean canPlaySound() {
        return this.maid == null || !this.maid.isSilent();
    }

    @Override
    public void tick() {
        if (this.maid == null) return;
        if (this.maid.isRemoved()) {
            this.stop();
        } else {
            this.x = this.maid.getX();
            this.y = this.maid.getY();
            this.z = this.maid.getZ();
        }
    }

    public String getId() {
        return id;
    }

    @Nullable
    public EntityMaid getMaid() {
        return maid;
    }

    public boolean isTestSound() {
        return testSound;
    }

    @Nullable
    public SoundBuffer getSoundBuffer() {
        SoundCache soundCache = CustomSoundLoader.getSoundCache(id);
        if (soundCache != null) {
            return soundCache.getBuffer(location);
        }
        return null;
    }
}
