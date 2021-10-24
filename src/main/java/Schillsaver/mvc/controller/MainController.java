package Schillsaver.mvc.controller;

import Schillsaver.SceneManager;
import Schillsaver.job.Job;
import Schillsaver.job.JobBuilder;
import Schillsaver.job.FFMPEGEndec;
import Schillsaver.mvc.model.JobModel;
import Schillsaver.mvc.model.MainModel;
import Schillsaver.mvc.model.SettingsModel;
import Schillsaver.mvc.view.MainView;
import Schillsaver.mvc.view.SettingsView;
import Schillsaver.setting.Settings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NonNull;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainController extends Controller implements EventHandler {
    /**
     * Constructs a new MainController.
     *
     * @param model
     *          The model.
     *
     * @param view
     *          The view.
     *
     * @throws NullPointerException
     *          If the model or view are null.
     */
    public MainController(final @NonNull MainModel model, final @NonNull MainView view) {
        super(model, view);

        // Load saved jobs.
        model.loadJobs();

        for (final Job job : model.getJobs().values()) {
            ((MainView) super.getView()).getJobsList().getItems().add(job.getName());
        }

        updateButtonStates();

        // Set the view's controls to use this controller as their event handler.
        view.getButton_createJob().setOnAction(this);
        view.getButton_editJob().setOnAction(this);
        view.getButton_deleteSelectedJobs().setOnAction(this);
        view.getButton_processJobs().setOnAction(this);

        view.getButton_programSettings().setOnAction(this);

        // Allow the user to drag and drop files onto the view in order to quickly open up job creation.
        view.getPane().setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        view.getPane().setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();

            if (db.hasFiles()) {
                final JobBuilder builder = new JobBuilder();
                builder.setName("");

                // Based on the input files, create either an encode or decode job.
                final List<File> files = db.getFiles();
                boolean areAllMp4 = true;

                for (final File file : files) {
                    if (file.getAbsolutePath().endsWith(".mp4") == false) {
                        areAllMp4 = false;
                        break;
                    }
                }

                if (areAllMp4) {
                    builder.setOutputDirectory(Settings.getInstance().getStringSetting("Default Decoding Output Directory"));
                } else {
                    builder.setOutputDirectory(Settings.getInstance().getStringSetting("Default Encoding Output Directory"));
                }

                builder.setFiles(db.getFiles());
                builder.setEncodeJob(!areAllMp4);

                // Swap to the job view.
                final JobController controller = new JobController(new JobModel(builder.build()));
                SceneManager.getInstance().swapToNewScene(controller);

                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }

            event.consume();
        });
    }

    @Override
    public void handle(final Event event) {
        final MainModel model = (MainModel) super.getModel();
        final MainView view = (MainView) super.getView();

        final Object source = event.getSource();

        if (source.equals(view.getButton_createJob())) {
            if (! view.getButton_createJob().isDisabled()) {
                openJobView();
                model.saveJobs();
            }

            return;
        }

        if (source.equals(view.getButton_editJob())) {
            if (! view.getButton_editJob().isDisabled()) {
                openEditJobView();
                model.saveJobs();
            }

            return;
        }

        if (source.equals(view.getButton_deleteSelectedJobs())) {
            if (! view.getButton_deleteSelectedJobs().isDisabled()) {
                deleteSelectedJobs();
                model.saveJobs();
            }

            return;
        }

        if (source.equals(view.getButton_processJobs())) {
            if (! view.getButton_processJobs().isDisabled()) {
                // Disable Buttons:
                view.getButton_createJob().setDisable(true);
                view.getButton_editJob().setDisable(true);
                view.getButton_deleteSelectedJobs().setDisable(true);
                view.getButton_processJobs().setDisable(true);
                view.getButton_programSettings().setDisable(true);

				final FFMPEGEndec endec = new FFMPEGEndec();

                final List<Thread> encodeJobs = endec.prepareEncodingJobs(this);
                final List<Thread> decodeJobs = endec.prepareDecodingJobs(this);

                final var thread = new Thread(() -> {
                    processJobs(encodeJobs, decodeJobs);

                    // Enable Buttons:
                    view.getButton_createJob().setDisable(false);
                    view.getButton_editJob().setDisable(false);
                    view.getButton_deleteSelectedJobs().setDisable(false);
                    view.getButton_processJobs().setDisable(false);
                    view.getButton_programSettings().setDisable(false);
                });

                thread.start();
            }

            return;
        }

        if (source.equals(view.getButton_programSettings())) {
            if (view.getButton_programSettings().isDisabled() == false) {
                final SettingsModel settingsModel = new SettingsModel();
                final SettingsView settingsView = new SettingsView(settingsModel);
                final SettingsController settingsController = new SettingsController(settingsModel, settingsView);

                final Scene scene = new Scene(settingsView.getPane());
                scene.getStylesheets().add("global.css");
                scene.getRoot().getStyleClass().add("main-root");

                final Stage stage = new Stage();
                stage.setTitle("Settings");
                stage.setScene(scene);
                stage.initOwner(SceneManager.getInstance().getStage());
                stage.initModality(Modality.APPLICATION_MODAL);

                settingsController.setDialog(stage);

                stage.show();
            }
        }
    }

    /** Opens the JobView. */
    private void openJobView() {
        final var controller = new JobController(new JobModel());
        SceneManager.getInstance().swapToNewScene(controller);
    }

    /**
     * Opens the JobView with the first of the currently selected Jobs.
     *
     * If no Jobs are selected, then nothing happens.
     */
    private void openEditJobView() {
        final ListView<String> jobList = ((MainView) super.getView()).getJobsList();
        final List<String> selectedJobs = jobList.getSelectionModel().getSelectedItems();

        if (selectedJobs.size() == 0) {
            return;
        }

        final MainModel model = (MainModel) super.getModel();
        final MainView view = (MainView) super.getView();

        final String firstJobName = selectedJobs.get(0);
        final Job job = model.getJobs().get(firstJobName);

        final JobController controller = new JobController(new JobModel(job));

        view.getJobsList().getItems().remove(job.getName());
        model.getJobs().remove(job.getName());
        view.getJobsList().getSelectionModel().clearSelection();

        SceneManager.getInstance().swapToNewScene(controller);
    }

    /**
     * Adds a job into the model and the job list.
     *
     * If a job of the same name already exists, then an error is shown and nothing happens.
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
            ((MainView) super.getView()).getJobsList().getItems().add(job.getName());
        }

        final MainModel model = (MainModel) super.getModel();
        model.getJobs().put(jobName, job);
        model.saveJobs();

        updateButtonStates();
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
        return ((MainModel) super.getModel()).getJobs().containsKey(jobName);
    }

    /** Deletes all jobs selected within the view's job list. */
    private void deleteSelectedJobs() {
        // Prompt the user to confirm their choice.
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the job(s)?");
        alert.setContentText("");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        final Optional<ButtonType> alertResult = alert.showAndWait();

        if (! alertResult.isPresent() || alertResult.get() == ButtonType.NO) {
            return;
        }

        // Delete jobs if user confirmed choice.
        final MainModel model = (MainModel) super.getModel();
        final MainView view = (MainView) super.getView();

        final ListView<String> jobsList = view.getJobsList();
        final List<String> selectedJobs = FXCollections.observableArrayList(jobsList.getSelectionModel().getSelectedItems());

        for (final String jobName : selectedJobs) {
            view.getJobsList().getItems().remove(jobName);
            model.getJobs().remove(jobName);
        }

        jobsList.getSelectionModel().clearSelection();

        updateButtonStates();
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
        final var mainEncodingThread = new Thread(() -> {
            for (final Thread thread : encodeJobs) {
                thread.start();

                try {
                    thread.join();
                } catch (final InterruptedException e) {
					e.printStackTrace();
                }
            }
        });

        mainEncodingThread.start();

        // Run Decode Jobs
        final var mainDecodingThread = new Thread(() -> {
            for (final Thread thread : decodeJobs) {
                thread.start();

                try {
                    thread.join();
                } catch (final InterruptedException e) {
					e.printStackTrace();
                }
            }
        });

        mainDecodingThread.start();

        try {
            mainEncodingThread.join();
            mainDecodingThread.join();
            Platform.runLater(this::updateButtonStates);
        } catch (InterruptedException e) {
			e.printStackTrace();
        }
    }

    /**
     * Updates the disabled state of the Edit, Delete, and Process buttons to reflect the list of jobs.
     *
     * If there are no jobs, then the buttons are disabled. Else they're enabled.
     */
    private void updateButtonStates() {
        final MainModel model = (MainModel) super.getModel();
        final MainView view = (MainView) super.getView();

        final boolean disableButtons = model.getJobs().size() == 0;

        view.getButton_editJob().setDisable(disableButtons);
        view.getButton_deleteSelectedJobs().setDisable(disableButtons);
        view.getButton_processJobs().setDisable(disableButtons);
    }
}
