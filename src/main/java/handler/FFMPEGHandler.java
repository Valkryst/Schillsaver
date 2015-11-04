package handler;

import gui.MainScreenController;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

public class FFMPEGHandler {
    /**
     * Encodes the specified handler(s) using the settings in the specified
     * configuration handler.
     * @param selectedFiles The handler(s) to encode.
     * @param controller The controller for the main screen.
     * @param configHandler The settings to use when encoding the handler(s).
     */
    public void encodeVideoToDisk(File[] selectedFiles, final MainScreenController controller, final ConfigHandler configHandler) {
        final ArchiveHandler archiveHandler = new ArchiveHandler();

        if(configHandler.getCombineAllFilesIntoSingleArchive()) {
            final File temp = archiveHandler.packFiles(selectedFiles, controller, configHandler, configHandler.getEncodedFilePath());
            selectedFiles = new File[1];
            selectedFiles[0] = temp;
        } else if(configHandler.getCombineIntoIndividualArchives()){
            for(int i = 0 ; i < selectedFiles.length ; i++) {
                selectedFiles[i] = archiveHandler.packFile(selectedFiles[i], controller, configHandler);
            }

            // Sort the array of files to ensure the smallest files
            // are encoded first.
            selectedFiles = greedySort(selectedFiles);
        }

        for(File f : selectedFiles) {
            FileHandler.padFile(f, configHandler);

            // Construct FFMPEG string:
            final StringBuilder stringBuilder = new StringBuilder();
            final Formatter formatter = new Formatter(stringBuilder, Locale.US);

            // Use the fully custom settings if they're enabled:
            if(configHandler.getUseFullyCustomFfmpegOptions() && !configHandler.getFullyCustomFfmpegEncodingOptions().isEmpty()) {
                formatter.format("\"%s\" %s",
                        configHandler.getFfmpegPath(),
                        configHandler.getFullyCustomFfmpegEncodingOptions());

                // Insert the input filename:
                final String inputFilename = "\"" + f.getAbsolutePath() + "\"";
                stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_INPUT", inputFilename));

                // Insert the output filename:
                final String outputFilename = "\"" + FilenameUtils.getFullPath(f.getAbsolutePath()) + FilenameUtils.getBaseName(f.getName()) + "." + configHandler.getEncodeFormat() + "\"";
                stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_OUTPUT", outputFilename));
            } else if (!configHandler.getUseFullyCustomFfmpegOptions()) {
                formatter.format("\"%s\" -f rawvideo -pix_fmt monob -s %dx%d -r %d -i \"%s\" -vf \"scale=iw*%d:-1\" -sws_flags neighbor -c:v %s -threads 8 -loglevel %s -y \"%s%s.%s\"",
                        configHandler.getFfmpegPath(),
                        (configHandler.getEncodedVideoWidth() / configHandler.getMacroBlockDimensions()),
                        (configHandler.getEncodedVideoHeight() / configHandler.getMacroBlockDimensions()),
                        configHandler.getEncodedFramerate(),
                        f.getAbsolutePath(),
                        configHandler.getMacroBlockDimensions(),
                        configHandler.getEncodingLibrary(),
                        configHandler.getFfmpegLogLevel(),
                        FilenameUtils.getFullPath(f.getAbsolutePath()),
                        FilenameUtils.getBaseName(f.getName()),
                        configHandler.getEncodeFormat());
            }

            controller.getView().getTextArea_ffmpegOutput().append(stringBuilder.toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

            CommandHandler.runProgram(stringBuilder.toString(), controller);
            controller.getView().getTextArea_ffmpegOutput().append("ENCODING COMPLETED");
            controller.getView().getTextArea_ffmpegOutput().append(System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

            // Delete leftovers:
            if(configHandler.getCombineAllFilesIntoSingleArchive()) {
                f.delete(); // This is just the archive, not the original handler.
            } else {
                if(configHandler.getDeleteOriginalFileWhenEncoding()) {
                    f.delete(); // This is the original handler.
                }
            }

            if(configHandler.getDeleteOriginalFileWhenEncoding()) {
               f = new File(f.getAbsolutePath().replace(configHandler.getDecodeFormat(), ""));
               f.delete();
            }
        }

        // Enable buttons after encoding:
        controller.getView().enableButtons();
    }

    /**
     * Decodes the specified handler(s) using the settings in the specified
     * configuration handler.
     * @param selectedFiles The handler(s) to decode.
     * @param controller The controller for the main screen.
     * @param configHandler The settings to use when decoding the handler(s).
     */
    public void decodeVideo(File[] selectedFiles, final MainScreenController controller,  final ConfigHandler configHandler) {
        // Sort the array of files to ensure the smallest files
        // are decoded first.
        selectedFiles = greedySort(selectedFiles);

        for(final File f : selectedFiles) {
            // Construct FFMPEG string:
            final StringBuilder stringBuilder = new StringBuilder();
            final Formatter formatter = new Formatter(stringBuilder, Locale.US);

            // Use the fully custom settings if they're enabled:
            if(configHandler.getUseFullyCustomFfmpegOptions() && !configHandler.getFullyCustomFfmpegDecodingOptions().isEmpty()) {
                formatter.format("\"%s\" %s",
                        configHandler.getFfmpegPath(),
                        configHandler.getFullyCustomFfmpegEncodingOptions());

                // Insert the input filename:
                final String inputFilename = "\"" + f.getAbsolutePath() + "\"";
                stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_INPUT", inputFilename));

                // Insert the output filename:
                final String outputFilename = "\"" + FilenameUtils.getFullPath(f.getAbsolutePath()) + FilenameUtils.getBaseName(f.getName()) + "." + configHandler.getEncodeFormat() + "\"";
                stringBuilder.replace(0, stringBuilder.length(), stringBuilder.toString().replace("FILE_OUTPUT", outputFilename));
            } else if (!configHandler.getUseFullyCustomFfmpegOptions()) {
                formatter.format("\"%s\" -i \"%s\" -vf \"format=pix_fmts=monob,scale=iw*%f:-1\" -sws_flags area -loglevel %s -f rawvideo \"%s.%s\"",
                        configHandler.getFfmpegPath(),
                        f.getAbsolutePath(),
                        (1.0 / configHandler.getMacroBlockDimensions()),
                        configHandler.getFfmpegLogLevel(),
                        f.getAbsolutePath(),
                        configHandler.getDecodeFormat());
            }

            controller.getView().getTextArea_ffmpegOutput().append(stringBuilder.toString() + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

            CommandHandler.runProgram(stringBuilder.toString(), controller);
            controller.getView().getTextArea_ffmpegOutput().append("DECODING COMPLETED");
            controller.getView().getTextArea_ffmpegOutput().append(System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

            // Delete leftovers:
            if(configHandler.getDeleteOriginalFileWhenDecoding()) {
               f.delete(); // This is just the archive, not the original handler.
            }
        }

        // Enable buttons after decoding:
        controller.getView().enableButtons();
    }

    /**
     * To ensure that the smallest files are encoded first, the greedy
     * algorithm sorts the array by smallest filesize using mergesort.
     *
     * @param arr The handler(s) to sort.
     * @return A sorted array of handler(s) from smallest to largest filesize.
     */
    private File[] greedySort(final File[] arr) {
        // If the array has zero, or one, element, then it is already sorted.
        if(arr.length == 0 || arr.length == 1) {
            return arr;
        }

        // Split the array into two halves.
        int half = arr.length/2;
        File[] arrA = new File[half];
        File[] arrB = new File[arr.length - half];

        // Fill both halves with the values from arr.
        for(int i=0;i<half;i++) {
            arrA[i] = arr[i];
        }

        for(int i=half;i<arr.length;i++) {
            arrB[i - half] = arr[i];
        }

        // Recursively call the sort() method on both halves.
        arrA = greedySort(arrA);
        arrB = greedySort(arrB);

        // Combine the two sorted arrays while sorting.
        File[] arrC = new File[arr.length];

        int arrAIndex = 0, arrBIndex = 0, arrCIndex = 0;

        while(arrAIndex < arrA.length && arrBIndex < arrB.length) {
            if(arrA[arrAIndex].length() < arrB[arrBIndex].length()) {
                arrC[arrCIndex] = arrA[arrAIndex];
                arrAIndex++;
            } else {
                arrC[arrCIndex] = arrB[arrBIndex];
                arrBIndex++;
            }
            arrCIndex++;
        }

        // Because the above loop doesn't work for all elements we must
        // copy whatever elements remain into arrC.
        while(arrAIndex < arrA.length) {
            arrC[arrCIndex] = arrA[arrAIndex];
            arrCIndex++;
            arrAIndex++;
        }

        while(arrBIndex < arrB.length) {
            arrC[arrCIndex] = arrB[arrBIndex];
            arrCIndex++;
            arrBIndex++;
        }

        return arrC;
    }
}
