package misc;

import java.io.File;
import java.util.List;

public class Job {
    /**
     * Test #1:
     *     60MB -> 126MB
     *     20m
     *     126MB/20m = ~6.3MB/minute
     *
     * Test #2:
     *     3GB -> 8.7GB = 3000MB - > 8700MB
     *     23h25m = 1405m
     *     8700MB/1405m = ~6.2MB/minute
     *
     * So a safe estimate is 6MB/minute.
     * Therefore 6,000,000 bytes/minute are encoded.
     * Therefore 100,000 bytes/second.
     */
    private final static long BYTES_ENCODED_PER_SECOND = 100000;

    /**
     * Test #1:
     *     366MB/2m = 183MB/min
     *     183/60 = 3.05MB/second
     *     3.05MB = 3,050,000 bytes/second
     */
    private final static long BYTES_DECODED_PER_SECOND = 3050000;

    /** The unique id of the Job. */
    private int id;
    /** The name of the Job. */
    private String name;
    /** A rough description of the Job. */
    private String description;
    /** The directory in which to place the output files. */
    private String outputDirectory;
    /** The file(s) belonging to the Job.*/
    private List<File> files;
    /** Whether or not the Job is an Encode Job. If not, then it's a Decode Job. */
    private boolean isEncodeJob;
    /** A rough estimation of the time, in minutes, it will take for the job to run. */
    private long estimatedDurationInMinutes;

    /** Whether or not to pack all of the currently selected files into a single archive before encoding. */
    private boolean combineAllFilesIntoSingleArchive = false;
    /** Whether or not to pack every handler into it's own individual archive before encoding each handler individually. */
    private boolean combineIntoIndividualArchives = false;

    // todo JavaDoc
    public Job(final String name, final String description, final String outputDirectory, final List<File> files, final boolean isEncodeJob, final boolean combineAllFilesIntoSingleArchive, final boolean combineIntoIndividualArchives) {
        this.name = name;
        this.description = description;
        this.outputDirectory = outputDirectory;
        this.files = files;
        this.isEncodeJob = isEncodeJob;
        this.combineAllFilesIntoSingleArchive = combineAllFilesIntoSingleArchive;
        this.combineIntoIndividualArchives = combineIntoIndividualArchives;
    }

    /**
     * Estimates the time required to encode the file(s) of
     * the job and assigns the estimate to estimatedDurationInMinutes.
     */
    private void estimateEncodingDuration() {
        long tempEstimation = 0;

        for(final File f : files) {
            tempEstimation += Math.floor(f.length() / BYTES_ENCODED_PER_SECOND) * 60;
        }

        estimatedDurationInMinutes = tempEstimation;
    }

    /**
     * Estimates the time required to decode the file(s) of
     * the job and assigns the estimate to estimatedDurationInMinutes.
     */
    private void estimateDecodingDuration() {
        long tempEstimation = 0;

        for(final File f : files) {
            tempEstimation += Math.floor(f.length() / BYTES_DECODED_PER_SECOND) * 60;
        }

        estimatedDurationInMinutes = tempEstimation;
    }

    ////////////////////////////////////////////////////////// Getters

    /** @return The full designation of the Job. This includes the unique ID, type, and name. */
    public String getFullDesignation() {
        String s = "";
        s += id;
        s += " - ";
        s += (isEncodeJob ? "Encode" : "Decode");
        s += " - ";
        s += name;
        return s;
    }

    /** @return The unique id of the Job. */
    public int getId() {
        return id;
    }

    /** @return The name of the Job. */
    public String getName() {
        return name;
    }

    /** @return A rough description of the Job. */
    public String getDescription() {
        return description;
    }

    /** @return The directory in which to place the output files.  */
    public String getOutputDirectory() {
        return outputDirectory;
    }

    /** @return The file(s) belonging to the Job.*/
    public List<File> getFiles() {
        return files;
    }

    /** @return Whether or not the Job is an Encode Job. If not, then it's a Decode Job. */
    public boolean getIsEncodeJob() {
        return isEncodeJob;
    }

    /** @return A rough estimation of the time, in minutes, it will take for the job to run. */
    public long getEstimatedDurationInMinutes() {
        if(isEncodeJob) {
            estimateEncodingDuration();
        } else {
            estimateDecodingDuration();
        }

        return estimatedDurationInMinutes;
    }

    /** @return Whether or not to combine all of the input files into a single archive when encoding. */
    public boolean getCombineAllFilesIntoSingleArchive() {
        return combineAllFilesIntoSingleArchive;
    }

    /** @return Whether or not to pack every handler into it's own individual archive before encoding each handler individually. */
    public boolean getCombineIntoIndividualArchives() {
        return combineIntoIndividualArchives;
    }

    ////////////////////////////////////////////////////////// Setters

    // todo JavaDoc
    public void setId(final int id) {
        this.id = id;
    }
}
