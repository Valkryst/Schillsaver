package Schillsaver.job.encode;

import lombok.NonNull;

import java.util.HashMap;

public class EndecFactory {
    /** The cache of created endecs. */
    private final static HashMap<EndecType, Endec> CACHE = new HashMap<>();

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
        if (CACHE.containsKey(type)) {
            return CACHE.get(type);
        }

        switch (type) {
            case FFMPEG: {
                final FFMPEGEndec endec = new FFMPEGEndec();
                CACHE.put(type, endec);
                return endec;
            }
            default: {
                throw new UnsupportedOperationException("The '" + type.name() + "' endec is not supported.");
            }
        }
    }
}
