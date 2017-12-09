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
     * Returns the FrameDimensions that matches the specified width/height.
     *
     * @param width
     *          The width to check for.
     *
     * @param height
     *          The height to check for.
     *
     * @return
     *          The matching FrameDimension, or null if no FrameDimensions match
     *          the given width/height.
     */
    public static FrameDimension getFrameDimension(final int width, final int height) {
        if (FrameDimension.P240.width == width && FrameDimension.P240.height == height) {
            return FrameDimension.P240;
        }

        if (FrameDimension.P360.width == width && FrameDimension.P360.height == height) {
            return FrameDimension.P360;
        }

        if (FrameDimension.P480.width == width && FrameDimension.P480.height == height) {
            return FrameDimension.P480;
        }

        if (FrameDimension.P720.width == width && FrameDimension.P720.height == height) {
            return FrameDimension.P720;
        }

        if (FrameDimension.P1080.width == width && FrameDimension.P1080.height == height) {
            return FrameDimension.P1080;
        }

        if (FrameDimension.P1440.width == width && FrameDimension.P1440.height == height) {
            return FrameDimension.P1440;
        }

        if (FrameDimension.P2160.width == width && FrameDimension.P2160.height == height) {
            return FrameDimension.P2160;
        }

        return null;
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
