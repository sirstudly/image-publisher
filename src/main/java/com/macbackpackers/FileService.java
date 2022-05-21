package com.macbackpackers;

import com.macbackpackers.beans.ImageProcessingProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class FileService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ImageProcessingProperties props;

    public FileService(ImageProcessingProperties props) {
        this.props = props;
    }

    public void deleteOldFiles() {
        if (props.getNumberOfDaysToKeepFiles() != null) {
            Date oldestAllowedFileDate = DateUtils.addDays(new Date(), -1 * props.getNumberOfDaysToKeepFiles());
            File targetDir = new File(props.getDestinationFolder());
            FileUtils.listFiles(targetDir, new AgeFileFilter(oldestAllowedFileDate), null)
                    .stream()
                    .peek(f -> LOGGER.info("Deleting file: " + f.getName()))
                    .forEach(f -> f.delete());
        }
    }
}
