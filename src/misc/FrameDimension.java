package misc;

import lombok.Getter;

import java.awt.Dimension;

public enum FrameDimension {
    P240(426, 240),
    P360(640, 360),
    P480(854, 480),
    P720(1280, 720),
    P1080(1920, 1080),
    P1440(2560, 1440),
    P2160(3840, 2160);

    /** The width of the frame. */
    @Getter private final int width;
    /** The height of the frame. */
    @Getter private final int height;

    /**
     * Construct a new FrameDimension enum.
     *
     * @param width
     *          The frame width.
     *
     * @param height
     *          The frame height.
     */
    FrameDimension(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if the dimensions of the frame are evenly divisible by the dimensions
     * of the blocks.
     *
     * @param blockDimensions
     *          The block dimensions
     *
     * @throws IllegalStateException
     *          If the width of the frame is not evenly divisible by the width of the block.
     *          If the height of the frame is not evenly divisible by the height of the block.
     */
    public void isCompatibleBlockSize(final Dimension blockDimensions) throws IllegalStateException {
        if ((this.getWidth() % blockDimensions.width) != 0) {
            throw new IllegalStateException("The frame height must be evenly divisible by the block height.");
        }

        if ((this.getHeight() % blockDimensions.height) != 0) {
            throw new IllegalStateException("The frame width must be evenly divisible by the block width.");
        }
    }
}
