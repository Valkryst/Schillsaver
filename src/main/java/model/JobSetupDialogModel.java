package model;

import java.io.File;

public class JobSetupDialogModel {
    /** The array of all files to be added to the Job. */
    private File[] files = new File[0];

    /** @return An array of all files to be added to the Job. */
    public File[] getFiles() {
        return files;
    }

    // todo JavaDoc
    public void setFiles(final File[] files) {
        this.files = files;
    }
}
