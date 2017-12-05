package controller;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lombok.Getter;
import misc.Job;
import view.MainView;

public class MainController implements EventHandler {
    /** The primary stage. */
    private final Stage primaryStage;

    /** The view. */
    @Getter private final MainView view = new MainView();

    /**
     * Constructs a new MainController.
     *
     * @param primaryStage
     *          The primary stage.
     */
    public MainController(final Stage primaryStage) {
        this.primaryStage = primaryStage;

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
            createJob();
        }

        if (source.equals(view.getButton_editJob())) {
            editJob();
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

    private Job createJob() {
        view.getJobsList().getItems().addAll("1", "2", "3", "4", "5", "6");
        return null;
    }

    private Job editJob() {
        return null;
    }

    /** Deletes all jobs selected within the view's job list. */
    private void deleteSelectedJobs() {
        final ListView<String> jobsList = view.getJobsList();
        final ObservableList<String> selectedJobs = jobsList.getSelectionModel().getSelectedItems();

        // There's a quirk with the removeAll function where, if you have
        // multiple items using the same string and you delete any one
        // of them, then all items using that string name are deleted.
        //
        // Ex:
        //    "1", "2", "3", "1"
        //    Delete "1"
        //    "2", "3"
        jobsList.getItems().removeAll(selectedJobs);
        jobsList.getSelectionModel().clearSelection();
    }

    private void processJob() {
        view.addOutputTab(String.valueOf(System.currentTimeMillis()));
    }

    private void editProgramSettings() {

    }
}
