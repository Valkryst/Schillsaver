package com.valkryst.Schillsaver.job.archive;

import com.valkryst.Schillsaver.job.Cache;
import lombok.NonNull;

public class ArchiverFactory {
    /** The cache of created archivers. */
    private final static Cache<ArchiveType, Archiver> CACHE = new Cache<>();

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
        if (CACHE.contains(type)) {
            return CACHE.retrieve(type);
        }

        switch (type) {
            case ZIP: {
                final ZipArchiver archiver = new ZipArchiver();
                CACHE.add(type, archiver);
                return archiver;
            }
            default: {
                throw new UnsupportedOperationException("The '" + type.name() + "' archiver is not supported.");
            }
        }
    }
}
