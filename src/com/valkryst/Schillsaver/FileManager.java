package com.valkryst.Schillsaver;

import lombok.NonNull;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

public class FileManager {
    // Prevent instances from being created.
    private FileManager() {}

    /**
     * Deserializes an object from a file, if the file exists.
     *
     * If the file causes an exception, then the file is deleted.
     *
     * @param filePath
     *          The path to the file.
     *
     * @return
     *          The deserialized object.
     *
     * @throws NullPointerException
     *          If the filePath is null.
     *
     * @throws IOException
     *          If an IO error occurs.
     *
     * @throws ClassNotFoundException
     *          If the class of the serialized object cannot be found.
     */
    public static Object deserializeObject(final @NonNull String filePath) throws IOException, ClassNotFoundException {
        final FileInputStream fis = new FileInputStream(filePath);
        final ObjectInputStream ois = new ObjectInputStream(fis);

        final Object object = ois.readObject();

        ois.close();
        fis.close();

        return object;
    }

    /**
     * Serializes an object to a file. Overwrites the file if it already exists.
     *
     * @param filePath
     *          The path to the file.
     *
     * @param object
     *          The object to serialize.
     *
     * @throws NullPointerException
     *          If the filePath is null.
     *
     * @throws IOException
     *          If an IO error occurs.
     */
    public static void serializeObject(final @NonNull String filePath, final @NonNull Object object) throws IOException {
        final FileOutputStream fos = new FileOutputStream(filePath, false);
        final ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(object);

        oos.flush();
        fos.flush();

        oos.close();
        fos.close();
    }

    /**
     * Deserializes an object from a file using GZIP to decompress the file, if the file exists.
     *
     * If the file causes an exception, then the file is deleted.
     *
     * If the file causes a ZipException, then an attempt will be made to read the file without decompressing
     * it with GZIP as this exception can be caused by reading an object that wasn't written using the
     * serializeObjectWithGZIP function.
     *
     * @param filePath
     *          The path to the file.
     *
     * @return
     *          The deserialized object.
     *
     * @throws NullPointerException
     *          If the filePath is null.
     *
     * @throws IOException
     *          If an IO error occurs.
     *
     * @throws ClassNotFoundException
     *          If the class of the serialized object cannot be found.
     */
    public static Object deserializeObjectWithGZIP(final @NonNull String filePath) throws IOException, ClassNotFoundException {
        try {
            final FileInputStream fis = new FileInputStream(filePath);
            final GZIPInputStream gis = new GZIPInputStream(fis);
            final ObjectInputStream ois = new ObjectInputStream(gis);

            final Object object = ois.readObject();

            ois.close();
            gis.close();
            fis.close();

            return object;
        } catch (final ZipException e) {
            return deserializeObject(filePath);
        }
    }

    /**
     * Serializes an object to a file and compresses it with GZIP.
     * Overwrites the file if it already exists.
     *
     * @param filePath
     *          The path to the file.
     *
     * @param object
     *          The object to serialize.
     *
     * @throws NullPointerException
     *          If the filePath is null.
     *
     * @throws IOException
     *          If an IO error occurs.
     */
    public static void serializeObjectWithGZIP(final @NonNull String filePath, final @NonNull Object object) throws IOException {
        final FileOutputStream fos = new FileOutputStream(filePath, false);
        final GZIPOutputStream gos = new GZIPOutputStream(fos);
        final ObjectOutputStream oos = new ObjectOutputStream(gos);

        oos.writeObject(object);

        oos.flush();
        gos.flush();
        fos.flush();

        oos.close();
        gos.close();
        fos.close();
    }
}
