package com.valkryst.Schillsaver.job.archive;

import lombok.NonNull;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchiver extends Archiver implements Serializable {
    private static final long serialVersionUID = 0;

    /** The archive file extension. */
    private final static String EXTENSION = ".zip";

    @Override
    public File archive(final @NonNull String fileName, final List<File> files) throws IOException {
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("The file name cannot be empty.");
        }

        if (files.size() == 0) {
            throw new IllegalArgumentException("There must be at least one file to archive.");
        }

        final File zipFile = new File(fileName + EXTENSION);

        final var fos = new FileOutputStream(zipFile);
        final var zos = new ZipOutputStream(fos);

        byte[] buffer = new byte[32_768];

        for (final File file : files) {
            if (! file.isDirectory()) {
                final var entry = new ZipEntry(file.getName());
                final var fis = new FileInputStream(file);

                zos.putNextEntry(entry);

                int read;
                while ((read = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, read);
                }

                zos.closeEntry();
                fis.close();
            }
        }

        zos.flush();
        fos.flush();

        zos.close();
        fos.close();

        return super.padFile(zipFile);
    }

    @Override
    public String getFileName(final String fileName) {
        return fileName + EXTENSION;
    }
}
