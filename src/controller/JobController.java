package controller;

import com.valkryst.VMVC.SceneManager;
import com.valkryst.VMVC.Settings;
import com.valkryst.VMVC.controller.Controller;
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
import misc.Job;
import misc.JobBuilder;
import model.JobModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.JobView;

import javax.swing.JFileChooser;
import java.awt.HeadlessException;
import java.io.File;
import java.util.List;

public class JobController extends Controller<JobModel, JobView> implements EventHandler {
    /**
     * Constructs a new JobController.
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
    public JobController(final @NonNull SceneManager sceneManager, final @NonNull Settings settings) {
        super (sceneManager, settings, new JobModel(), new JobView(settings));
        addEventHandlers();
    }

    /** Sets all of the view's controls to use this class as their event handler. */
    private void addEventHandlers() {
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

    /**
     * Populates the view with the data of a Job.
     *
     * @param job
     *          The job.
     *
     * @throws NullPointerException
     *         If the job is null.
     */
    void editJob(final @NonNull Job job) {
        view.getTextField_jobName().setText(job.getName());
        view.getTextField_outputFolder().setText(job.getOutputDirectory());

        job.getFiles().forEach(model::addFile);

        for (final File file : job.getFiles()) {
            view.getFileList().getItems().add(file.getName());
        }

        if (job.isEncodeJob()) {
            view.getComboBox_jobType().getSelectionModel().select("Encode");
        } else {
            view.getComboBox_jobType().getSelectionModel().select("Decode");
        }
    }

    @Override
    public void handle(final Event event) {
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
            final Controller previousController = sceneManager.getPreviousController();
            final MainController controller = (MainController) previousController;

            controller.addJob(createJob());

            sceneManager.swapToPreviousScene();
            return;
        }

        if (source.equals(view.getButton_cancel())) {
            sceneManager.swapToPreviousScene();
        }
    }

    /**
     * Creates a new job using the data entered in the view.
     *
     * @return
     *         The job.
     */
    private Job createJob() {
        final JobBuilder builder = new JobBuilder(settings);
        builder.setName(view.getTextField_jobName().getText());
        builder.setOutputDirectory(view.getTextField_outputFolder().getText());
        builder.setFiles(model.getFiles());
        builder.setEncodeJob(view.getComboBox_jobType().getSelectionModel().isSelected(0));


        Job job = null;

        try {
            job = builder.build();
        } catch (final NullPointerException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            final String alertMessage = "Unable to build job. Have you selected an output directory and/or any files?";
            final Alert alert = new Alert(Alert.AlertType.ERROR, alertMessage, ButtonType.OK);
            alert.showAndWait();
        }

        return job;
    }

    /** Opens a file chooser for the user to add files to the job. */
    private void addFiles() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Job File Selection");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final List<File> selectedFiles = fileChooser.showOpenMultipleDialog(sceneManager.getPrimaryStage());

        if (selectedFiles != null) {
            selectedFiles.forEach(model::addFile);

            // Add all of the files to the list:
            for (final File f : selectedFiles) {
                view.getFileList().getItems().add(f.getName());
            }
        }
    }

    /** Removes all files selected within the view's file list. */
    private void removeSelectedFiles() {
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
                view.getTextField_outputFolder().setText(fileChooser.getSelectedFile().getPath() + "/");
            }
        } catch(final HeadlessException e) {
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            final String alertMessage = "There was an issue selecting an output folder.\nSee the log file for more information.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, alertMessage, ButtonType.OK);
            alert.showAndWait();
        }
    }
}
