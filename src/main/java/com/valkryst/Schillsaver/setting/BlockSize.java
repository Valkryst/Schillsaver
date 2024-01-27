package com.valkryst.Schillsaver.setting;

public enum BlockSize {
    S6(6),
    S8(8),
    S10(10);

    /** The block size. */
    public final int blockSize;

    /** Number of pixels in a block. */
    public final long pixelCount;

    /**
     * Constructs a new BlockSize enum.
     *
     * @param blockSize
     *          The the block size.
     *
     * @throws IllegalArgumentException
     *          If the size is less than one.
     */
    BlockSize(final int blockSize) {
        if (blockSize < 1) {
            throw new IllegalArgumentException("The block size cannot be < 1.");
        }

        this.blockSize = blockSize;
        this.pixelCount = (long) blockSize * blockSize;
    }
}
