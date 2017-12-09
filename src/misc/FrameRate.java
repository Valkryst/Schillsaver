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

    /**
     * Returns the FrameRate that matches the specified Rational.
     *
     * @param frameRate
     *          The Rational frame rate to check for.
     *
     * @return
     *          The matching FrameRate, or null if no FrameRate match the given
     *          Rational.
     */
    public static FrameRate getFrameRate(final Rational frameRate) {
        if (FrameRate.FPS30.getFrameRate().getNumerator() == frameRate.getNumerator()) {
            if (FrameRate.FPS30.getFrameRate().getDenominator() == frameRate.getDenominator()) {
                return FrameRate.FPS30;
            }
        }

        if (FrameRate.FPS60.getFrameRate().getNumerator() == frameRate.getNumerator()) {
            if (FrameRate.FPS60.getFrameRate().getDenominator() == frameRate.getDenominator()) {
                return FrameRate.FPS60;
            }
        }

        return null;
    }
}
