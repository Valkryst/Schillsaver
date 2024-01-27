package Schillsaver.display.model;

import Schillsaver.display.controller.TextOutputController;
import Schillsaver.display.view.TextOutputView;
import com.valkryst.VMVC.model.Model;
import lombok.NonNull;

public class TextOutputModel extends Model<TextOutputController, TextOutputView> {
    @Override
    protected TextOutputController createController() {
        return new TextOutputController(this);
    }

    @Override
    protected TextOutputView createView(final @NonNull TextOutputController textOutputController) {
        return new TextOutputView(textOutputController);
    }
}
