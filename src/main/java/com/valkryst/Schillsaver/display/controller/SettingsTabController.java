package com.valkryst.Schillsaver.display.controller;

import com.valkryst.Schillsaver.setting.FrameRate;
import com.valkryst.Schillsaver.setting.FrameResolution;
import com.valkryst.Schillsaver.display.model.SettingsTabModel;
import com.valkryst.Schillsaver.setting.BlockSize;
import com.valkryst.Schillsaver.setting.SwingTheme;
import com.valkryst.VMVC.controller.Controller;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;

public class SettingsTabController extends Controller<SettingsTabModel> {
    /**
     * Constructs a new {@code SettingsTabController}.
     *
     * @param model {@link SettingsTabModel} associated with this controller.
     */
    public SettingsTabController(@NonNull SettingsTabModel model) {
        super(model);
    }

    /**
     * Retrieves the path to the FFMPEG executable.
     *
     * @return The path to the FFMPEG executable.
     */
    public Path getFfmpegPath() {
        return model.getFfmpegPath();
    }

    /**
     * Retrieves the path to the output folder.
     *
     * @return The path to the output folder.
     */
    public Path getOutputFolderPath() {
        return model.getOutputFolderPath();
    }

    /**
     * Retrieves the block size.
     *
     * @return The block size.
     */
    public BlockSize getBlockSize() {
        return model.getBlockSize();
    }

    /**
     * Retrieves the codec.
     *
     * @return The codec.
     */
    public String getCodec() {
        return model.getCodec();
    }

    /**
     * Retrieves the resolution.
     *
     * @return The resolution.
     */
    public FrameResolution getResolution() {
        return model.getResolution();
    }

    /**
     * Retrieves the framerate.
     *
     * @return The framerate.
     */
    public FrameRate getFramerate() {
        return model.getFramerate();
    }

    /**
     * Retrieves the Swing theme.
     *
     * @return The Swing theme.
     */
    public SwingTheme getSwingTheme() {
        return model.getSwingTheme();
    }

    /**
     * Sets the path to the FFMPEG executable.
     *
     * @param path The new path.
     * @throws IOException If an IO error occurs.
     */
    public void setFfmpegPath(final @NonNull Path path) throws IOException {
        model.setFfmpegPath(path);
        model.save();
    }

    /**
     * Sets the path to the output folder.
     *
     * @param path The new path.
     * @throws IOException If an IO error occurs.
     */

    public void setOutputFolderPath(final @NonNull Path path) throws IOException {
        model.setOutputFolderPath(path);
        model.save();
    }

    /**
     * Sets the block size.
     *
     * @param blockSize The new block size.
     * @throws IOException If an IO error occurs.
     */
    public void setBlockSize(final @NonNull BlockSize blockSize) throws IOException {
        model.setBlockSize(blockSize);
        model.save();
    }

    /**
     * Sets the codec.
     *
     * @param codec The new codec.
     * @throws IOException If an IO error occurs.
     */
    public void setCodec(final @NonNull String codec) throws IOException {
        model.setCodec(codec);
        model.save();
    }

    /**
     * Sets the framerate.
     *
     * @param framerate The new framerate.
     * @throws IOException If an IO error occurs.
     */
    public void setFramerate(final @NonNull FrameRate framerate) throws IOException {
        model.setFramerate(framerate);
        model.save();
    }

    /**
     * Sets the resolution.
     *
     * @param resolution The new resolution.
     * @throws IOException If an IO error occurs.
     */
    public void setResolution(final @NonNull FrameResolution resolution) throws IOException {
        model.setResolution(resolution);
        model.save();
    }

    /**
     * Sets the Swing theme.
     *
     * @param swingTheme The new Swing theme.
     * @throws IOException If an IO error occurs.
     */
    public void setSwingTheme(final @NonNull SwingTheme swingTheme) throws IOException {
        model.setSwingTheme(swingTheme);
        model.save();
    }
}
