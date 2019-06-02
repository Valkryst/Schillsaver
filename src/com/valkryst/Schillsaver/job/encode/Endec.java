package com.valkryst.Schillsaver.job.encode;

import com.valkryst.Schillsaver.mvc.controller.MainController;
import lombok.NonNull;

import java.util.List;

public abstract class Endec{
    /**
     * Prepares all of the encoding jobs.
     *
     * @param controller
     *          The controller.
     *
     * @return
     *          The prepared jobs.
     *
     * @throws NullPointerException
     *          If the job or controller is null.
     */
    public abstract List<Thread> prepareEncodingJobs(final @NonNull MainController controller);

    /**
     * Prepares all of the decoding jobs.
     *
     * @param controller
     *          The controller.
     *
     * @return
     *          The prepared jobs.
     *
     * @throws NullPointerException
     *          If the job or controller is null.
     */
    public abstract List<Thread> prepareDecodingJobs(final @NonNull MainController controller);
}
