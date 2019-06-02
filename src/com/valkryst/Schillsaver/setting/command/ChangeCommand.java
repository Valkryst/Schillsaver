package com.valkryst.Schillsaver.setting.command;

import lombok.Getter;
import lombok.NonNull;

public class ChangeCommand extends Command {
    /** The key. */
    @Getter private final String key;
    /** The value. */
    @Getter private final String value;

    /** The previous value. */
    @Getter private final String previousValue;

    /**
     * Constructs a new ChangeCommand.
     *
     * @param key
     *          The key.
     *
     * @param value
     *          The value.
     *
     * @param previousValue
     *          The previous value.
     */
    public ChangeCommand(final @NonNull String key, final @NonNull String value, final String previousValue) {
        this.key = key;
        this.value = value;
        this.previousValue = (previousValue == null ? "" : previousValue);
    }

    @Override
    public String toString() {
        return "Change Command (\n\tKey: " + key + "\n\tValue: " + value + "\n\tPrevious Value: " + previousValue + "\n)";
    }
}
