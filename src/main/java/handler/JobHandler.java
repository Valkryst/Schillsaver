package handler;

import controller.MainScreenController;
import javafx.concurrent.Task;

import java.util.List;

public class JobHandler extends Task {
    // todo JavaDoc
    final MainScreenController controller;

    // todo JavaDoc
    private final List<FFMPEGHandler> preparedTasks;

    // todo JavaDoc
    public JobHandler(final MainScreenController controller, final List<FFMPEGHandler> preparedTasks) {
        this.controller = controller;
        this.preparedTasks = preparedTasks;
    }

    @Override
    protected Object call() throws Exception {
        controller.setButtonsEnabled(false);

        for(final FFMPEGHandler task : preparedTasks) {
            final Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            thread.join();
        }

        controller.setButtonsEnabled(true);

        return null;
    }
}
