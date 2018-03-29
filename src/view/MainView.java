package view;

import com.valkryst.VIcons.VIconType;
import com.valkryst.VMVC.view.View;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;

public class MainView extends View {
    @Getter private Button button_createJob;
    @Getter private Button button_editJob;
    @Getter private Button button_deleteSelectedJobs;
    @Getter private Button button_processJobs;

    @Getter private ListView<String> jobsList;
    @Getter private TabPane outputPanes;

    @Getter private Button button_programSettings;

    private final SplitPane contentArea;

    /** Constructs a new MainView. */
    public MainView() {
        initializeComponents();
        setComponentTooltips();

        final Pane menuBar = createMenuBar();
        contentArea = createContentArea();

        super.pane = new VBox(menuBar, contentArea);
        super.pane.setMinSize(512, 512);
    }

    /** Initializes the components. */
    private void initializeComponents() {
        button_createJob = createIconButton(VIconType.FILE_NEW.getFilePath(), 32, 32);
        button_editJob = createIconButton(VIconType.FILE_EDIT.getFilePath(), 32, 32);
        button_deleteSelectedJobs = createIconButton(VIconType.FILE_DELETE.getFilePath(), 32, 32);
        button_processJobs = createIconButton(VIconType.FILE_PROCESS.getFilePath(), 32, 32);

        jobsList = new ListView<>();
        jobsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        outputPanes = new TabPane();

        button_programSettings = createIconButton(VIconType.SETTINGS.getFilePath(), 32, 32);
    }

    /** Sets the tooltips of the components. */
    private void setComponentTooltips() {
        setTooltip(button_createJob, "Create a new job.");
        setTooltip(button_editJob, "Edit the selected job.");
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
    private Pane createMenuBar() {
        final HBox pane = new HBox(button_createJob, button_editJob, button_deleteSelectedJobs, button_processJobs, button_programSettings);

        HBox.setHgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(pane, Priority.NEVER);

        HBox.setHgrow(button_createJob, Priority.ALWAYS);
        HBox.setHgrow(button_editJob, Priority.ALWAYS);
        HBox.setHgrow(button_deleteSelectedJobs, Priority.ALWAYS);
        HBox.setHgrow(button_processJobs, Priority.ALWAYS);
        HBox.setHgrow(button_programSettings, Priority.ALWAYS);

        return pane;
    }

    /**
     * Creates the content area panel.
     *
     * @return
     *         The content area panel.
     */
    private SplitPane createContentArea() {
        final SplitPane contentArea = new SplitPane(jobsList);

        HBox.setHgrow(contentArea, Priority.ALWAYS);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        jobsList.setFocusTraversable(false);

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
     *
     * @throws NullPointerException
     *         If the title is null.
     */
    public Tab addOutputTab(final @NonNull String title) {
        // Create Text Area for Output
        final TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);

        HBox.setHgrow(textArea, Priority.ALWAYS);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        // Add Output Tab View If Necessary
        if (outputPanes.getTabs().size() == 0) {
            contentArea.getItems().add(outputPanes);
        }

        // Create Tab
        final Tab tab = new Tab(title, textArea);
        tab.setOnClosed(e -> {
            if (outputPanes.getTabs().size() == 0) {
                contentArea.getItems().remove(outputPanes);
            }
        });

        outputPanes.getTabs().add(tab);
        return tab;
    }
}
