package com.github.tartaricacid.touhoulittlemaid.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class PowerAttachment implements Component {
    public static final float MAX_POWER = 5.0f;

    private float power;

    public PowerAttachment(float power) {
        this.power = power;
    }

    public void add(float points) {
        if (points + this.power <= MAX_POWER) {
            this.power += points;
        } else {
            this.power = MAX_POWER;
        }
    }

    public void min(float points) {
        if (points <= this.power) {
            this.power -= points;
        } else {
            this.power = 0.0f;
        }
    }

    public void set(float points) {
        this.power = Mth.clamp(points, 0, MAX_POWER);
    }

    public float get() {
        return this.power;
    }

    public void copyFrom(PowerAttachment other) {
        this.power = other.power;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.power = tag.getFloat("power");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putFloat("power", this.power);
    }
}
