package com.macbackpackers;

import com.macbackpackers.beans.ImageProcessingProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {WatermarkTool.class, ImageProcessingProperties.class}
)
@EnableAutoConfiguration
public class WatermarkToolTest {
    @Autowired
    WatermarkTool tool;

    @Test
    public void testSaveThumbnail() throws Exception {
        tool.saveImageThumbnail();
    }

    @Test
    public void testSaveImageWithWatermark() throws Exception {
        tool.saveImageWithWatermark();
    }
}