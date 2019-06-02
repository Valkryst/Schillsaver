package com.valkryst.Schillsaver.job.encode;

import com.valkryst.Schillsaver.job.Cache;
import lombok.NonNull;

public class EndecFactory {
    /** The cache of created endecs. */
    private final static Cache<EndecType, Endec> CACHE = new Cache<>();

    // Ensure no instance of the factory can be created.
    private EndecFactory() {}

    /**
     * Creates a new endec.
     *
     * @param type
     *          The type.
     *
     * @return
     *          The endec.
     *
     * @throws NullPointerException
     *          If the type is null.
     */
    public static Endec create(final @NonNull EndecType type) {
        if (CACHE.contains(type)) {
            return CACHE.retrieve(type);
        }

        switch (type) {
            case FFMPEG: {
                final FFMPEGEndec endec = new FFMPEGEndec();
                CACHE.add(type, endec);
                return endec;
            }
            default: {
                throw new UnsupportedOperationException("The '" + type.name() + "' endec is not supported.");
            }
        }
    }
}
