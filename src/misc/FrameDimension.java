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

    /** The frame dimensions. */
    @Getter private final Dimension dimensions;

    /** The number of bits that can be stored in each frame. */
    @Getter private final int bitsPerFrame;

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
        dimensions = new Dimension(width, height);
        bitsPerFrame = (width * height) / 8;
    }
}
