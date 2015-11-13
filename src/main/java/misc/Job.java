package misc;

import java.io.File;
import java.util.List;

public class Job {
    /** The unique id of the Job. */
    private int id;
    /** The name of the Job. */
    private String name;
    /** A rough description of the Job. */
    private String description;
    /** The directory in which to place the output file(s). */
    private String outputDirectory;
    /** The file(s) belonging to the Job.*/
    private List<File> files;
    /** Whether or not the Job is an Encode Job. If not, then it's a Decode Job. */
    private boolean isEncodeJob;

    /** Whether or not to pack all of the currently selected files into a single archive before encoding. */
    private boolean combineAllFilesIntoSingleArchive = false;
    /** Whether or not to pack every handler into it's own individual archive before encoding each handler individually. */
    private boolean combineIntoIndividualArchives = false;

    /**
     * Constructs a new Job with the specified parameters.
     * @param name The name of the Job.
     * @param description A rough description of the Job.
     * @param outputDirectory The directory in which to place the output file(s).
     * @param files The file(s) belonging to the Job.
     * @param isEncodeJob Whether or not the Job is an Encode Job. If not, then it's a Decode Job.
     * @param combineAllFilesIntoSingleArchive Whether or not to pack all of the currently selected files into a single archive before encoding.
     * @param combineIntoIndividualArchives Whether or not to pack every handler into it's own individual archive before encoding each handler individually.
     */
    public Job(final String name, final String description, final String outputDirectory, final List<File> files, final boolean isEncodeJob, final boolean combineAllFilesIntoSingleArchive, final boolean combineIntoIndividualArchives) {
        this.name = name;
        this.description = description;

        if(outputDirectory.endsWith("\\") || outputDirectory.endsWith("/")) {
            this.outputDirectory = outputDirectory;
        } else {
            this.outputDirectory = outputDirectory + "/";
        }

        this.files = files;
        this.isEncodeJob = isEncodeJob;
        this.combineAllFilesIntoSingleArchive = combineAllFilesIntoSingleArchive;
        this.combineIntoIndividualArchives = combineIntoIndividualArchives;
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
