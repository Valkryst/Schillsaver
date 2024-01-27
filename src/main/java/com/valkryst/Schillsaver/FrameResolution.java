package com.valkryst.Schillsaver;

/**
 * <p>Represents frame resolutions supported by YouTube.</p>
 *
 * <p>
 *     Supported resolutions were pulled from
 *     <a href="https://support.google.com/youtube/answer/6375112?hl=en&co=GENIE.Platform%3DDesktop">here</a>.
 * </p>
 */
public enum FrameResolution {
    P240(426, 240),
    P360(640, 360),
    P480(854, 480),
    P720(1280, 720),
    P1080(1920, 1080),
    P1440(2560, 1440),
    P2160(3840, 2160),
    P4320(7680, 4320);

    /** Frame width. */
    public final int width;

    /** Frame height. */
    public final int height;

    /** Number of pixels in a frame. */
    public final long pixelCount;

    /**
     * Constructs a new {@code FrameResolution} enum.
     *
     * @param width The frame width.
     * @param height The frame height.
     */
    FrameResolution(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.pixelCount = (long) width * height;
    }

    @Override
    public String toString() {
        return String.format("%dx%d", width, height);
    }
}
