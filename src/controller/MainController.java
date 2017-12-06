package controller;

import core.Driver;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import misc.Job;
import model.MainModel;
import view.MainView;

import javax.swing.Timer;
import java.util.List;

public class MainController extends Controller<MainModel, MainView> implements EventHandler {
    /**
     * Constructs a new MainController.
     *
     * @param driver
     *          The driver.
     */
    public MainController(final Driver driver) {
        super(driver, new MainModel(), new MainView());
        addEventHandlers();
    }

    /**
     * Sets all of the view's controls to use this class as their
     * event handler.
     */
    private void addEventHandlers() {
        view.getButton_createJob().setOnAction(this);
        view.getButton_editJob().setOnAction(this);
        view.getButton_deleteSelectedJobs().setOnAction(this);
        view.getButton_processJobs().setOnAction(this);

        view.getButton_programSettings().setOnAction(this);
    }

    @Override
    public void handle(final Event event) {
        final Object source = event.getSource();

        if (source.equals(view.getButton_createJob())) {
            openJobView();
        }

        if (source.equals(view.getButton_editJob())) {
            openEditJobView();
        }

        if (source.equals(view.getButton_deleteSelectedJobs())) {
            deleteSelectedJobs();
        }

        if (source.equals(view.getButton_processJobs())) {
            processJob();
        }

        if (source.equals(view.getButton_programSettings())) {
            editProgramSettings();
        }
    }

    /** Opens the JobView. */
    private void openJobView() {
        final JobController controller = new JobController(getDriver());
        getDriver().swapToNewScene(controller);
    }

    /**
     * Opens the JobView with the first of the currently selected Jobs.
     *
     * If no Jobs are selected, then nothing happens.
     */
    private void openEditJobView() {
        final ListView<String> jobList = view.getJobsList();
        final List<String> selectedJobs = jobList.getSelectionModel().getSelectedItems();

        if (selectedJobs.size() == 0) {
            return;
        }

        final String firstJobName = selectedJobs.get(0);
        final Job job = model.getJobs().get(firstJobName);

        final JobController controller = new JobController(getDriver());
        controller.editJob(job);

        getDriver().swapToNewScene(controller);
    }

    /**
     * Adds a job into the model and the job list.
     *
     * If a job of the same name already exists, then an error is shown
     * and nothing happens.
     *
     * @param job
     *          The job.
     */
    public void addJob(final Job job) {
        final String jobName = job.getName();

        if (! containsJob(jobName)) {
            view.getJobsList().getItems().add(job.getName());
        }

        model.getJobs().put(jobName, job);
    }

    /**
     * Determines whether the model contains a job with a specific name.
     *
     * @param jobName
     *          The name.
     *
     * @return
     *          Whether the model contains a job with the name.
     */
    public boolean containsJob(final String jobName) {
        return model.getJobs().containsKey(jobName);
    }

    /** Deletes all jobs selected within the view's job list. */
    private void deleteSelectedJobs() {
        final ListView<String> jobsList = view.getJobsList();
        final List<String> selectedJobs = jobsList.getSelectionModel().getSelectedItems();

        for (final String jobName : selectedJobs) {
            view.getJobsList().getItems().remove(jobName);
            model.getJobs().remove(jobName);
        }
    }

    private void processJob() {
        final Tab tab = view.addOutputTab(String.valueOf(System.currentTimeMillis()));


        final Timer timer = new Timer(1000, e -> ((TextArea) tab.getContent()).appendText("\n" + System.currentTimeMillis()));
        timer.start();
    }

    private void editProgramSettings() {

    }
}
