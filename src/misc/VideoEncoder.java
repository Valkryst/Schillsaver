package misc;

import configuration.Settings;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class VideoEncoder {
    private final Settings settings;

    private final int columns;
    private final int rows;
    private final int bitsPerFrame;

    /**
     * Constructs a new VideoEncoder.
     *
     * @param settings
     *          The settings.
     *
     * @throws IllegalStateException
     *          If there's an issue with the encoding settings.
     */
    public VideoEncoder(final Settings settings) throws IllegalStateException {
        this.settings = settings;

        final FrameDimension frameDimensions = settings.getFrameDimensions();
        final Dimension blockDimensions = settings.getBlockDimensions();

        rows = frameDimensions.getHeight() / blockDimensions.height;
        columns = frameDimensions.getWidth() / blockDimensions.width;
        bitsPerFrame = (columns * rows) / 8;
    }

    public void encode(final File inputFile, final File outputFile) throws IOException, InterruptedException {
        final Muxer muxer = Muxer.make(outputFile.getAbsolutePath(), null, "MP4");
        final MuxerFormat muxerFormat = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodecByName(settings.getCodec());

        final Rational frameRate = settings.getFrameRate().getFrameRate();
        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;

        final Encoder encoder = Encoder.make(codec);
        encoder.setWidth(settings.getFrameDimensions().getWidth());
        encoder.setHeight(settings.getFrameDimensions().getHeight());
        encoder.setPixelFormat(pixelFormat);
        encoder.setTimeBase(frameRate);

        // Some formats require a global, rather than per-stream, header.
        if (muxerFormat.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);

        // Java uses some variant of RGB color encoding, but most videos
        // use some variant of YCrCb encoding. We'll need a converter
        // to deal with the difference.
        MediaPictureConverter converter = null;
        final MediaPicture picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelFormat);
        picture.setTimeBase(frameRate);

        try (
            final FileInputStream fis = new FileInputStream(inputFile);
        ) {
            final MediaPacket packet = MediaPacket.make();

            final byte[] buffer = new byte[bitsPerFrame];

            int frame = 0;

            while (fis.read(buffer) != -1) {
                final BufferedImage image = encodeFrame(buffer);

                if (converter == null) {
                    converter = MediaPictureConverterFactory.createConverter(image, picture);
                }

                converter.toPicture(picture, image, frame);

                do {
                    encoder.encode(packet, picture);

                    if (packet.isComplete()) {
                        muxer.write(packet, false);
                    }
                } while (packet.isComplete());

                frame++;
            }

            /*
             * Encoders, like decoders, sometimes cache pictures, so it can
             * do the right key-frame optimizations.
             *
             * So, they need to be flushed as well. As with the decoders,
             * the convention is to pass in a null input until the output
             * is not complete.
             */
            do {
                encoder.encode(packet, null);

                if (packet.isComplete()) {
                    muxer.write(packet, false);
                }
            } while (packet.isComplete());
        } catch (final IOException e) {
            // todo Alert the user to this issue
            final Logger logger = LogManager.getLogger();
            logger.error(e);

            outputFile.delete();

            e.printStackTrace();
        } finally {
            muxer.close();
        }
    }

    /**
     * Creates a video frame to represent a set of bits as black and white
     * blocks on the frame.
     *
     * @param bytes
     *          The bytes.
     *
     * @return
     *          The frame.
     */
    private BufferedImage encodeFrame(byte[] bytes) {
        if (bytes.length > bitsPerFrame) {
            throw new IllegalStateException("You cannot create a frame with more than " + bitsPerFrame + " bits.");
        } else {
            // When we hit the last frame, there won't be enough bits to
            // fill the entire frame with blocks, so we'll fill the rest of
            // the array with -1 for all the missing bits.
            final byte[] newBits = new byte[bitsPerFrame];

            Arrays.fill(newBits, (byte) -1);
            System.arraycopy(bytes, 0, newBits, 0, bytes.length);

            bytes = newBits;
        }

        final FrameDimension frameDimensions = settings.getFrameDimensions();
        final Dimension blockDimensions = settings.getBlockDimensions();

        final BufferedImage image = new BufferedImage(frameDimensions.getWidth(), frameDimensions.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        final Graphics2D gc = (Graphics2D) image.getGraphics();
        int byteIndex = 0;
        int bitIndex = 0;

        for (int y = 0 ; y < rows ; y++) {
            for (int x = 0 ; x < columns ; x++) {
                final int xPos = x * blockDimensions.width;
                final int yPos = y * blockDimensions.height;
                final int bit = getBitAt(bytes[byteIndex], bitIndex);

                if (bit == 0) {
                    gc.setColor(Color.BLACK);
                } else if (bit == 1) {
                    gc.setColor(Color.WHITE);
                } else {
                    gc.setColor(Color.MAGENTA);
                }

                gc.fillRect(xPos, yPos, blockDimensions.width, blockDimensions.height);

                if (bitIndex == 7) {
                    byteIndex++;
                    bitIndex = 0;
                } else {
                    bitIndex++;
                }
            }
        }

        gc.dispose();

        return image;
    }

    /**
     * Retrieves the value of a bit at a specific position in a byte.
     *
     * @param byteVal
     *          The byte.
     *
     * @param position
     *          The position of the bit to retrieve.
     *
     * @return
     *          The bit.
     */
    private int getBitAt(final byte byteVal, final int position) {
        return (byteVal >> position) & 1;
    }
}
