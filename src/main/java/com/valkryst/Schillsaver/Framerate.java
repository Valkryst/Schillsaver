package Schillsaver;

public enum Framerate {
    FPS_30(30),
    FPS_60(60),
    FPS_120(120);

    /** Frame rate. */
    public final int framerate;

    /**
     * Constructs a new {@code Framerate} enum.
     *
     * @param framerate The frame rate.
     */
    Framerate(final int framerate) {
        this.framerate = framerate;
    }

    @Override
    public String toString() {
        return String.format("%d FPS", framerate);
    }
}
