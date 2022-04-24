package com.macbackpackers.beans;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "image")
public class ImageProcessingProperties {

    @NotBlank
    private String watermarkFile;
    @NotBlank
    private String sourceUrl;
    @NotBlank
    private String destinationFolder;
    @NotBlank
    private String destinationFilePrefix;
    @NotBlank
    private String destinationFormat;
    private String destinationThumbnailFilePrefix;
    private String destinationThumbnailFormat;
    @NotBlank
    private float alpha;
    private Integer targetSize = null;
    private int thumbnailTargetSize = 100;
    private int pixelsFromLeftEdge = 0;
    private int pixelsFromBottomEdge = 0;

    public String getWatermarkFile() {
        return watermarkFile;
    }

    public void setWatermarkFile(String watermarkFile) {
        this.watermarkFile = watermarkFile;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public String getDestinationFilePrefix() {
        return destinationFilePrefix;
    }

    public void setDestinationFilePrefix(String destinationFilePrefix) {
        this.destinationFilePrefix = destinationFilePrefix;
    }

    public String getDestinationFormat() {
        return destinationFormat;
    }

    public void setDestinationFormat(String destinationFormat) {
        this.destinationFormat = destinationFormat;
    }

    public String getDestinationThumbnailFilePrefix() {
        return destinationThumbnailFilePrefix;
    }

    public void setDestinationThumbnailFilePrefix(String destinationThumbnailFilePrefix) {
        this.destinationThumbnailFilePrefix = destinationThumbnailFilePrefix;
    }

    public String getDestinationThumbnailFormat() {
        return destinationThumbnailFormat;
    }

    public void setDestinationThumbnailFormat(String destinationThumbnailFormat) {
        this.destinationThumbnailFormat = destinationThumbnailFormat;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getPixelsFromLeftEdge() {
        return pixelsFromLeftEdge;
    }

    public void setPixelsFromLeftEdge(int pixelsFromLeftEdge) {
        this.pixelsFromLeftEdge = pixelsFromLeftEdge;
    }

    public int getPixelsFromBottomEdge() {
        return pixelsFromBottomEdge;
    }

    public void setPixelsFromBottomEdge(int pixelsFromBottomEdge) {
        this.pixelsFromBottomEdge = pixelsFromBottomEdge;
    }

    public Integer getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(Integer targetSize) {
        this.targetSize = targetSize;
    }

    public int getThumbnailTargetSize() {
        return thumbnailTargetSize;
    }

    public void setThumbnailTargetSize(int thumbnailTargetSize) {
        this.thumbnailTargetSize = thumbnailTargetSize;
    }

    /**
     * Returns the filename of the generated image with the current date time prefixed.
     * @return non-null filename
     */
    public String getDestinationFilename() {
        return getDestinationFolder() + "/"
                + getDestinationFilePrefix() + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
                + "." + getDestinationFormat();
    }

    /**
     * Returns the filename of the thumbnail image.
     * @return non-null filename
     */
    public String getDestinationThumbnailFilename() {
        return getDestinationFolder() + "/" + getDestinationThumbnailFilePrefix() + "." + getDestinationThumbnailFormat();
    }
}
