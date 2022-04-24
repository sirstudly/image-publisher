package com.macbackpackers;

import com.macbackpackers.beans.ImageProcessingProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;

@Component
public class WatermarkTool {
    private final ImageProcessingProperties props;
    private BufferedImage sourceImage;

    public WatermarkTool(ImageProcessingProperties props) {
        this.props = props;
    }

    private BufferedImage getSourceImage() throws IOException {
        if (this.sourceImage == null) {
            this.sourceImage = ImageIO.read(Channels.newInputStream(
                    Channels.newChannel(new URL(props.getSourceUrl()).openStream())));
        }
        return this.sourceImage;
    }

    /**
     * Embeds an image watermark over a source image to produce
     * a watermarked one.
     * @throws IOException on image processing error
     */
    public File saveImageWithWatermark() throws IOException {
        BufferedImage sourceImage = getSourceImage();
        BufferedImage watermarkImage = ImageIO.read(new File(props.getWatermarkFile()));

        // adjust size of watermark image if applicable
        if(props.getTargetSize() != null) {
            watermarkImage = resize(watermarkImage, props.getTargetSize());
        }

        // initializes necessary graphic properties
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, props.getAlpha());
        g2d.setComposite(alphaChannel);

        // calculates the coordinate where the image is painted
        int topLeftX = props.getPixelsFromLeftEdge();
        int topLeftY = sourceImage.getHeight() - watermarkImage.getHeight() - props.getPixelsFromBottomEdge();

        // paints the image watermark
        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

        if ("bottom".equals(props.getShowTimestamp())) {
            g2d.setFont(new Font(props.getTimestampFont(), Font.BOLD, 12));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            String text = props.getCurrentTimestamp();
            Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);

            // calculates the coordinate where the String is painted
            int centerX = (sourceImage.getWidth() - (int) rect.getWidth()) / 2;
            int centerY = sourceImage.getHeight() - (int) rect.getHeight();

            // paints the textual watermark
            g2d.drawString(text, centerX, centerY);
        }

        File destinationFile = new File(props.getDestinationFilename());
        ImageIO.write(sourceImage, props.getDestinationFormat(), destinationFile);
        g2d.dispose();
        return destinationFile;
    }

    /**
     * Saves a resized image of the source file without a watermark.
     * @return saved thumbnail file or null if thumbnailFilePrefix is not defined
     * @throws IOException on save error
     */
    public File saveImageThumbnail() throws IOException {
        if (StringUtils.isNotBlank(props.getDestinationThumbnailFilePrefix())) {
            BufferedImage sourceImage = getSourceImage();
            BufferedImage thumbnailImage = resize(sourceImage, props.getThumbnailTargetSize());
            File destinationFile = new File(props.getDestinationThumbnailFilename());
            ImageIO.write(thumbnailImage, props.getDestinationThumbnailFormat(), destinationFile);
            return destinationFile;
        }
        return null;
    }

    /**
     * Takes a BufferedImage and resizes it according to the provided targetSize
     *
     * @param src the source BufferedImage
     * @param targetSize maximum height (if portrait) or width (if landscape)
     * @return a resized version of the provided BufferedImage
     */
    private BufferedImage resize(BufferedImage src, int targetSize) {
        if (targetSize <= 0) {
            return src; //this can't be resized
        }
        int targetWidth = targetSize;
        int targetHeight = targetSize;
        float ratio = ((float) src.getHeight() / (float) src.getWidth());
        if (ratio <= 1) { //square or landscape-oriented image
            targetHeight = (int) Math.ceil((float) targetWidth * ratio);
        } else { //portrait image
            targetWidth = Math.round((float) targetHeight / ratio);
        }
        BufferedImage bi = new BufferedImage(targetWidth, targetHeight, src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //produces a balanced resizing (fast and decent quality)
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return bi;
    }
}
