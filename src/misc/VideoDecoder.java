package misc;

import configuration.Settings;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class VideoDecoder {
    private final Settings settings;
    private final TextArea outputArea;
    private StringBuilder outputAreaText = new StringBuilder();

    private int numBlockColumns;
    private int numBlockRows;
    private int bitsPerFrame;
    private int bytesPerFrame;

    /**
     * Constructs a new VideoEncoder.
     *
     * @param settings
     *          The settings.
     *
     * @throws NullPointerException
     *          If the settings or outputArea are null.
     *
     * @throws IllegalStateException
     *          If there's an issue with the encoding settings.
     */
    public VideoDecoder(final Settings settings, final TextArea outputArea) throws IllegalStateException {
        Objects.requireNonNull(settings);
        Objects.requireNonNull(outputArea);

        this.settings = settings;
        this.outputArea = outputArea;
    }

    public void decode(final File inputFile, final File outputFile) throws IOException, InterruptedException {
        final Demuxer demuxer = Demuxer.make();
        demuxer.open(inputFile.getAbsolutePath(), null, false, true, null, null);

        // Retrieve the Decoder for the first video stream of the file.
        Decoder decoder = null;
        int streamId = -1;

        for (int i = 0 ; i < demuxer.getNumStreams() ; i++) {
            final DemuxerStream stream = demuxer.getStream(i);
            decoder = stream.getDecoder();

            if (decoder != null) {
                if (decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
                    streamId = i;
                    break;
                }
            }
        }

        if (decoder == null || streamId == -1) {
            // todo Throw an error, there's no video stream
            System.err.println("There's no video stream. decode VideoDecoder");
            System.exit(1);
        }

        decoder.open(null, null);

        outputAreaText.append("Decode Info:");
        outputAreaText.append("\n\tInput File: ").append(inputFile.getAbsolutePath());
        outputAreaText.append("\n\tOutput File: ").append(outputFile.getAbsolutePath());

        final FrameDimension frameDimensions = FrameDimension.getFrameDimension(decoder.getWidth(), decoder.getHeight());

        if (frameDimensions == null) {
            // todo Video doesn't match any of the accepted FrameDimension's, so it's
            // todo probably not a video made by Schillsaver.
            System.err.println("Video probs not made by Schillsaver. VideoDecoder|FrameDimensions");
            System.exit(1);
        }


        final FrameRate frameRate = FrameRate.getFrameRate(decoder.getTimeBase());

        if (frameRate == null) {
            // todo Video doesn't match any of the accept FrameRate's, so it's probably
            // todo not a video made by Schillsaver.
            System.err.println("Video probs not made by Schillsaver. VideoDecoder|FrameRate");
            System.exit(1);
        }

        final PixelFormat.Type pixelFormat = decoder.getPixelFormat();
        final Dimension blockDimensions = settings.getBlockDimensions().getBlockSize();

        numBlockRows = frameDimensions.getHeight() / blockDimensions.height;
        numBlockColumns = frameDimensions.getWidth() / blockDimensions.width;
        bytesPerFrame = numBlockColumns * numBlockRows;
        bitsPerFrame = bytesPerFrame/ 8;

        outputAreaText.append("\n\nSettings:");
        outputAreaText.append("\n\tFormat: ZIP");
        outputAreaText.append("\n\tCodec: ").append(decoder.getCodec().getName());
        outputAreaText.append("\n\tFrame Rate Enum: ").append(frameRate.name());
        outputAreaText.append("\n\tFrame Rate: ").append(decoder.getTimeBase().getDenominator());
        outputAreaText.append("\n\tFrame Dimensions Enum: ").append(frameDimensions.name());
        outputAreaText.append("\n\tFrame Width: ").append(frameDimensions.getWidth());
        outputAreaText.append("\n\tFrame Height: ").append(frameDimensions.getHeight());
        outputAreaText.append("\n\tPixel Format: ").append(pixelFormat.name());
        outputAreaText.append("\n\nDecode Progress:");

        final MediaPicture picture = MediaPicture.make(frameDimensions.getWidth(), frameDimensions.getHeight(), pixelFormat);
        final MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture);
        final MediaPacket packet = MediaPacket.make();
        BufferedImage frameImage = null;

        try (
            final FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            int frame = 0;

            while (demuxer.read(packet) >= 0) {
                if (packet.getStreamIndex() == streamId) {
                    int offset = 0;
                    int bytesRead = 0;

                    do {
                        bytesRead += decoder.decode(picture, packet, offset);

                        if (picture.isComplete()) {
                            frameImage = converter.toImage(frameImage, picture);

                            final byte[] bytes = decodeFrame(frameImage);
                            fos.write(bytes);
                        }

                        offset += bytesRead;
                    } while (offset < packet.getSize());

                    frame++;
                    final int finalFrame = frame - 1;
                    Platform.runLater(() -> outputArea.setText(outputAreaText + "\n\tProcessed Frames: " + finalFrame));
                }
            }

            fos.flush();

            /*
             * Some video decoders will cache video data before they begin decoding, so
             * when you're done, you need to flush them.
             *
             * The convention to flush encoders/decoders in Humble Video is to keep
             * passing in null until incomplete samples or packets are returned.
             */
            do {
                decoder.decode(picture, null, 0);
            } while (picture.isComplete());
        } catch (final IOException e) {
            // todo Alert the user to this issue
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            outputFile.delete();

            e.printStackTrace();
        } finally {
            demuxer.close();
        }

        Platform.runLater(() -> outputArea.appendText("\n\nDecode Operation Completed"));
    }

    /**
     * Decodes a video frame into a set of bytes.
     *
     * @param frameImage
     *          The frame.
     *
     * @return
     *          The bytes.
     *
     * @throws NullPointerException
     *          If the frameImage is null.
     */
    private byte[] decodeFrame(final BufferedImage frameImage) {
        Objects.requireNonNull(frameImage);

        final Dimension blockDimensions = settings.getBlockDimensions().getBlockSize();
        final long totalPixelsPerBlock = blockDimensions.width * blockDimensions.height;

        byte[] bytes = new byte[bytesPerFrame];
        int byteIndex = 0;

        for (int blockY = 0; blockY < numBlockRows; blockY++) {
            for (int blockX = 0; blockX < numBlockColumns; blockX++) {
                byte currentByte = 0;

                /*
                 * Because there could be some discoloration, artifacts, etc... in
                 * a downloaded video, we need to ensure that the current block is
                 * either white, black, or magenta by summing the values of each
                 * pixel in the block, then determining which color the resulting
                 * value is closest to.
                 */
                final int xPosOfBlock = blockX * blockDimensions.width;
                final int yPosOfBlock = blockY * blockDimensions.height;
                long colorSum = 0;

                for (int pixelY = 0 ; pixelY < blockDimensions.height ; pixelY++) {
                    for (int pixelX = 0 ; pixelX < blockDimensions.width ; pixelX++) {
                        final int xPosOfPixel = xPosOfBlock + pixelX;
                        final int yPosOfPixel = yPosOfBlock + pixelY;
                        colorSum += frameImage.getRGB(xPosOfPixel, yPosOfPixel);
                    }
                }

                // BLACK goes from 0 to 4194303, Represents Bit 0
                // TEAL goes from 4194304 to 12582909, Represents No Bit
                // WHITE goes from 12582910 to 16777215, Represents Bit 1
                final long colorAvg = colorSum / totalPixelsPerBlock;

                if (colorAvg <= 4194303) {
                    currentByte >>= 0;
                } else if (colorAvg >= 12582910 ) {
                    currentByte >>= 1;
                } else {
                    // There aren't anymore bits in the video, if we've hit a TEAL
                    // block. So, we need to resize the bytes array.
                    final byte[] oldBytes = bytes;
                    bytes = new byte[byteIndex]; // todo May need to be byteIndex - 1
                    System.arraycopy(oldBytes, 0, bytes, 0, bytes.length);
                    return bytes;
                }

                bytes[byteIndex] = currentByte;
                byteIndex++;
            }
        }

        return bytes;
    }
}
