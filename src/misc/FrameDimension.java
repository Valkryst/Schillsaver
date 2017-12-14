package misc;

import lombok.Getter;

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
}
