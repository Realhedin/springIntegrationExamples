package com.example.springintegration;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.util.Scanner;

@EnableIntegration
@Configuration
public class SpringIntegrationApplication {
    public String INPUT_DIR = "CHANGE_TO_INPUT_DIR";
    public String OUTPUT_DIR = "CHANGE_TO_OUTPUT_DIR";
    public String FILE_PATTERN = "*.jpg";

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource sourceReader= new FileReadingMessageSource();
        sourceReader.setDirectory(new File(INPUT_DIR));
        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
        return sourceReader;
    }

    @Bean
    @ServiceActivator(inputChannel= "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);
        return handler;
    }



    public static void main(String[] args) {
        AbstractApplicationContext context
                = new AnnotationConfigApplicationContext(SpringIntegrationApplication.class);
        context.registerShutdownHook();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter q and press <enter> to exit the program: ");

        while (true) {
            String input = scanner.nextLine();
            if("q".equals(input.trim())) {
                break;
            }
        }
        System.exit(0);

    }

}
