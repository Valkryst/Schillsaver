package com.valkryst.Schillsaver.mvc.controller;

import com.valkryst.Schillsaver.SceneManager;
import com.valkryst.Schillsaver.job.Job;
import com.valkryst.Schillsaver.job.JobBuilder;
import com.valkryst.Schillsaver.log.LogLevel;
import com.valkryst.Schillsaver.mvc.model.JobModel;
import com.valkryst.Schillsaver.mvc.view.JobView;
import com.valkryst.Schillsaver.setting.Settings;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class JobController extends Controller implements EventHandler {
    /**
     * Constructs a new JobController.
     *
     * @param model
     *          The model.
     *
     * @throws NullPointerException
     *          If the sceneManager or settings is null.
     */
    public JobController(final @NonNull JobModel model) {
        super(model, new JobView(model));
        addEventHandlers();

        final JobView view = (JobView) super.getView();
        for (final File file : model.getFiles()) {
            view.getFileList().getItems().add(file.getName());
        }
    }

    /** Sets all of the view's controls to use this class as their event handler. */
    private void addEventHandlers() {
        final JobModel model = (JobModel) super.getModel();
        final JobView view = (JobView) super.getView();

        view.getButton_addFiles().setOnAction(this);
        view.getButton_removeSelectedFiles().setOnAction(this);
        view.getButton_selectOutputFolder().setOnAction(this);
        view.getButton_accept().setOnAction(this);
        view.getButton_cancel().setOnAction(this);

        // Allow the user to drag and drop files onto the view in order to
        // add those files to the Job's file list.
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
                for (final File file : db.getFiles()) {
                    view.getFileList().getItems().add(file.getName());
                    model.addFile(file);
                }

                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }

            event.consume();
        });
    }

    @Override
    public void handle(final Event event) {
        final JobModel model = (JobModel) super.getModel();
        final JobView view = (JobView) super.getView();

        final Object source = event.getSource();

        if (source.equals(view.getButton_addFiles())) {
            addFiles();
            return;
        }

        if (source.equals(view.getButton_removeSelectedFiles())) {
            removeSelectedFiles();
            return;
        }

        if (source.equals(view.getButton_selectOutputFolder())) {
            selectOutputFolder();
            return;
        }

        if (source.equals(view.getButton_accept())) {
            final Controller previousController = SceneManager.getInstance().getPreviousController();
            final MainController controller = (MainController) previousController;

            if (model.getFiles().size() == 0) {
                SceneManager.getInstance().swapToPreviousScene();
                return;
            }

            final Job job = createJob();

            if (job != null) {
                controller.addJob(job);
                SceneManager.getInstance().swapToPreviousScene();
            }

            return;
        }

        if (source.equals(view.getButton_cancel())) {
            SceneManager.getInstance().swapToPreviousScene();
        }
    }

    /**
     * Creates a new job using the data entered in the view.
     *
     * @return
     *         The job.
     */
    private Job createJob() {
        final JobModel model = (JobModel) super.getModel();
        final JobView view = (JobView) super.getView();

        final JobBuilder builder = new JobBuilder();
        builder.setName(view.getTextField_jobName().getText());
        builder.setOutputDirectory(view.getTextField_outputFolder().getText());
        builder.setFiles(model.getFiles());
        builder.setEncodeJob(view.getComboBox_jobType().getSelectionModel().isSelected(0));

        Job job = null;

        try {
            job = builder.build();
        } catch (final NullPointerException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to build job. Have you selected an output directory and/or any files?", ButtonType.OK);
            alert.showAndWait();

            Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
        } catch (final IllegalArgumentException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "The output directory does not exist and\ncould not be created.", ButtonType.OK);
            alert.showAndWait();

            Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
        }

        return job;
    }

    /** Opens a file chooser for the user to add files to the job. */
    private void addFiles() {
        final var fileChooser = new FileChooser();
        fileChooser.setTitle("Job File Selection");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final var selectedFiles = fileChooser.showOpenMultipleDialog(SceneManager.getInstance().getStage());

        if (selectedFiles != null) {
            final JobModel model = (JobModel) super.getModel();
            final JobView view = (JobView) super.getView();

            selectedFiles.forEach(model::addFile);

            // Add all of the files to the list:
            for (final File f : selectedFiles) {
                view.getFileList().getItems().add(f.getName());
            }
        }
    }

    /** Removes all files selected within the view's file list. */
    private void removeSelectedFiles() {
        final JobModel model = (JobModel) super.getModel();
        final JobView view = (JobView) super.getView();

        final ListView<String> fileList = view.getFileList();
        final List<String> selectedFiles = FXCollections.observableArrayList(fileList.getSelectionModel().getSelectedItems());

        for (final String fileName : selectedFiles) {
            view.getFileList().getItems().remove(fileName);
            model.removeFilesWithFilename(fileName);
        }

        fileList.getSelectionModel().clearSelection();
    }

    /**
     * Open a file chooser for the user to select an output directory for
     * the job.
     */
    private void selectOutputFolder() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDragEnabled(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setDialogTitle("Directory Selection");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        fileChooser.setApproveButtonText("Accept");

        try {
            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final JobView view = (JobView) super.getView();
                view.getTextField_outputFolder().setText(fileChooser.getSelectedFile().getPath() + "/");
            }
        } catch(final HeadlessException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, "There was an issue selecting an output folder.\nSee the log file for more information.", ButtonType.OK);
            alert.showAndWait();

            Settings.getInstance().getLogger().log(e, LogLevel.ERROR);
        }
    }
}
