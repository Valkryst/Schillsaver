package com.valkryst.Schillsaver.setting;

public enum FrameRate {
    FPS_30(30),
    FPS_60(60),
    FPS_120(120);

    /** The frame rate. */
    public final int frameRate;

    /**
     * Constructs a new FrameRate enum.
     *
     * @param frameRate
     *          The frame rate.
     *
     * @throws IllegalArgumentException
     *          If the frameRate is less than one.
     */
    FrameRate(final int frameRate) {
        if (frameRate < 1) {
            throw new IllegalArgumentException("The frame rate cannot be < 1.");
        }

        this.frameRate = frameRate;
    }

    @Override
    public String toString() {
        return String.format("%d FPS", frameRate);
    }
}
