package Schillsaver.mvc.model;

import Schillsaver.setting.Settings;
import Schillsaver.setting.command.ChangeCommand;
import Schillsaver.setting.command.CommitCommand;
import lombok.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SettingsModel extends Model {
    /** Queued change commands. */
    private static final List<ChangeCommand> queuedCommands = new LinkedList<>();

    /** Applied commit commands. */
    private static final List<CommitCommand> appliedCommands = new LinkedList<>();
    /** Reverted commit commands. */
    private static final List<CommitCommand> undoneCommands = new LinkedList<>();

    /**
     * Queues a change command.
     *
     * @param key
     *          The setting name.
     *
     * @param value
     *          The value.
     */
    public void queue(final @NonNull String key, final @NonNull String value) {
        final String previousValue = Settings.getInstance().getStringSetting(key);
        queuedCommands.add(new ChangeCommand(key, value, previousValue));
    }

    /** Processes the queued commands. */
    public void processQueue() {
        if (queuedCommands.size() == 0) {
            undoneCommands.clear();
            return;
        }

        final Settings settings = Settings.getInstance();

        for (final ChangeCommand command : queuedCommands) {
            settings.setSetting(command.getKey(), command.getValue());
        }

        Collections.reverse(queuedCommands);
        appliedCommands.add(new CommitCommand(queuedCommands));
        queuedCommands.clear();
        undoneCommands.clear();
    }

    /** Reverts the most recent commit command. */
    public void undo() {
        if (appliedCommands.size() == 0) {
            return;
        }

        final Settings settings = Settings.getInstance();
        final CommitCommand commitCommand = appliedCommands.remove(appliedCommands.size() - 1);

        for (final ChangeCommand changeCommand : commitCommand.getChangeCommands()) {
            settings.setSetting(changeCommand.getKey(), changeCommand.getPreviousValue());
        }

        undoneCommands.add(commitCommand);
    }

    /** Applies the most recent undone commit command. */
    public void redo() {
        if (undoneCommands.size() == 0) {
            return;
        }

        final Settings settings = Settings.getInstance();
        final CommitCommand commitCommand = undoneCommands.remove(undoneCommands.size() - 1);

        final ChangeCommand[] changeCommands = commitCommand.getChangeCommands();

        for (int i = changeCommands.length - 1 ; i >= 0 ; i--) {
            settings.setSetting(changeCommands[i].getKey(), changeCommands[i].getValue());
        }

        appliedCommands.add(commitCommand);
    }

    /**
     * Whether there are commits that can be undone.
     *
     * @return
     *          Whether there are commits that can be undone.
     */
    public boolean canUndo() {
        return appliedCommands.size() > 0;
    }

    /**
     * Whether there are commits that can be redone.
     *
     * @return
     *          Whether there are commits that can be redone.
     */
    public boolean canRedo() {
        return undoneCommands.size() > 0;
    }
}
