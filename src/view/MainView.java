package view;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class MainView extends VBox implements View{
    private Button button_createJob;
    private Button button_editJob;
    private Button button_deleteSelectedJobs;
    private Button button_processJobs;

    private ListView<String> jobsList;
    private TabPane outputPanes;

    private Button button_programSettings;

    /** Constructs a new MainView. */
    public MainView() {
        initializeComponents();
        setComponentTooltips();

        final HBox menuBar = createMenuBar();
        final TilePane contentArea = createContentArea();
        this.getChildren().addAll(menuBar, contentArea);
    }

    /** Initializes the components. */
    private void initializeComponents() {
        button_createJob = new Button("Create Job");
        button_editJob = new Button("Edit Job");
        button_deleteSelectedJobs = new Button("Delete Selected Job(s)");
        button_processJobs = new Button("Process Jobs");

        jobsList = new ListView<>();
        outputPanes = new TabPane();

        button_programSettings = new Button();
        button_programSettings.setGraphic(new ImageView(new Image("Gear.png")));
    }

    /** Sets the tooltips of the components. */
    private void setComponentTooltips() {
        setTooltip(button_createJob, "Create a new job.");
        setTooltip(button_editJob, "Edit the selected job..");
        setTooltip(button_deleteSelectedJobs, "Delete all selected jobs.");
        setTooltip(button_processJobs, "Process jobs.");

        setTooltip(button_programSettings, "Edit the program settings.");
    }

    /**
     * Creates the menu bar panel.
     *
     * @return
     *         The menu bar panel.
     */
    private HBox createMenuBar() {
        final HBox menuBar = new HBox();

        HBox.setHgrow(menuBar, Priority.ALWAYS);
        VBox.setVgrow(menuBar, Priority.NEVER);

        HBox.setHgrow(button_createJob, Priority.ALWAYS);
        HBox.setHgrow(button_editJob, Priority.ALWAYS);
        HBox.setHgrow(button_deleteSelectedJobs, Priority.ALWAYS);
        HBox.setHgrow(button_processJobs, Priority.ALWAYS);
        HBox.setHgrow(button_programSettings, Priority.ALWAYS);

        menuBar.getChildren().addAll(button_createJob,
                                         button_editJob,
                                         button_deleteSelectedJobs,
                                         button_processJobs,
                                         button_programSettings);

        return menuBar;
    }

    /**
     * Creates the content area panel.
     *
     * @return
     *         The content area panel.
     */
    private TilePane createContentArea() {
        final TilePane contentArea = new TilePane();

        HBox.setHgrow(contentArea, Priority.ALWAYS);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        contentArea.getChildren().addAll(jobsList, outputPanes);

        return contentArea;
    }
}
