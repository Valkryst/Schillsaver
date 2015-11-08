package handler;

import controller.MainScreenController;
import javafx.application.Platform;
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.getView().getButton_createJob().setDisable(true);
                controller.getView().getButton_deleteSelectedJobs().setDisable(true);
                controller.getView().getButton_deleteAllJobs().setDisable(true);
                controller.getView().getButton_clearOutput().setDisable(true);
                controller.getView().getButton_editSettings().setDisable(true);
                controller.getView().getButton_encode().setDisable(true);
                controller.getView().getButton_decode().setDisable(true);
            }
        });

        for(final FFMPEGHandler task : preparedTasks) {
            final Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            thread.join();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.getView().getButton_createJob().setDisable(false);
                controller.getView().getButton_deleteSelectedJobs().setDisable(false);
                controller.getView().getButton_deleteAllJobs().setDisable(false);
                controller.getView().getButton_clearOutput().setDisable(false);
                controller.getView().getButton_editSettings().setDisable(false);
                controller.getView().getButton_encode().setDisable(false);
                controller.getView().getButton_decode().setDisable(false);
            }
        });

        return null;
    }
}
