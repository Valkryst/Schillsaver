package model;

import misc.Job;

import java.util.ArrayList;
import java.util.List;

public class MainScreenModel {
    /** The list of all Jobs to be run. */
    private List<Job> list_jobs = new ArrayList<>();

    /** @return The list of all Jobs to be run. */
    public List<Job> getList_jobs() {
        return list_jobs;
    }
}
