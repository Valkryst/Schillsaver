package model;

import lombok.Getter;
import misc.Job;

import java.util.ArrayList;
import java.util.List;

public class MainScreenModel {
    /** The list of all Jobs to be run. */
    @Getter private List<Job> list_jobs = new ArrayList<>();
}
