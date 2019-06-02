package com.valkryst.Schillsaver.setting.command;

import lombok.Getter;

import java.util.List;

public class CommitCommand {
    /** The changes committed. */
    @Getter private final ChangeCommand[] changeCommands;

    /**
     * Constructs a new CommitCommand.
     *
     * @param changeCommands
     *          The changes committed.
     */
    public CommitCommand(final List<ChangeCommand> changeCommands) {
        if (changeCommands == null || changeCommands.size() == 0) {
            this.changeCommands = new ChangeCommand[0];
        } else {
            this.changeCommands = new ChangeCommand[changeCommands.size()];

            for (int i = 0 ; i < changeCommands.size() ; i++) {
                this.changeCommands[i] = changeCommands.get(i);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Commit Command (");

        for (final ChangeCommand command : changeCommands) {
            sb.append("\n\t").append(command.toString().replace("\n", "\n\t"));
        }

        sb.append("\n)");

        return sb.toString();
    }
}
