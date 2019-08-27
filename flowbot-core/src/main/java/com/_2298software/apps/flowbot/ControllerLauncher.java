package com._2298software.apps.flowbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ControllerLauncher {
    static Logger logger = Logger.getLogger(ControllerLauncher.class.getName());

    public static void main(String args[]) throws Exception {
        logger.info("Starting Flowbot ControllerLauncher");

        logger.info("Loading ControllerLauncher Configuration");

        File configFile = new File(args[0]);
        if (!configFile.exists()) {
            printUsage();
            System.exit(1);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Controller controller = mapper.readValue(configFile, Controller.class);
        logger.info(ReflectionToStringBuilder.toString(controller, ToStringStyle.MULTI_LINE_STYLE));

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(controller.getControllerMaxThreads());


        while(true) {
            File[] configFiles = new File(controller.getConfDir()).listFiles();
            if(configFiles.length == 0){
                logger.info("Controller does not have any files to process, backing-off.");
                Thread.sleep(controller.getControllerLoopThrottleMills());
                continue;
            }

            logger.info("Controller is sleeping");
            Thread.sleep(controller.getControllerLoopThrottleMills());

            for(File f : configFiles){
                if(f.getName().endsWith(".yaml") && !f.getName().equals("controller.yaml")){
                    controller.setConfFile(f.getAbsolutePath());
                    logger.info("Executing controller for " + f.getAbsolutePath());
                    executor.submit(controller);
                    logger.info("Current threadpool activeCount is " + executor.getActiveCount());
                    logger.info("Current threadpool poolSize is " + executor.getPoolSize());
                }
            }
        }

        //executor.shutdown();
    }

    private static void printUsage() {
        logger.info("Failed to start Flowbot. Please provide the configuration yaml.");
        logger.info("java flowbot.jar configFile.yaml");
    }

}
