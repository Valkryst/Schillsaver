package com.valkryst.Schillsaver.setting;

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
}
