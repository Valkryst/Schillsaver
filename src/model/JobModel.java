package model;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JobModel extends Model {
    /** The job's files. */
    @Getter private final List<File> files = new ArrayList<>();
}
