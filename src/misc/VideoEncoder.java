package misc;

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
    private final Dimension blockDimensions;
    private final FrameDimension frameDimensions;
    private final int columns;
    private final int rows;
    private final int bitsPerFrame;
    private final int framerate;

    public static void main(String[] args) throws IOException, InterruptedException {
        final VideoEncoder encoder = new VideoEncoder();
        encoder.encode(new File("20MB.zip"), new File("20MB.mp4"));
    }

    public VideoEncoder() {
        blockDimensions = new Dimension(8, 8);
        frameDimensions = FrameDimension.P720;

        // Check if the widths are evenly divisible:
        double divisionRemainder = frameDimensions.getWidth() % blockDimensions.width;

        if (divisionRemainder != 0) {
            throw new IllegalStateException("The frame height must be evenly divisible by the block height.");
        }

        // Check if the heights are evenly divisible:
        divisionRemainder = frameDimensions.getHeight() % blockDimensions.height;

        if (divisionRemainder != 0) {
            throw new IllegalStateException("The frame width must be evenly divisible by the block width.");
        }

        // Calculate rows/columns:
        rows = frameDimensions.getHeight() / blockDimensions.height;
        columns = frameDimensions.getWidth() / blockDimensions.width;

        bitsPerFrame = (columns * rows) / 8;
        framerate = 30;
    }

    public void encode(final File inputFile, final File outputFile) throws IOException, InterruptedException {
        final Muxer muxer = Muxer.make(outputFile.getAbsolutePath(), null, "MP4");
        final MuxerFormat muxerFormat = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodecByName("libx264");

        final Rational framerate = Rational.make(1, this.framerate);
        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;

        final Encoder encoder = Encoder.make(codec);
        encoder.setWidth(frameDimensions.getWidth());
        encoder.setHeight(frameDimensions.getHeight());
        encoder.setPixelFormat(pixelFormat);
        encoder.setTimeBase(framerate);

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
        picture.setTimeBase(framerate);

        try (
            final FileInputStream fis = new FileInputStream(inputFile);
        ) {
            final MediaPacket packet = MediaPacket.make();

            final byte[] buffer = new byte[bitsPerFrame];

            int frame = 0;

            while (fis.read(buffer) != -1) {
                final BufferedImage image = createFrame(buffer);

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
    private BufferedImage createFrame(byte[] bytes) {
        final long start = System.currentTimeMillis();

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
        System.out.println(System.currentTimeMillis() - start);

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
