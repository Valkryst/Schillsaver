package handler;

import configuration.Settings;
import controller.MainScreenController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import misc.Job;
import module.RuntimeStatisticsModule;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public class FFMPEGHandler extends Task implements EventHandler<WorkerStateEvent> {
    /** The Job being run. */
    private final Job job;
    /** The controller for the main screen. */
    private final MainScreenController controller;
    /** The settings to use when encoding the file(s). */
    private final Settings settings;

    // todo JavaDoc
    final StatisticsHandler statisticsHandler;

    /**
     * Creates a new FFMPEGHandler with the specified parameters.
     *
     * @param job
     *         The Job being run.
     *
     * @param controller
     *         The controller for the main screen.
     *
     * @param settings
     *         The settings to use when encoding the file(s).
     *
     * @param statisticsHandler
     *         todo JavaDoc
     */
    public FFMPEGHandler(final Job job, final MainScreenController controller, final Settings settings, final StatisticsHandler statisticsHandler) {
        this.job = job;
        this.controller = controller;
        this.settings = settings;
        this.statisticsHandler = statisticsHandler;
    }

    @Override
    public Object call() {
        if(job.isEncodeJob()) {
            encode();
        } else {
            decode();
        }

        return null;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        if(event.getEventType().equals(WorkerStateEvent.WORKER_STATE_SUCCEEDED)) {
            controller.getModel().getList_jobs().remove(job);
            controller.getView().getListView_jobs().getItems().remove(job.getFullDesignation());
            controller.getView().getListView_jobs().getSelectionModel().clearSelection();
        }
    }

    /**
     * Encodes the specified file(s) using the settings in the
     * configuration handler.
     */
    private void encode() {
        final ArchiveHandler archiveHandler = new ArchiveHandler();

        if(job.isArchiveFiles()) {
            final File temp = archiveHandler.packFiles(job, job.getFiles(), controller, settings);
            job.getFiles().clear();
            job.getFiles().add(temp);
        }

        for(File f : job.getFiles()) {
            final RuntimeStatisticsModule statisticsModule = new RuntimeStatisticsModule();
            statisticsModule.recordStart();

            // Pad the file:
            FileHandler.padFile(f, settings);

            // Construct FFMPEG string:
            final StringBuilder stringBuilder = new StringBuilder();
            final Formatter formatter = new Formatter(stringBuilder, Locale.US);

            // Use the fully custom settings if they're enabled:
            if(settings.getBooleanSetting("Use Custom FFMPEG Options") && !settings.getStringSetting("Custom FFMPEG Enc Options").isEmpty()) {
                formatter.format("\"%s\" %s",
                        settings.getStringSetting("FFMPEG Path"),
                        settings.getStringSetting("Custom FFMPEG Enc Options"));

                // Insert the input filename:
                final String inputFilename = "\"" + f.getAbsolutePath() + "\"";
                stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_INPUT", inputFilename));

                // Insert the output filename:
                final String outputFilename = "\"" + FilenameUtils.getFullPath(f.getAbsolutePath()) + FilenameUtils.getBaseName(f.getName()) + "." + settings.getStringSetting("Enc Format") + "\"";
                stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_OUTPUT", outputFilename));
            } else if (!settings.getBooleanSetting("Use Custom FFMPEG Options")) {
                formatter.format("\"%s\" -f rawvideo -pix_fmt monob -s %dx%d -r %d -i \"%s\" -vf \"scale=iw*%d:-1\" -sws_flags neighbor -c:v %s -threads 8 -loglevel %s -y \"%s%s.%s\"",
                        settings.getStringSetting("FFMPEG Path"),
                        settings.getIntegerSetting("Enc Vid Width") / settings.getIntegerSetting("Enc Vid Macro Block Dimensions"),
                        settings.getIntegerSetting("Enc Vid Height") / settings.getIntegerSetting("Enc Vid Macro Block Dimensions"),
                        settings.getIntegerSetting("Enc Vid Framerate"),
                        f.getAbsolutePath(),
                        settings.getIntegerSetting("Enc Vid Macro Block Dimensions"),
                        settings.getStringSetting("Enc Library"),
                        settings.getStringSetting("FFMPEG Log Level"),
                        job.getOutputDirectory(),
                        FilenameUtils.getBaseName(f.getName()),
                        settings.getStringSetting("Enc Format"));
            }

            Platform.runLater(() -> controller.getView()
                                              .getTextArea_output()
                                              .appendText(stringBuilder.toString() + System.lineSeparator() +
                                                          System.lineSeparator() + System.lineSeparator()));

            CommandHandler.runProgram(stringBuilder.toString(), controller);

            Platform.runLater(() -> {
                controller.getView()
                          .getTextArea_output()
                          .appendText("ENCODING COMPLETED");

                controller.getView()
                          .getTextArea_output()
                          .appendText(System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
            });

            // Finish statistics estimation:
            statisticsModule.recordEnd();
            statisticsHandler.recordData(true, statisticsHandler.calculateProcessingSpeed(f, statisticsModule));

            // Delete leftovers:
            if(job.isArchiveFiles()) {
                f.delete(); // This is just the archive, not the original handler.
            }
        }
    }

    /**
     * Decodes the specified file(s) using the settings in the
     * configuration handler.
     */
    private void decode() {
        try {
            for(final File f : job.getFiles()) {
                final RuntimeStatisticsModule statisticsModule = new RuntimeStatisticsModule();
                statisticsModule.recordStart();

                // Construct FFMPEG string:
                final StringBuilder stringBuilder = new StringBuilder();
                final Formatter formatter = new Formatter(stringBuilder, Locale.US);

                // Use the fully custom settings if they're enabled:
                if(settings.getBooleanSetting("Use Custom FFMPEG Options") && ! settings.getStringSetting("Custom FFMPEG Dec Options").isEmpty()) {
                    formatter.format("\"%s\" %s",
                            settings.getStringSetting("FFMPEG Path"),
                            settings.getStringSetting("Custom FFMPEG Enc Options"));

                    // Insert the input filename:
                    final String inputFilename = "\"" + f.getAbsolutePath() + "\"";
                    stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_INPUT", inputFilename));

                    // Insert the output filename:
                    final String outputFilename = "\"" + FilenameUtils.getFullPath(f.getAbsolutePath()) + FilenameUtils.getBaseName(f.getName()) + "." + settings.getStringSetting("Dec Format") + "\"";
                    stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_OUTPUT", outputFilename));
                } else if(! settings.getBooleanSetting("Use Custom FFMPEG Options")) {
                    formatter.format("\"%s\" -i \"%s\" -vf \"format=pix_fmts=monob,scale=iw*%f:-1\" -sws_flags area -loglevel %s -f rawvideo \"%s%s.%s\"",
                            settings.getStringSetting("FFMPEG Path"),
                            f.getAbsolutePath(),
                            (1.0 / settings.getIntegerSetting("Enc Vid Macro Block Dimensions")),
                            settings.getStringSetting("FFMPEG Log Level"),
                            job.getOutputDirectory(),
                            FilenameUtils.getBaseName(f.getName()),
                            settings.getStringSetting("Dec Format"));
                }

                Platform.runLater(() -> controller.getView()
                                                  .getTextArea_output()
                                                  .appendText(stringBuilder.toString() + System.lineSeparator() +
                                                              System.lineSeparator() + System.lineSeparator()));

                CommandHandler.runProgram(stringBuilder.toString(), controller);

                Platform.runLater(() -> {
                    controller.getView()
                              .getTextArea_output()
                              .appendText("DECODING COMPLETED");

                    controller.getView()
                              .getTextArea_output()
                              .appendText(System.lineSeparator() + System.lineSeparator() + System.lineSeparator());
                });

                // Finish statistics estimation:
                statisticsModule.recordEnd();
                statisticsHandler.recordData(false, statisticsHandler.calculateProcessingSpeed(f, statisticsModule));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////// Getters

    /** @return The total combined file-size of all file(s) to be en/decoded. */
    public long getTotalFilesize() {
        final AtomicLong temp = new AtomicLong(0);

        job.getFiles().parallelStream()
                     .forEach(file -> temp.addAndGet(file.length()));

        return temp.get();
    }
}
