package cn.sh1rocu.touhoulittlemaid.client;

import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.NoSuchElementException;

/** Divides the pixels evenly over the tiles of a nine-slice texture. */
public final class PixelSlices implements IntIterator {
    private final int count;
    private final int slice;
    private final int remainder;
    private int index;
    private int carry;

    public PixelSlices(int total, int count) {
        this.count = count;
        this.slice = count > 0 ? total / count : 0;
        this.remainder = count > 0 ? total % count : 0;
    }

    @Override
    public boolean hasNext() {
        return index < count;
    }

    @Override
    public int nextInt() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
        carry += remainder;
        if (carry >= count) {
            carry -= count;
            return slice + 1;
        }
        return slice;
    }
}
