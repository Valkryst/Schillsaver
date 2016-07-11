package handler;

import eu.hansolo.enzo.notification.Notification;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class StatisticsHandler {
    /** The number of bytes encoded, per second, across all recorded encode Jobs. */
    @Getter private long bytesEncodedPerSecond;
    /** The number of bytes decoded, per second, across all recorded decode Jobs. */
    @Getter private long bytesDecodedPerSecond;

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
     * en/decoded per second variables.
     */
    private void processStatistcsFiles() {
        final File file_encode = new File("statistics_encode.txt");
        final File file_dedode = new File("statistics_decode.txt");

        long bytesEncodedPerSecondTotal = 0;
        long bytesDecodedPerSecondTotal = 0;

        long totalEncodeRecords = 0;
        long totalDecodeRecords = 0;

        // Load the total amount of bytes encoded per second from all
        // records of the statistics_encode.txt file.
        if(file_encode.exists()) {
            try {
                final Scanner sc = new Scanner(new FileInputStream(file_encode));

                while(sc.hasNextLong()) {
                    bytesEncodedPerSecondTotal += sc.nextLong();
                    totalEncodeRecords++;
                }
            } catch(final FileNotFoundException e) {
                final String error = "Could not locate the statistics_encode.txt file.";

                final Logger logger = LogManager.getLogger();
                logger.error(error);

                Notification.Notifier.INSTANCE.notifyError("Error", error);
            }
        }

        // Load the total amount of bytes decoded per second from all
        // records of the statistics_decode.txt file.
        if(file_dedode.exists()) {
            try {
                final Scanner sc = new Scanner(new FileInputStream(file_dedode));

                while(sc.hasNextLong()) {
                    bytesDecodedPerSecondTotal += sc.nextLong();
                    totalDecodeRecords++;
                }
            } catch(final FileNotFoundException e) {
                final String error = "Could not locate the statistics_decode.txt file.";

                final Logger logger = LogManager.getLogger();
                logger.error(error);

                Notification.Notifier.INSTANCE.notifyError("Error", error);
            }
        }

        // Calculate the average bytes en/decoded per second
        // from all acquired data.
        if(totalEncodeRecords > 0) {
            bytesEncodedPerSecond = (bytesEncodedPerSecondTotal / totalEncodeRecords);
        }

        if(totalDecodeRecords > 0 ) {
            bytesDecodedPerSecond = (bytesDecodedPerSecondTotal / totalDecodeRecords);
        }
    }

    /**
     * Calculates the amount of bytes, per second, that the specified file was processed
     * at.
     * If the processing speed was too fast, then 0 is returned.
     * @param file The file that was processed.
     * @param startTime The time, in milliseconds, that processing began.
     * @param endTime The time, in milliseconds, that processing completed.
     * @return The amount of bytes, per second, that the specified file was processed at.
     */
    public long calculateProcessingSpeed(final File file, final long startTime, final long endTime) {
        try {
            long duration = endTime - startTime; // The total time that the Job ran for, in milliseconds.
            duration /= 1000; // The total time that the Job ran for, in seconds.

            long speed = file.length() / duration; // The bytes per millisecond that were en/decoded.
            return speed;
        } catch(final ArithmeticException e) {
            return 0;
        }
    }

    /**
     * Writes the specified data to either the encode, or decode, statistics
     * file.
     * @param isEncodeJob Whether or not the data is from an encode or decode Job.
     * @param bytesPerSecond The bytes per second to write to the file.
     */
    public void recordData(final boolean isEncodeJob, final long bytesPerSecond) {
        // Prepare the output file:
        final File outputFile;

        if(isEncodeJob) {
            outputFile = new File("statistics_encode.txt");
        } else {
            outputFile = new File("statistics_decode.txt");
        }

        // Create the file if it does not exist.
        if(! outputFile.exists()) {
            try {
                if(! outputFile.createNewFile()) {
                    final String error = "Unable to create " + outputFile.getAbsolutePath() + ".";

                    final Logger logger = LogManager.getLogger();
                    logger.error(error);

                    Notification.Notifier.INSTANCE.notifyError("Error", error);
                }
            } catch(final IOException e) {
                final Logger logger = LogManager.getLogger();
                logger.error(e);

                Notification.Notifier.INSTANCE.notifyError("IOException", "Please view the log file.");
            }
        }

        // Append data to the output file.
        try {
            final PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
            printWriter.append(String.valueOf(bytesPerSecond));
            printWriter.append(System.lineSeparator());
            printWriter.close();
        } catch(final IOException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            Notification.Notifier.INSTANCE.notifyError("IOException", "Please view the log file.");
        }
    }

    /**
     * Estimates the time it will take for a Job, with the specified files, to
     * either encode, or decode, based on previous data.
     * @param isEncodeJob Whether of not the Job to be run is an encode, or decode, Job.
     * @param files The files to be processed.
     * @return The amount of time, in seconds, that the Job may take.
     */
    public long estimateProcessingDuration(final boolean isEncodeJob, final List<File> files) {
        long estimation = 0;

        for(final File f : files) {
            estimation += f.length();
        }

        estimation /= (isEncodeJob ? bytesEncodedPerSecond : bytesDecodedPerSecond);
        return estimation;
    }
}
