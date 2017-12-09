package misc;

import lombok.Getter;

import java.awt.Dimension;

public enum BlockSize {
    S6(6),
    S8(8),
    S10(10);

    /** The block size. */
    @Getter private final Dimension blockSize;

    /**
     * Constructs a new BlockSize enum.
     *
     * @param size
     *          The width/height of the block size.
     */
    BlockSize(final int size) {
        blockSize = new Dimension(size, size);
    }
}
