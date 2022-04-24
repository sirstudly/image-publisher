package com.macbackpackers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.Arrays;

@SpringBootApplication
public class Application {

    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            LOGGER.info("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                LOGGER.debug(beanName);
            }

            // add the watermark
            WatermarkTool watermarkTool = ctx.getBean(WatermarkTool.class);

            // transfer the watermarked file over to the remote server
            RemoteSftpTransferTool transferTool = ctx.getBean(RemoteSftpTransferTool.class);
            File watermarkedImage = watermarkTool.saveImageWithWatermark();
            transferTool.copyFile(watermarkedImage);
            transferTool.copyFile(watermarkTool.saveImageThumbnail());

            // run remote command on the image file we just created
            transferTool.runRemoteCommand(watermarkedImage);

            ((ConfigurableApplicationContext) ctx).close();
        };
    }

}
