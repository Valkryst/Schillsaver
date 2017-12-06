package view;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.Getter;

public class MainView extends VBox implements View{
    @Getter private Button button_createJob;
    @Getter private Button button_editJob;
    @Getter private Button button_deleteSelectedJobs;
    @Getter private Button button_processJobs;

    @Getter private ListView<String> jobsList;
    @Getter private TabPane outputPanes;

    @Getter private Button button_programSettings;

    private final GridPane contentArea;

    /** Constructs a new MainView. */
    public MainView() {
        initializeComponents();
        setComponentTooltips();

        final HBox menuBar = createMenuBar();
        contentArea = createContentArea();
        this.getChildren().addAll(menuBar, contentArea);
    }

    /** Initializes the components. */
    private void initializeComponents() {
        button_createJob = new Button("Create Job");
        button_editJob = new Button("Edit Job");
        button_deleteSelectedJobs = new Button("Delete Selected Job(s)");
        button_processJobs = new Button("Process Jobs");

        jobsList = new ListView<>();
        jobsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


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
    private GridPane createContentArea() {
        final GridPane contentArea = new GridPane();

        // Set job list's side to take up all space
        final ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        contentArea.getColumnConstraints().add(column);

        HBox.setHgrow(contentArea, Priority.ALWAYS);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        fillAllAvailableSpace(jobsList);

        contentArea.add(jobsList, 0, 0);

        return contentArea;
    }

    /**
     * Creates and adds a new output tab to the set of output tabs.
     *
     * @param title
     *          The title of the tab.
     *
     * @return
     *          The tab.
     */
    public Tab addOutputTab(final String title) {
        // Create Text Area for Output
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);

        HBox.setHgrow(textArea, Priority.ALWAYS);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        // Add Output Tab View If Necessary
        if (outputPanes.getTabs().size() == 0) {
            contentArea.add(outputPanes, 1, 0);

            // Set each side to use 50% of the space:
            contentArea.getColumnConstraints().clear();

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(50);

            ColumnConstraints column2 = new ColumnConstraints();
            column2.setPercentWidth(50);

            contentArea.getColumnConstraints().addAll(column1, column2);
        }

        // Create Tab
        final Tab tab = new Tab();
        tab.setText(title);
        tab.setContent(textArea);

        outputPanes.getTabs().add(tab);
        return tab;
    }

    /**
     * Removes a tab from the set of output tabs.
     *
     * @param tab
     *          The tab to remove.
     */
    public void removeOutputTab(final Tab tab) {
        outputPanes.getTabs().remove(tab);

        if (outputPanes.getTabs().size() == 0) {
            contentArea.getChildren().remove(outputPanes);

            // Set job list's side to take up all space
            contentArea.getColumnConstraints().clear();

            final ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100);
            contentArea.getColumnConstraints().add(column);
        }
    }
}
