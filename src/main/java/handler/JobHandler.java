package handler;

import controller.MainScreenController;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class JobHandler extends Task {
    // todo JavaDoc
    final MainScreenController controller;

    /** The tasks that are ready to run. */
    private final List<FFMPEGHandler> preparedTasks;

    /**
     * Constructs a new JobHandler.
     * @param controller todo JavaDoc
     * @param preparedTasks The tasks that are ready to run.
     */
    public JobHandler(final MainScreenController controller, final List<FFMPEGHandler> preparedTasks) {
        this.controller = controller;
        this.preparedTasks = preparedTasks;


        // Sort the Jobs from smalles to largest:
        greedySort(this.preparedTasks);
    }

    @Override
    protected Object call() throws Exception {
        // Disable interface components:
        Platform.runLater(() -> {
            controller.getView().getButton_createJob().setDisable(true);
            controller.getView().getButton_deleteSelectedJobs().setDisable(true);
            controller.getView().getButton_deleteAllJobs().setDisable(true);
            controller.getView().getButton_clearOutput().setDisable(true);
            controller.getView().getButton_editSettings().setDisable(true);
            controller.getView().getButton_encode().setDisable(true);
            controller.getView().getButton_decode().setDisable(true);
        });

        // Run Jobs one-by-one:
        for(final FFMPEGHandler task : preparedTasks) {
            final Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
            thread.join();
        }

        // Enable interface components:
        Platform.runLater(() -> {
            controller.getView().getButton_createJob().setDisable(false);
            controller.getView().getButton_deleteSelectedJobs().setDisable(false);
            controller.getView().getButton_deleteAllJobs().setDisable(false);
            controller.getView().getButton_clearOutput().setDisable(false);
            controller.getView().getButton_editSettings().setDisable(false);
            controller.getView().getButton_encode().setDisable(false);
            controller.getView().getButton_decode().setDisable(false);
        });

        return null;
    }

    /**
     * To ensure that the smallest Jobs are run first, the greedy
     * algorithm sorts the array by smallest total filesize using
     * mergesort.
     *
     * @param arr The Job(s) to sort.
     * @return A sorted list of Job(s) from smallest to largest filesize.
     */
    private List<FFMPEGHandler> greedySort(final List<FFMPEGHandler> arr) {
        // If the array has zero, or one, element, then it is already sorted.
        if(arr.size() == 0 || arr.size() == 1) {
            return arr;
        }

        // Split the array into two halves.
        int half = arr.size()/2;
        List<FFMPEGHandler> arrA = new ArrayList<>();
        List<FFMPEGHandler> arrB = new ArrayList<>();

        // Fill both halves with the values from arr.
        for(int i=0;i<half;i++) {
            arrA.add(arr.get(i));
        }

        for(int i=half;i<arr.size();i++) {
            arrB.add(i - half, arr.get(i));
        }

        // Recursively call the sort() method on both halves.
        arrA = greedySort(arrA);
        arrB = greedySort(arrB);

        // Combine the two sorted arrays while sorting.
        List<FFMPEGHandler> arrC = new ArrayList<>();

        int arrAIndex = 0, arrBIndex = 0, arrCIndex = 0;

        while(arrAIndex < arrA.size() && arrBIndex < arrB.size()) {
            if(arrA.get(arrAIndex).getTotalFilesize() < arrB.get(arrBIndex).getTotalFilesize()) {
                arrC.add(arrCIndex, arrA.get(arrAIndex));
                arrAIndex++;
            } else {
                arrC.add(arrCIndex, arrB.get(arrBIndex));
                arrBIndex++;
            }
            arrCIndex++;
        }

        // Because the above loop doesn't work for all elements we must
        // copy whatever elements remain into arrC.
        while(arrAIndex < arrA.size()) {
            arrC.add(arrCIndex, arrA.get(arrAIndex));
            arrCIndex++;
            arrAIndex++;
        }

        while(arrBIndex < arrB.size()) {
            arrC.add(arrCIndex, arrB.get(arrBIndex));
            arrCIndex++;
            arrBIndex++;
        }

        return arrC;
    }
}
