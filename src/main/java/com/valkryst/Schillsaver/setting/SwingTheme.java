package com.valkryst.Schillsaver.setting;

public enum SwingTheme {
    DARK,
    LIGHT;

    @Override
    public String toString() {
        return Character.toUpperCase(this.name().charAt(0)) + this.name().substring(1).toLowerCase();
    }
}
