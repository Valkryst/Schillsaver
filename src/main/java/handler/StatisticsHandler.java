package handler;

import misc.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class StatisticsHandler {
    /** The number of bytes encoded, per minute, across all recorded encode Jobs. */
    private long bytesEncodedPerMinute;
    /** The number of bytes decoded, per minute, across all recorded decode Jobs. */
    private long bytesDecodedPerMinute;

    /**
     * Constructs a new StatisticsHandler and processes all of the
     * existing statistics records.
     */
    public StatisticsHandler() {
        processStatistcsFiles();
    }

    /**
     * Reads in all existing en/decode data from the respective
     * statistics files, averages the data, then sets the bytes
     * en/decoded per minute variables.
     */
    private void processStatistcsFiles() {
        final File file_encode = new File("statistics_encode.txt");
        final File file_dedode = new File("statistics_decode.txt");

        long bytesEncodedPerMinuteTotal = 0;
        long bytesDecodedPerMinuteTotal = 0;

        long totalEncodeRecords = 0;
        long totalDecodeRecords = 0;

        // Load the total amount of bytes encoded per minute from all
        // records of the statistics_encode.txt file.
        if(file_encode.exists()) {
            try {
                final Scanner sc = new Scanner(new FileInputStream(file_encode));

                while(sc.hasNextLong()) {
                    bytesEncodedPerMinuteTotal += sc.nextLong();
                    totalEncodeRecords++;
                }
            } catch(final FileNotFoundException e) {
                Logger.writeLog("Could not locate the statistics_encode.txt file", Logger.LOG_TYPE_VERBOSE);
            }
        }

        // Load the total amount of bytes decoded per minute from all
        // records of the statistics_decode.txt file.
        if(file_dedode.exists()) {
            try {
                final Scanner sc = new Scanner(new FileInputStream(file_dedode));

                while(sc.hasNextLong()) {
                    bytesDecodedPerMinuteTotal += sc.nextLong();
                    totalDecodeRecords++;
                }
            } catch(final FileNotFoundException e) {
                Logger.writeLog("Could not locate the statistics_decode.txt file", Logger.LOG_TYPE_VERBOSE);
            }
        }

        // Calculate the average bytes en/decoded per minute
        // from all acquired data.
        bytesEncodedPerMinute = (bytesEncodedPerMinuteTotal / totalEncodeRecords);
        bytesDecodedPerMinute = (bytesDecodedPerMinuteTotal / totalDecodeRecords);
    }

    /**
     * Calculates the amount of time, in minutes, that it took for the specified
     * file to be processed (encoded or decoded).
     * @param file The file that was processed.
     * @param startTime The time, in milliseconds, that processing began.
     * @param endTime The time, in milliseconds, that processing completed.
     * @return The amount of time, in minutes, that it took for the specified file to be processed.
     */
    public long calculateProcessingSpeed(final File file, final long startTime, final long endTime) {
        final long duration = endTime - startTime; // The total time that the Job ran for.

        long speed = file.length() / duration; // The bytes per millisecond that were en/decoded.
        speed /= 1000; // The bytes per second that were en/decoded.
        speed /= 60; // The bytes per minute that were en/decoded;

        return speed;
    }

    /**
     * Writes the specified data to either the encode, or decode, statistics
     * file.
     * @param isEncodeJob Whether or not the data is from an encode or decode Job.
     * @param bytesPerMinute The bytes per minute to write to the file.
     */
    public void recordData(final boolean isEncodeJob, final long bytesPerMinute) {
        // Prepare the output file:
        final File outputFile;

        if(isEncodeJob) {
            outputFile = new File("statistics_encode.txt");
        } else {
            outputFile = new File("statistics_decode.txt");
        }

        // Create the file if it does not exist.
        if(!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch(final IOException e) {
                Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
            }
        }

        // Append data to the output file.
        try {
            final FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.append(bytesPerMinute + "\n");
            fileWriter.close();
        } catch(final IOException e) {
            Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_ERROR);
        }
    }

    /**
     * Estimates the time it will take for a Job, with the specified files, to
     * either encode, or decode, based on previous data.
     * @param isEncodeJob Whether of not the Job to be run is an encode, or decode, Job.
     * @param files The files to be processed.
     * @return The amount of time, in minutes, that the Job may take.
     */
    public long estimateProcessingDuration(final boolean isEncodeJob, final List<File> files) {
        long estimation = 0;

        for(final File f : files) {
            estimation += f.length();
        }

        estimation /= (isEncodeJob ? bytesEncodedPerMinute : bytesDecodedPerMinute);
        return estimation;
    }
}
