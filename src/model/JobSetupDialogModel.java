package model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import misc.Job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JobSetupDialogModel {
    /** The Job being created by the JobSetupDialog. */
    @Getter @Setter @NonNull private Job job = null;

    /** The list of all list_files to be added to the Job. */
    @Getter private List<File> list_files = new ArrayList<>();
}
