package gui;

import java.io.File;

public class MainScreenModel {
    /** The array of all files to be encoded. */
    private File[] selectedFiles = new File[0];

    /** @return An array of all files to be encoded. */
    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    // todo JavaDoc
    public void setSelectedFiles(final File[] selectedFiles) {
        this.selectedFiles = selectedFiles;
    }
}
