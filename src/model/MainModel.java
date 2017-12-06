package model;

import lombok.Getter;
import lombok.Setter;
import misc.Job;

import java.util.ArrayList;
import java.util.List;

public class MainModel {
    /** The jobs. */
    @Getter @Setter private List<Job> jobs = new ArrayList<>();
}
