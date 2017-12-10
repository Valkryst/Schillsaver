package misc;

import lombok.Getter;

public enum FrameRate {
    FPS30(30),
    FPS60(60);

    /** The frame rate. */
    @Getter private final int frameRate;

    /**
     * Constructs a new FrameRate enum.
     *
     * @param frameRate
     *          The frame rate.
     */
    FrameRate(final int frameRate) {
        this.frameRate = frameRate;
    }

    /**
     * Returns the FrameRate that matches the specified frame rate.
     *
     * @param frameRate
     *          The frame rate to check for.
     *
     * @return
     *          The matching FrameRate, or null if no FrameRate match the given
     *          frame rate.
     */
    public static FrameRate getFrameRate(final int frameRate) {
        if (frameRate == 30) {
            return FrameRate.FPS30;
        }

        if (frameRate == 60) {
            return FrameRate.FPS60;
        }

        return null;
    }
}
