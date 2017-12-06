package controller;

import core.Driver;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import misc.Job;
import misc.JobBuilder;
import model.JobModel;
import view.JobView;

import java.io.File;
import java.util.List;

public class JobController extends Controller<JobModel, JobView> implements EventHandler {
    /**
     * Constructs a new JobController.
     *
     * @param driver
     *          The driver.
     */
    JobController(final Driver driver) {
        super(driver, new JobModel(), new JobView());

        addEventHandlers();
    }

    /**
     * Sets all of the view's controls to use this class as their
     * event handler.
     */
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
                    model.getFiles().add(file);
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
     */
    public void editJob(final Job job) {

    }

    @Override
    public void handle(final Event event) {
        final Object source = event.getSource();

        if (source.equals(view.getButton_addFiles())) {
            addFiles();
        }

        if (source.equals(view.getButton_removeSelectedFiles())) {
            removeSelectedFiles();
        }

        if (source.equals(view.getButton_selectOutputFolder())) {
            selectOutputFolder();
        }

        if (source.equals(view.getButton_accept())) {
            final Controller previousController = getDriver().getPreviousController();
            final MainController controller = (MainController) previousController;

            controller.addJob(createJob());

            getDriver().swapToPreviousScene();
        }

        if (source.equals(view.getButton_cancel())) {
            getDriver().swapToPreviousScene();
        }
    }

    private Job createJob() {
        final JobBuilder builder = new JobBuilder();
        builder.setName(view.getTextField_jobName().getText());
        builder.setOutputDirectory(view.getTextField_outputFolder().getText());
        builder.setFiles(model.getFiles());
        builder.setEncodeJob(view.getComboBox_jobType().getSelectionModel().isSelected(1));
        builder.setSingleArchive(view.getRadioButton_singleArchive_yes().isSelected());


        Job job = null;

        try {
            job = builder.build();
        } catch (final NullPointerException | IllegalArgumentException e) {
            // todo Handle case where job build fails
            e.printStackTrace();
        }

        return job;
    }

    /** Opens a file chooser for the user to add files to the job. */
    private void addFiles() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Job File Selection");

        final List<File> selectedFiles = fileChooser.showOpenMultipleDialog(getDriver().getPrimaryStage());

        if (selectedFiles != null) {
            model.getFiles().addAll(selectedFiles);

            // Add all of the files to the list:
            for (final File f : selectedFiles) {
                view.getFileList().getItems().add(f.getName());
            }
        }
    }

    private void removeSelectedFiles() {

    }

    private void selectOutputFolder() {

    }
}
