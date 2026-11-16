package com.github.tartaricacid.touhoulittlemaid.data;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class MaidNumAttachment implements Component {

    private int num;

    public MaidNumAttachment(int num) {
        this.num = num;
    }

    public boolean canAdd() {
        return this.num + 1 <= getMaxNum();
    }

    public void add() {
        this.add(1);
    }

    public void add(int num) {
        if (num + this.num <= getMaxNum()) {
            this.num += num;
        } else {
            this.num = getMaxNum();
        }
    }

    public void min(int num) {
        if (num <= this.num) {
            this.num -= num;
        } else {
            this.num = 0;
        }
    }

    public void set(int num) {
        this.num = Mth.clamp(num, 0, getMaxNum());
    }

    public int getMaxNum() {
        return MaidConfig.OWNER_MAX_MAID_NUM.get();
    }

    public int get() {
        return this.num;
    }

    public void copyFrom(MaidNumAttachment other) {
        this.num = other.num;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.num = tag.getInt("num");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("num", this.num);
    }
}
