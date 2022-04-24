package com.macbackpackers;

import com.macbackpackers.beans.SftpTransferProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {RemoteSftpTransferTool.class, SftpTransferProperties.class}
)
@EnableAutoConfiguration
public class RemoteSftpTransferToolTest {
    @Autowired
    RemoteSftpTransferTool tool;

    @Test
    public void testCopyFile() throws Exception {
        tool.copyFile(new File("images/roofcam_20220423_220632.png"));
    }

    @Test
    public void testRunRemoteCommand() throws Exception {
        tool.runRemoteCommand("ls -lsah");
    }
}