package misc;

import io.humble.video.Rational;
import lombok.Getter;

public enum FrameRate {
    FPS30(30),
    FPS60(60);

    /** The frame rate. */
    @Getter private final Rational frameRate;

    /**
     * Constructs a new FrameRate enum.
     *
     * @param frameRate
     *          The frame rate.
     */
    FrameRate(final int frameRate) {
        this.frameRate = Rational.make(1, frameRate);
    }
}
