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
}
