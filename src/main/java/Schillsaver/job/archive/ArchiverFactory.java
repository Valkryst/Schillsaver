package Schillsaver.job.archive;

import lombok.NonNull;

import java.util.HashMap;

public class ArchiverFactory {
    /** The cache of created archivers. */
    private final static HashMap<ArchiveType, Archiver> CACHE = new HashMap<>();

    // Ensure no instance of the factory can be created.
    private ArchiverFactory() {}

    /**
     * Creates a new archiver.
     *
     * @param type
     *          The type.
     *
     * @return
     *          The archiver.
     *
     * @throws NullPointerException
     *          If the type is null.
     */
    public static Archiver create(final @NonNull ArchiveType type) {
        if (CACHE.containsKey(type)) {
            return CACHE.get(type);
        }

        switch (type) {
            case ZIP: {
                final ZipArchiver archiver = new ZipArchiver();
                CACHE.put(type, archiver);
                return archiver;
            }
            default: {
                throw new UnsupportedOperationException("The '" + type.name() + "' archiver is not supported.");
            }
        }
    }
}
