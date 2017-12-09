package misc;

import java.awt.Dimension;

public enum BlockSize {
    S1(1),
    S2(2),
    S4(4),
    S6(6),
    S8(8),
    S10(10);

    /** The block size. */
    private final Dimension blockSize;

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
