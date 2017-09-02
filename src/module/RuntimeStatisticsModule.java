package module;

public class RuntimeStatisticsModule {
    /** The time at which the start method was first called. */
    private long startTime;
    /** The time at which the stop method was first called. */
    private long endTime;

    /** Records the current time as the start time. */
    public void recordStart() {
        startTime = System.currentTimeMillis();
    }

    /** Records the current time as the end time. */
    public void recordEnd() {
        endTime = System.currentTimeMillis();
    }

    /**
     * Determines the amount of time that elapsed between
     * the start and end times.
     *
     * @return
     *         The elapsed time.
     */
    public long getElapsedTime() {
        return endTime - startTime;
    }
}
