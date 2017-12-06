package model;

import lombok.Getter;
import lombok.Setter;
import misc.Job;

import java.util.HashMap;
import java.util.Map;

public class MainModel extends Model {
    /** The jobs. */
    @Getter @Setter private Map<String, Job> jobs = new HashMap<>();
}
