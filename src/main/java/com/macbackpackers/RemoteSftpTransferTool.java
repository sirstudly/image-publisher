package com.macbackpackers;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.macbackpackers.beans.SftpTransferProperties;
import io.micrometer.core.instrument.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Component
public class RemoteSftpTransferTool {
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );
    private final SftpTransferProperties props;

    public RemoteSftpTransferTool(SftpTransferProperties props) {
        this.props = props;
    }

    public void copyFile(File fileToTransfer) throws FileSystemException {
        try(StandardFileSystemManager sysManager = new StandardFileSystemManager()) {
            sysManager.init();
            FileObject localFile = sysManager.resolveFile(fileToTransfer.getAbsolutePath());
            String connectionString = props.getConnectionString();
            connectionString += (connectionString.endsWith("/") ? "" : "/") + fileToTransfer.getName();
            FileObject remoteFile = sysManager.resolveFile(
                    connectionString, createFileSystemOptions());

            //Selectors.SELECT_FILES --> A FileSelector that selects only the base file/folder.
            remoteFile.copyFrom(localFile, Selectors.SELECT_FILES);
        }
        // The following is a normal response when closing the connection and can be safely ignored
        // SftpClientFactory:84 - Caught an exception, leaving main loop due to Socket closed
    }

    private FileSystemOptions createFileSystemOptions() throws FileSystemException {
        FileSystemOptions options = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
        SftpFileSystemConfigBuilder.getInstance().setSessionTimeout(options, Duration.ofMillis(props.getTimeoutMillis()));

        if (StringUtils.isNotBlank(props.getIdentityFile())) {
            SftpFileSystemConfigBuilder.getInstance().setIdentityProvider(options,
                    StringUtils.isNotBlank(props.getPassphrase()) ?
                            new IdentityInfo(new File(props.getIdentityFile()), props.getPassphrase().getBytes())
                            : new IdentityInfo(new File(props.getIdentityFile())));
        }
        return options;
    }

    /**
     * Executes the remote command on the given input file.
     * @param inputFile the image file that we just uploaded
     * @throws JSchException
     * @throws IOException
     */
    public void runRemoteCommand(File inputFile) throws JSchException, IOException {
        runRemoteCommand(String.format(props.getRemoteCommand(), inputFile.getName()));
    }

    public void runRemoteCommand(String command) throws JSchException, IOException {
        Session jschSession = null;

        try {
            JSch jsch = new JSch();
            jschSession = jsch.getSession(props.getUser(), props.getHost(), props.getPort());
            jschSession.setConfig("StrictHostKeyChecking", "no");

            // set authentication
            if (StringUtils.isNotBlank(props.getPassword())) {
                jschSession.setPassword(props.getPassword());
            }
            if (StringUtils.isNotBlank(props.getIdentityFile())) {
                jsch.addIdentity(props.getIdentityFile());
            }
            jschSession.connect(props.getTimeoutMillis());

            ChannelExec channelExec = (ChannelExec) jschSession.openChannel("exec");
            channelExec.setCommand(command);

            // display errors to System.err
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();
            channelExec.connect(props.getTimeoutMillis());

            LOGGER.info(IOUtils.toString(in));
            if (channelExec.isClosed()) {
                LOGGER.info("exit-status: " + channelExec.getExitStatus());
            }
            channelExec.disconnect();

        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }
}
