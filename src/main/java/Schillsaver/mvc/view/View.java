package Schillsaver.mvc.view;

import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;


public abstract class View {
    /** The view pane. */
    @Getter @Setter private Pane pane;
}
