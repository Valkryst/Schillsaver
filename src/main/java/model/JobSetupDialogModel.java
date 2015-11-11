package model;

import misc.Job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JobSetupDialogModel {
    /** The Job being created by the JobSetupDialog. */
    private Job job = null;
    /** The list of all list_files to be added to the Job. */
    private List<File> list_files = new ArrayList<>();

    /** @return The Job being created by the JobSetupDialog. */
    public Job getJob() {
        return job;
    }

    /** @return A list of all list_files to be added to the Job. */
    public List<File> getList_files() {
        return list_files;
    }

    /** @param job The new Job being created by the JobSetupDialog. */
    public void setJob(final Job job) {
        this.job = job;
    }
}
