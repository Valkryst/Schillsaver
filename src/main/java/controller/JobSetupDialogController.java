package controller;

import handler.ConfigHandler;
import handler.StatisticsHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.Job;
import misc.Logger;
import model.JobSetupDialogModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import view.JobSetupDialogView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class JobSetupDialogController extends Stage implements EventHandler {
    // todo JavaDoc
    private final JobSetupDialogView view;
    // todo JavaDoc
    private final JobSetupDialogModel model;

    /** The object that handles settings for encoding, decoding, compression, and a number of other features. */
    private final ConfigHandler configHandler;

    // todo JavaDoc
    private final StatisticsHandler statisticsHandler;

    /**
     * Construct a new job setup dialog controller.
     * @param primaryStage todo JavaDoc
     * @param configHandler The object that handles settings for encoding, decoding, compression, and a number of other features.
     * @param statisticsHandler todo JavaDoc
     */
    public JobSetupDialogController(final Stage primaryStage, final ConfigHandler configHandler, final StatisticsHandler statisticsHandler) {
        this.configHandler = configHandler;
        this.statisticsHandler = statisticsHandler;

        view = new JobSetupDialogView(primaryStage, this, configHandler);
        model = new JobSetupDialogModel();

        // Setup Stage:
        final Scene scene = new Scene(view);
        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Job Creator");
        this.getIcons().add(new Image("icon.png"));
        this.setScene(scene);
    }

    @Override
    public void handle(Event event) {
        final Object source = event.getSource();

        // The button to open the handler selection dialog.
        if(source.equals(view.getButton_addFiles())) {
            final FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Job File Selection");
            final List<File> selectedFiles = fileChooser.showOpenMultipleDialog(this);

            if(selectedFiles != null) {
                model.getList_files().addAll(selectedFiles);

                // Add all of the files to the list:
                for(final File f : selectedFiles) {
                    view.getListView_selectedFiles().getItems().add(f.getAbsolutePath());
                }
            }

            updateEstimatedDurationLabel();
        }

        // The button to remove all files that are currently selected on the scrollpane_selectedFiles.
        if(source.equals(view.getButton_removeSelectedFiles())) {
            // If a copy of the observable list is not made, then errors can occur.
            // These errors are caused by the ObservableList updating as items are being removed.
            // This causes items that shouldn't be removed to be removed.
            final ObservableList<String> copy = FXCollections.observableArrayList(view.getListView_selectedFiles().getSelectionModel().getSelectedItems());
            view.getListView_selectedFiles().getItems().removeAll(copy);

            // Remove Jobs from the Model while updating
            // the IDs of all Jobs.
            final Iterator<File> it = model.getList_files().iterator();

            while(it.hasNext()) {
                final File f = it.next();

                if(view.getListView_selectedFiles().getItems().contains(f.getAbsolutePath()) == false) {
                    it.remove();
                }
            }

            view.getListView_selectedFiles().getSelectionModel().clearSelection();

            updateEstimatedDurationLabel();
        }

        // The button to remove all files from the list.
        if(source.equals(view.getButton_clearAllFiles())) {
            model.getList_files().clear();
            view.getListView_selectedFiles().getItems().clear();
            view.getListView_selectedFiles().getSelectionModel().clearSelection();

            updateEstimatedDurationLabel();
        }

        // The radio button that says that each of the currently selected files should be archived as a single archive before encoding.
        if(source.equals(view.getRadioButton_singleArchive_yes())) {
            // Ensure that both the single and individual archive options aren't
            // selected at the same time.
            view.getRadioButton_individualArchives_no().setSelected(true);
        }

        // The radio button that says that each handler should be archived individually before encoding each of them individually.
        if(source.equals(view.getRadioButton_individualArchives_yes())) {
            // Ensure that both the single and individual archive options aren't
            // selected at the same time.
            view.getRadioButton_singleArchive_no().setSelected(true);
        }

        // The button to select the folder to output the archive to if the "Yes" radio button is selected.
        if(source.equals(view.getButton_selectOutputDirectory())) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDragEnabled(false);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            fileChooser.setDialogTitle("Directory Slection");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            fileChooser.setApproveButtonText("Accept");
            fileChooser.setApproveButtonToolTipText("Accept the selected directory.");

            try {
                int returnVal = fileChooser.showOpenDialog(null);

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    view.getTextField_outputDirectory().setText(fileChooser.getSelectedFile().getPath() + "/");
                }
            } catch(final HeadlessException e) {
                Logger.writeLog(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e), Logger.LOG_TYPE_WARNING);
            }
        }

        // The button to close the JobCreationDialog without creating a Job.
        if(source.equals(view.getButton_accept())) {
            if(areSettingsCorrect() == false) {
                final String name = view.getField_jobName().getText();
                final String description = view.getTextArea_jobDescription().getText();
                final String outputDirectory = view.getTextField_outputDirectory().getText();
                final List<File> files = model.getList_files();
                final boolean isEncodeJob = view.getIsEncodeJob();
                final boolean combineAllFilesIntoSingleArchive = view.getRadioButton_singleArchive_yes().isSelected();
                final boolean combineIntoIndividualArchives = view.getRadioButton_individualArchives_yes().isSelected();

                final Job job = new Job(name, description, outputDirectory, files, isEncodeJob, combineAllFilesIntoSingleArchive, combineIntoIndividualArchives);
                model.setJob(job);
                this.close();
            }
        }

        // The button to close the JobCreationDialog while creating a Job.
        if(source.equals(view.getButton_cancel())) {
            model.setJob(null);
            this.close();
        }
    }

    // todo Javadoc
    public boolean areSettingsCorrect() {
        boolean wasErrorFound = false;

        // Reset the error states and tooltips of any components
        // that can have an error state set.
        // Set the password fields back to their normal look:
        view.getField_jobName().getStylesheets().remove("field_error.css");
        view.getField_jobName().getStylesheets().add("global.css");
        view.getField_jobName().setTooltip(null);

        view.getTextField_outputDirectory().getStylesheets().remove("field_error.css");
        view.getTextField_outputDirectory().getStylesheets().add("global.css");
        view.getTextField_outputDirectory().setTooltip(new Tooltip("The directory in which to place the en/decoded file(s)."));

        // Check to see that the Job has been given a name.
        if(view.getField_jobName().getText().isEmpty()) {
            view.getField_jobName().getStylesheets().remove("global.css");
            view.getField_jobName().getStylesheets().add("field_error.css");

            final String errorText = "Error - You need to enter a name for the Job.";
            view.getField_jobName().setTooltip(new Tooltip(errorText));

            wasErrorFound = true;
        }

        // Check to see that the output directory exists:
        if(view.getTextField_outputDirectory().getText().isEmpty() || Files.exists(Paths.get(view.getTextField_outputDirectory().getText())) == false) {
            view.getTextField_outputDirectory().getStylesheets().remove("global.css");
            view.getTextField_outputDirectory().getStylesheets().add("field_error.css");

            final Tooltip currentTooltip = view.getTextField_outputDirectory().getTooltip();
            final String errorText = "Error - You need to set an output directory for the Job.";
            view.getField_jobName().setTooltip(new Tooltip(currentTooltip.getText() + "\n\n" + errorText));

            wasErrorFound = true;
        }

        if(Files.isDirectory(Paths.get(view.getTextField_outputDirectory().getText())) == false) {
            view.getTextField_outputDirectory().getStylesheets().remove("global.css");
            view.getTextField_outputDirectory().getStylesheets().add("field_error.css");

            final Tooltip currentTooltip = view.getTextField_outputDirectory().getTooltip();
            final String errorText = "Error - You need to set a valid output directory for the Job.";
            view.getField_jobName().setTooltip(new Tooltip(currentTooltip.getText() + "\n\n" + errorText));

            wasErrorFound = true;
        }

        return wasErrorFound;
    }

    /**
     * Either updates the estimated duration label with the estimated duration, in minutes,
     * that the Job will take, or "unknown" if there is insufficent data to estimate.
     */
    public void updateEstimatedDurationLabel() {
        // Determine if the time can be estimated.
        boolean canTimeBeEstimated = true;
        canTimeBeEstimated &= model.getList_files().size() > 0;
        canTimeBeEstimated &= (view.getIsEncodeJob() ? statisticsHandler.getBytesEncodedPerMinute() > 0 : statisticsHandler.getBytesDecodedPerMinute() > 0);

        // If the time can be estimated, then do so, else show unknown.
        if(canTimeBeEstimated) {
            view.getLabel_job_estimatedDurationInMinutes().setText("Estimated Time - " + statisticsHandler.estimateProcessingDuration(view.getIsEncodeJob(), model.getList_files()) + " Minutes");
        } else {
            view.getLabel_job_estimatedDurationInMinutes().setText("Estimated Time - Unknown");
        }
    }

    ////////////////////////////////////////////////////////// Getters

    // todo JavaDoc
    public JobSetupDialogView getView() {
        return view;
    }

    // todo JavaDoc
    public JobSetupDialogModel getModel() {
        return model;
    }
}
