package controller;

import com.valkryst.VMVC.SceneManager;
import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.controller.Controller;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NonNull;
import misc.Job;
import model.MainModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.MainView;

import java.util.List;

public class MainController extends Controller<MainModel, MainView> implements EventHandler {
    /**
     * Constructs a new MainController.
     *
     * @param sceneManager
     *          The scene manager.
     *
     * @param settings
     *          The program settings.
     *
     * @throws NullPointerException
     *         If the sceneManager or settings is null.
     */
    public MainController(final SceneManager sceneManager, final Settings settings) {
        super (sceneManager, settings, new MainModel(), new MainView());
        addEventHandlers();

        loadJobsFromFile();
    }

    /** Sets the view's controls to use this class as their event handler. */
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
            if (view.getButton_createJob().isDisabled() == false) {
                openJobView();
                saveJobsToFile();
                return;
            }
        }

        if (source.equals(view.getButton_editJob())) {
            if (view.getButton_editJob().isDisabled() == false) {
                openEditJobView();
                saveJobsToFile();
                return;
            }
        }

        if (source.equals(view.getButton_deleteSelectedJobs())) {
            if (view.getButton_deleteSelectedJobs().isDisabled() == false) {
                deleteSelectedJobs();
                saveJobsToFile();
                return;
            }
        }

        if (source.equals(view.getButton_processJobs())) {
            if (view.getButton_processJobs().isDisabled() == false) {
                // Disable Buttons:
                view.getButton_createJob().setDisable(true);
                view.getButton_editJob().setDisable(true);
                view.getButton_deleteSelectedJobs().setDisable(true);
                view.getButton_processJobs().setDisable(true);
                view.getButton_programSettings().setDisable(true);

                final List<Thread> encodeJobs = model.prepareEncodingJobs(super.settings, view);
                final List<Thread> decodeJobs = model.prepareDecodingJobs(super.settings, view);

                final Thread thread = new Thread(() -> {
                    processJobs(encodeJobs, decodeJobs);

                    // Enable Buttons:
                    view.getButton_createJob().setDisable(false);
                    view.getButton_editJob().setDisable(false);
                    view.getButton_deleteSelectedJobs().setDisable(false);
                    view.getButton_processJobs().setDisable(false);
                    view.getButton_programSettings().setDisable(false);
                });

                thread.start();
                return;
            }
        }

        if (source.equals(view.getButton_programSettings())) {
            if (view.getButton_programSettings().isDisabled() == false) {
                final SettingsController controller = new SettingsController(sceneManager, settings);

                final Scene scene = new Scene(controller.getView().getPane());
                scene.getStylesheets().add("global.css");
                scene.getRoot().getStyleClass().add("main-root");

                final Stage dialog = new Stage();
                dialog.setTitle("Settings");
                dialog.setScene(scene);
                dialog.setResizable(false);
                dialog.initOwner(sceneManager.getPrimaryStage());
                dialog.initModality(Modality.APPLICATION_MODAL);

                controller.setDialog(dialog);

                dialog.show();
                return;
            }
        }
    }

    /** Deserializes the jobs from a file, if the file exists. */
    private void loadJobsFromFile() {
        model.loadJobs();

        for (final Job job : model.getJobs().values()) {
            view.getJobsList().getItems().add(job.getName());
        }
    }

    /** Serializes the jobs to a file. */
    private void saveJobsToFile() {
        model.saveJobs();
    }

    /** Opens the JobView. */
    private void openJobView() {
        final JobController controller = new JobController(sceneManager, settings);
        sceneManager.swapToNewScene(controller);
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

        final JobController controller = new JobController(sceneManager, settings);
        controller.editJob(job);

        view.getJobsList().getItems().remove(job.getName());
        model.getJobs().remove(job.getName());
        view.getJobsList().getSelectionModel().clearSelection();

        sceneManager.swapToNewScene(controller);
    }

    /**
     * Adds a job into the model and the job list.
     *
     * If a job of the same name already exists, then an error is shown
     * and nothing happens.
     *
     * @param job
     *          The job.
     *
     * @throws NullPointerException
     *         If the job is null.
     */
    void addJob(final @NonNull Job job) {
        final String jobName = job.getName();

        if (! containsJob(jobName)) {
            view.getJobsList().getItems().add(job.getName());
        }

        model.getJobs().put(jobName, job);
        model.saveJobs();
    }

    /**
     * Determines whether the model contains a job with a specific name.
     *
     * @param jobName
     *          The name.
     *
     * @return
     *          Whether the model contains a job with the name.
     *
     * @throws NullPointerException
     *         If the jobName is null.
     */
    private boolean containsJob(final @NonNull String jobName) {
        return model.getJobs().containsKey(jobName);
    }

    /** Deletes all jobs selected within the view's job list. */
    private void deleteSelectedJobs() {
        final ListView<String> jobsList = view.getJobsList();
        final List<String> selectedJobs = FXCollections.observableArrayList(jobsList.getSelectionModel().getSelectedItems());

        for (final String jobName : selectedJobs) {
            view.getJobsList().getItems().remove(jobName);
            model.getJobs().remove(jobName);
        }

        jobsList.getSelectionModel().clearSelection();
        model.saveJobs();
    }

    /**
     * Processes the encode and decode jobs.
     *
     * @param encodeJobs
     *          The encode jobs.
     *
     * @param decodeJobs
     *          The decode jobs.
     *
     * @throws NullPointerException
     *         If the encodeJobs or decodeJobs is null.
     */
    private void processJobs(final @NonNull List<Thread> encodeJobs, final @NonNull List<Thread> decodeJobs) {
        // Run Encode Jobs
        final Thread mainEncodingThread = new Thread(() -> {
           for (final Thread thread : encodeJobs) {
               thread.start();

               try {
                   thread.join();
                   saveJobsToFile();
               } catch (final InterruptedException e) {
                   final Logger logger = LogManager.getLogger();
                   logger.error(e);

                   e.printStackTrace();
               }
           }
        });

        mainEncodingThread.start();

        // Run Decode Jobs
        final Thread mainDecodingThread = new Thread(() -> {
            for (final Thread thread : decodeJobs) {
                thread.start();

                try {
                    thread.join();
                    saveJobsToFile();
                } catch (final InterruptedException e) {
                    final Logger logger = LogManager.getLogger();
                    logger.error(e);

                    e.printStackTrace();
                }
            }
        });

        mainDecodingThread.start();

        try {
            mainEncodingThread.join();
            mainDecodingThread.join();
        } catch (InterruptedException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            e.printStackTrace();
        }
    }
}
