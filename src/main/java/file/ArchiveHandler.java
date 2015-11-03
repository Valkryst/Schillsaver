package file;

import gui.MainScreenController;
import misc.Logger;

import java.io.File;

public class ArchiveHandler {
    public File packFile(final File selectedFile, final MainScreenController controller,  final ConfigHandler configHandler) {
        // Basic command settings ripped from http://superuser.com/a/742034
        String commands = "";
        commands += "\"" + configHandler.getCompressionProgramPath() + "\"";
        commands += " ";
        commands += configHandler.getCompressionCommands();
        commands += " ";
        commands += "\"" + selectedFile.getAbsolutePath() + "." + configHandler.getDecodeFormat() + "\"";
        commands += " ";
        commands += "\"" + selectedFile.getAbsolutePath() + "\"";

        controller.getView().getTextArea_ffmpegOutput().append(commands + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

        CommandHandler.runProgram(commands, controller);

        // Return a File poingint to the newly created archive:
        final File file = new File(selectedFile.getAbsoluteFile() + "." + configHandler.getDecodeFormat());


        if(!file.exists()) {
            Logger.writeLog("Could not create " + file.getAbsolutePath() + " shutting down.", Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }
        return file;
    }

    public File packFiles(final File[] selectedFiles, final MainScreenController controller,  final ConfigHandler configHandler, final String archiveName) {
        // Basic command settings ripped from http://superuser.com/a/742034
        String commands = "";
        commands += "\"" + configHandler.getCompressionProgramPath() + "\"";
        commands += " ";
        commands += configHandler.getCompressionCommands();
        commands += " ";
        commands += "\"" + archiveName  + "." + configHandler.getDecodeFormat() + "\""; // todo PUT FILENAME HERE. ARCHIVE.7Z OR WHATEVER.

        for(final File f : selectedFiles) {
            commands += " ";
            commands += "\"" + f.getAbsolutePath() + "\"";
        }

        controller.getView().getTextArea_ffmpegOutput().append(commands + System.lineSeparator() + System.lineSeparator() + System.lineSeparator());

        CommandHandler.runProgram(commands, controller);

        // Return a File int to the newly created archive:
        final File file = new File(archiveName + "." + configHandler.getDecodeFormat());

        if(!file.exists()) {
            Logger.writeLog("Could not create " + archiveName + " shutting down.", Logger.LOG_TYPE_ERROR);
            System.exit(1);
        }

        return file;
    }
}
