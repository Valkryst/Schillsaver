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
     *
     * @throws IllegalArgumentException
     *          If the size is less than one.
     */
    BlockSize(final int size) {
        if (size < 1) {
            throw new IllegalArgumentException("The size cannot be < 1.");
        }

        blockSize = new Dimension(size, size);
    }
}
