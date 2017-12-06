package controller;

import core.Driver;
import javafx.event.Event;
import javafx.event.EventHandler;
import model.JobModel;
import view.JobView;

public class JobController extends Controller<JobModel, JobView> implements EventHandler {
    /**
     * Constructs a new JobController.
     *
     * @param driver
     *          The driver.
     */
    public JobController(final Driver driver) {
        super(driver, new JobModel(), new JobView());
    }

    @Override
    public void handle(final Event event) {

    }

    private void addFiles() {

    }

    private void removeSelectedFiles() {

    }

    private void selectOutputFolder() {

    }
}
